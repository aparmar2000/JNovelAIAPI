package aparmar.nai.utils.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;

import com.google.common.math.Quantiles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;

import boofcv.alg.filter.convolve.GConvolveImageOps;
import boofcv.alg.misc.GPixelMath;
import boofcv.alg.misc.PixelMath;
import boofcv.core.image.ConvertImage;
import boofcv.struct.border.BorderType;
import boofcv.struct.convolve.Kernel1D_F32;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static aparmar.nai.utils.filter.ImageFilterUtils.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class PixelSnapImageFilter implements IImageFilter {
	protected static final Kernel1D_F32 KERNEL_SMALL = makeSmallKernel();
	protected static final Kernel1D_F32 KERNEL_MEDIUM = makeMediumKernel();
	
	protected static final float BASELINE_SUB_SCALAR = 0.7f;
	
	protected static final float DEFAULT_MIN_STRIDE_FACTOR = 0.7f;
	protected static final float DEFAULT_MAX_STRIDE_FACTOR = 1.45f;
	protected static final float DEFAULT_PENALTY_FACTOR = 1.5f;
	
	@RequiredArgsConstructor
	@Getter(value = AccessLevel.PROTECTED)
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public static enum EdgeKernel {
		SMALL(KERNEL_SMALL),
		MEDIUM(KERNEL_MEDIUM);
		
		Kernel1D_F32 edgeKernel;
	}
	
	@FunctionalInterface
	protected static interface StrideEstimator { float estimate(GrayF32 strip); }
	@RequiredArgsConstructor
	@Getter(value = AccessLevel.PROTECTED)
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public static enum PixelRatioEstimationMode implements StrideEstimator {
		AUTOCORRELATION(s -> estimateStrideAutocorrelation(s, 3, 32)),
		PEAK_MEDIAN(s -> estimateStrideLocalMaxima(s, PeakStatistic.MEDIAN)),
		PEAK_MODE(s -> estimateStrideLocalMaxima(s, PeakStatistic.MODE)),
		PEAK_MEAN(s -> estimateStrideLocalMaxima(s, PeakStatistic.MEAN));
		
		StrideEstimator strideEstimator;

		@Override
		public float estimate(GrayF32 strip) {
			return strideEstimator.estimate(strip);
		}
	}
	
	@RequiredArgsConstructor
	@Getter(value = AccessLevel.PROTECTED)
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public static enum DownsamplerMode implements Function<Planar<GrayF32>, IFixedSizeDownsampler> {
		CHANNEL_MEDIAN(oklab -> new ChannelMedianFixedDownsampler()),
		MEDIOID_DUAL(oklab -> new DualImageFixedDownsampler.MedoidDualRgbLabDownsampler(oklab));
		
		Function<Planar<GrayF32>, IFixedSizeDownsampler> downsamplerFactory;

		@Override
		public IFixedSizeDownsampler apply(Planar<GrayF32> t) {
			return downsamplerFactory.apply(t);
		}
	}
	
	@NotNull
	@Builder.Default
	private EdgeKernel edgeKernel = EdgeKernel.MEDIUM;
	@NotNull
	@Builder.Default
	private PixelRatioEstimationMode pixelRatioEstimationMode = PixelRatioEstimationMode.AUTOCORRELATION;
	@NotNull
	@Builder.Default
	private DownsamplerMode downsamplerMode = DownsamplerMode.CHANNEL_MEDIAN;
	@Builder.Default
	private boolean performRefinement = true;

	@Override
	public Planar<GrayF32> applyPlanar(Planar<GrayF32> rgba) {
		Planar<GrayF32> oklab = rgbToOklab(rgba);
		GrayF32 horizontalStrip = convolve1D(oklab, edgeKernel.getEdgeKernel(), true, BASELINE_SUB_SCALAR);
		GrayF32 verticalStrip = convolve1D(oklab, edgeKernel.getEdgeKernel(), false, BASELINE_SUB_SCALAR);
		
		float horizontalStrideEst = pixelRatioEstimationMode.estimate(horizontalStrip);
		float verticalStrideEst = pixelRatioEstimationMode.estimate(verticalStrip);

		int[] horizontalCuts = solveGridCuts(horizontalStrip, horizontalStrideEst);
		int[] verticalCuts = solveGridCuts(verticalStrip, verticalStrideEst);
		
		if (performRefinement && (horizontalStrideEst > 2.2f || verticalStrideEst > 2.2f)) {
			float finerHStride = Math.max(2.0f, horizontalStrideEst / 2.0f);
			float finerVStride = Math.max(2.0f, verticalStrideEst / 2.0f);
			int[] finerHCuts = solveGridCuts(horizontalStrip, finerHStride);
			int[] finerVCuts = solveGridCuts(verticalStrip, finerVStride);

			double baseVar = computeMeanCellVariance(oklab, horizontalCuts, verticalCuts);
			double finerVar = computeMeanCellVariance(oklab, finerHCuts, finerVCuts);
			if (baseVar > 1e-4 && finerVar < baseVar * 0.75) {
				horizontalStrideEst = finerHStride;
				verticalStrideEst = finerVStride;
				horizontalCuts = finerHCuts;
				verticalCuts = finerVCuts;
			}
		}
		
		Planar<GrayF32> downsampledRgb = downsampleByCuts(rgba, horizontalCuts, verticalCuts, downsamplerMode.apply(oklab));
		
		return downsampledRgb;
	}
	
	protected static double computeMeanCellVariance(Planar<GrayF32> oklab, int[] horizontalCuts, int[] verticalCuts) {
		GrayF32 L = oklab.getBand(0);
		GrayF32 a = oklab.getBand(1);
		GrayF32 b = oklab.getBand(2);
		GrayF32 alpha = oklab.getNumBands() == 4 ? oklab.getBand(3) : null;
		double totalVar = 0.0;
		int cellCount = 0;

		for (int r = 0; r < verticalCuts.length - 1; r++) {
			int y0 = verticalCuts[r];
			int y1 = verticalCuts[r + 1];
			for (int c = 0; c < horizontalCuts.length - 1; c++) {
				int x0 = horizontalCuts[c];
				int x1 = horizontalCuts[c + 1];
				int n = (x1 - x0) * (y1 - y0);
				if (n <= 1) {
					continue;
				}

				double mL = 0, ma = 0, mb = 0, mAlpha = 0;
				int opaqueCount = 0;
				for (int y = y0; y < y1; y++) {
					int idx = L.startIndex + y * L.stride + x0;
					for (int x = x0; x < x1; x++, idx++) {
						mL += L.data[idx];
						ma += a.data[idx];
						mb += b.data[idx];
						if (alpha != null) {
							mAlpha += alpha.data[idx];
							if (alpha.data[idx] > 0.05f) {
								opaqueCount++;
							}
						} else {
							opaqueCount++;
						}
					}
				}
				if (opaqueCount == 0) {
					continue;
				}
				mL /= n;
				ma /= n;
				mb /= n;
				mAlpha /= n;

				double var = 0.0;
				for (int y = y0; y < y1; y++) {
					int idx = L.startIndex + y * L.stride + x0;
					for (int x = x0; x < x1; x++, idx++) {
						double dL = L.data[idx] - mL;
						double da = a.data[idx] - ma;
						double db = b.data[idx] - mb;
						double dAlpha = alpha != null ? alpha.data[idx] - mAlpha : 0.0;
						var += dL * dL + da * da + db * db + dAlpha * dAlpha;
					}
				}
				totalVar += var / n;
				cellCount++;
			}
		}
		return cellCount > 0 ? totalVar / cellCount : 0.0;
	}
	
	protected static GrayF32 convolve1D(Planar<GrayF32> in, Kernel1D_F32 kernel, boolean horizontal, float baselineSubScalar) {
		Planar<GrayF32> convolved = in.createSameShape();
		if (horizontal) {
			GConvolveImageOps.horizontal(kernel, in, convolved, BorderType.EXTENDED);
		} else {
			GConvolveImageOps.vertical(kernel, in, convolved, BorderType.EXTENDED);
		}
		GPixelMath.abs(convolved, convolved);
		GrayF32 convolvedAvg = ConvertImage.average(convolved, null);
		
		GrayF32 gradientStrip;
		if (horizontal) {
			gradientStrip = convolvedAvg.createNew(convolvedAvg.width, 1);
			sumColumns(convolvedAvg, gradientStrip);
		} else {
			gradientStrip = convolvedAvg.createNew(1, convolvedAvg.height);
			sumRows(convolvedAvg, gradientStrip);
			gradientStrip.reshape(gradientStrip.height, 1);
		}

		GrayF32 gradientStripWindowedAverage = gradientStrip.createSameShape();
		Kernel1D_F32 avgKernel = makeAveragingKernel(Math.max(5, gradientStrip.width / 20));
		GConvolveImageOps.horizontal(avgKernel, gradientStrip, gradientStripWindowedAverage, BorderType.EXTENDED);
		
		PixelMath.multiply(gradientStripWindowedAverage, baselineSubScalar, gradientStripWindowedAverage);
		PixelMath.subtract(gradientStrip, gradientStripWindowedAverage, gradientStrip);
		PixelMath.boundImage(gradientStrip, 0, Float.MAX_VALUE);
		
		return gradientStrip;
	}
	
	protected static float estimateStrideAutocorrelation(GrayF32 image, int minStride, int maxStride) {
        int n = image.width;
        if (n == 0) {
            return (float) minStride;
        }

        // -- Extract values and mean-center (norm_p = profile - np.mean(profile))
        float[] normP = new float[n];
        double sum = 0.0;
        
        for (int i = 0; i < n; i++) {
            float val = image.data[i];
            normP[i] = val;
            sum += val;
        }

        float mean = (float) (sum / n);
        for (int i = 0; i < n; i++) {
            normP[i] -= mean;
        }

        // -- Compute non-negative lags: corr[lag] = sum_{i=0}^{n - 1 - lag} normP[i] * normP[i + lag]
        int computeLimit = Math.min(n, maxStride + 2);
        float[] corr = new float[computeLimit];

        for (int lag = 0; lag < computeLimit; lag++) {
            double dot = 0.0;
            int limit = n - lag;
            for (int i = 0; i < limit; i++) {
                dot += normP[i] * normP[i + lag];
            }
            corr[lag] = (float) dot;
        }

        // -- Search for the highest autocorrelation peak within bounds [minStride, maxStride)
        int searchEnd = Math.min(maxStride, n);
        if (minStride >= searchEnd) {
            return (float) minStride;
        }

        int bestLag = minStride;
        float maxVal = corr[minStride];

        for (int lag = minStride + 1; lag < searchEnd; lag++) {
            if (corr[lag] > maxVal) {
                maxVal = corr[lag];
                bestLag = lag;
            }
        }

        // -- Prefer fundamental sub-harmonic peak when a harmonic multiple (e.g. 2x lag) is slightly higher
        for (int div = bestLag / minStride; div >= 2; div--) {
            int subLag = Math.round((float) bestLag / div);
            if (subLag >= minStride && subLag < searchEnd - 1
                    && corr[subLag] > corr[subLag - 1] && corr[subLag] >= corr[subLag + 1]
                    && corr[subLag] >= maxVal * 0.8f) {
                bestLag = subLag;
                maxVal = corr[subLag];
                break;
            }
        }

        // -- Sub-pixel parabolic refinement around a concave-down local peak:
        // vertex offset = (y0 - y2) / (2 * (2*y1 - y0 - y2))
        if (bestLag > 0 && bestLag < n - 1) {
            float y0 = corr[bestLag - 1];
            float y1 = corr[bestLag];
            float y2 = corr[bestLag + 1];

            if (y1 >= y0 && y1 >= y2) {
                float denom = 2.0f * (2.0f * y1 - y0 - y2);
                if (denom > 1e-6f) {
                    return bestLag + (y0 - y2) / denom;
                }
            }
        }

        return (float) bestLag;
    }
	
	protected static enum PeakStatistic { MEAN, MODE, MEDIAN; }
	protected static float estimateStrideLocalMaxima(GrayF32 image, PeakStatistic peakStatistic) {
		double threshold = Quantiles.percentiles().index(5).compute(Floats.asList(image.data));
		
		List<Integer> peakDistances = new ArrayList<>(image.width);
		int lastMaximaIdx = -1;
		for (int i = 1; i < image.width-1; i++) {
			float prevVal = image.data[i-1];
			float val = image.data[i];
			float nextVal = image.data[i+1];
			if (val > prevVal && val >= nextVal && val > threshold) {
				if (lastMaximaIdx >= 0) {
					peakDistances.add(i - lastMaximaIdx);
				}
				
				lastMaximaIdx = i;
			}
		}
		
		switch (peakStatistic) {
		case MEAN:
			return (float) peakDistances.stream().mapToInt(v->v).average().orElse(1);
		case MEDIAN:
			return (float) Quantiles.median().compute(peakDistances);
		case MODE:
			Map<Integer, Integer> freqMap = new HashMap<>();
			peakDistances.forEach(v->freqMap.merge(v, 1, (a,b)->a+b));
			return freqMap.entrySet()
					.stream()
					.reduce((a,b)->a.getValue()>b.getValue() ? a : b)
					.map(v->v.getKey())
					.orElse(1);
		}
		return 1;
	}
	
	protected static int[] solveGridCuts(GrayF32 profileStrip, float strideEst) {
		return solveGridCuts(profileStrip, strideEst, DEFAULT_MIN_STRIDE_FACTOR, DEFAULT_MAX_STRIDE_FACTOR, DEFAULT_PENALTY_FACTOR);
	}
	protected static int[] solveGridCuts(GrayF32 profileStrip, float strideEst, float minStrideFactor, float maxStrideFactor, float penaltyFac) {
		int n = profileStrip.width;
		
		GrayF32 pNorm = profileStrip.createSameShape();
		PixelMath.divide(profileStrip, (float) (Quantiles.percentiles().index(95).compute(Floats.asList(profileStrip.data)) + 1e-6), pNorm);
		int minStep = Math.max(2, (int) Math.ceil(strideEst * minStrideFactor));
		int maxStep = Math.max(minStep, (int) (strideEst * maxStrideFactor));
		double penaltyWeight = penaltyFac / Math.pow(strideEst, 2);
		
		float[] scores = new float[n];
		Arrays.fill(scores, Float.NEGATIVE_INFINITY);
		int[] parent = new int[n];
		Arrays.fill(parent, -1);
		for (int i=0; i<minStep; i++) {
			scores[i] = pNorm.data[i];
		}
		
		// Perform actual search
		for (int i=minStep; i<n; i++) {
			int stepRangeMin = Math.max(0, i - maxStep);
			int stepRangeMax = i - minStep;
			
			double bestScore = Double.NEGATIVE_INFINITY;
			int bestScoreIdx = -1;
			for (int s=stepRangeMin; s<=stepRangeMax; s++) {
				int stepOffs = i - s;
				double score = scores[s] - penaltyWeight * Math.pow((stepOffs - strideEst), 2);
				
				if (score > bestScore) {
					bestScore = score;
					bestScoreIdx = s;
				}
			}
			
			scores[i] = (float) (pNorm.data[i] + bestScore);
			parent[i] = bestScoreIdx;
		}
		
		// Backtrack to rebuild optimal sequence
		int backtrackIdx = IntStream.range(Math.max(0, n - maxStep), n)
				.reduce((a,b)->scores[a] > scores[b] ? a : b)
				.orElse(n - 1);
		List<Integer> cuts = new ArrayList<>();
		while (backtrackIdx >= 0) {
			cuts.add(backtrackIdx);
			backtrackIdx = parent[backtrackIdx];
		}
		Collections.sort(cuts);
		
		// Final sequence cleanup
		if (cuts.get(0) > strideEst * minStrideFactor) {
			cuts.add(0, 0);
		}
		if (n - cuts.get(cuts.size()-1) > strideEst * minStrideFactor) {
			cuts.add(n);
		} else {
			cuts.set(cuts.size()-1, n);
		}
		
		return Ints.toArray(cuts);
	}
	
	protected static Kernel1D_F32 makeSmallKernel() {
		Kernel1D_F32 kernel = new Kernel1D_F32(2);
		kernel.offset = 1; // specify the kernel's origin
		kernel.data[0] = 1;
		kernel.data[1] = -1;
		
		return kernel;
	}
	
	protected static Kernel1D_F32 makeMediumKernel() {
		Kernel1D_F32 kernel = new Kernel1D_F32(3);
		kernel.offset = 1; // specify the kernel's origin
		kernel.data[0] = 1;
		kernel.data[1] = 0;
		kernel.data[2] = -1;
		
		return kernel;
	}

}
