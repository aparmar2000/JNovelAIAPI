package aparmar.nai.utils.filter;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.google.common.math.Quantiles;

import boofcv.abst.distort.FDistort;
import boofcv.alg.filter.convolve.GConvolveImageOps;
import boofcv.alg.interpolate.InterpolationType;
import boofcv.alg.misc.PixelMath;
import boofcv.core.image.ConvertImage;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.border.BorderType;
import boofcv.struct.convolve.Kernel1D_F32;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.Planar;
import georegression.struct.affine.Affine2D_F64;
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
		AUTOCORRELATION(s -> estimateStrideAutocorrelation(s, 2, 32)),
		PEAK_MEDIAN(s -> estimateStrideLocalMaxima(s, PeakStatistic.MEDIAN)),
		PEAK_MODE(s -> estimateStrideLocalMaxima(s, PeakStatistic.MODE)),
		PEAK_MEAN(s -> estimateStrideLocalMaxima(s, PeakStatistic.MEAN));
		
		StrideEstimator strideEstimator;

		@Override
		public float estimate(GrayF32 strip) {
			return strideEstimator.estimate(strip);
		}
	}
	
	@NotNull
	@Builder.Default
	private EdgeKernel edgeKernel = EdgeKernel.MEDIUM;
	@NotNull
	@Builder.Default
	private PixelRatioEstimationMode pixelRatioEstimationMode = PixelRatioEstimationMode.PEAK_MEDIAN;
	@Builder.Default
	private boolean performRefinement = true;

	@Override
	public BufferedImage apply(BufferedImage inImage) {
		Planar<GrayF32> rgb = ConvertBufferedImage.convertFromPlanar(inImage, null, true, GrayF32.class);
		Planar<GrayF32> oklab = rgbToOklab(rgb);
		GrayF32 horizontalStrip = convolve1D(oklab, edgeKernel.getEdgeKernel(), true);
		GrayF32 verticalStrip = convolve1D(oklab, edgeKernel.getEdgeKernel(), false);
		
		float horizontalStrideEst = pixelRatioEstimationMode.estimate(horizontalStrip);
		float verticalStrideEst = pixelRatioEstimationMode.estimate(verticalStrip);
		
		int outWidth = (int) (oklab.width/horizontalStrideEst);
		int outHeight = (int) (oklab.height/verticalStrideEst);
		Affine2D_F64 transformation = new Affine2D_F64(horizontalStrideEst, 0, 0, verticalStrideEst, 0.5, 0.5);
		
		Planar<GrayF32> rgbOut = rgb.createNew(outWidth, outHeight);
		FDistort distorter = new FDistort(ImageType.SB_F32);
		distorter.input(rgb);
		distorter.output(rgbOut);
		distorter.interp(InterpolationType.BILINEAR);
		distorter.affine(transformation.invert(null));
		distorter.borderExt();
		distorter.apply();
		
		return ConvertBufferedImage.convertTo_F32(rgbOut, null, true);
	}
	
	protected static GrayF32 convolve1D(Planar<GrayF32> in, Kernel1D_F32 kernel, boolean horizontal) {
		Planar<GrayF32> convolved = in.createSameShape();
		if (horizontal) {
			GConvolveImageOps.horizontal(kernel, in, convolved, BorderType.EXTENDED);
		} else {
			GConvolveImageOps.vertical(kernel, in, convolved, BorderType.EXTENDED);
		}
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
		
		PixelMath.multiply(gradientStripWindowedAverage, BASELINE_SUB_SCALAR, gradientStripWindowedAverage);
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

        // -- Sub-pixel parabolic refinement around the peak:
        // vertex offset = (y0 - y2) / (2 * (2*y1 - y0 - y2))
        if (bestLag > 0 && bestLag < n - 1) {
            float y0 = corr[bestLag - 1];
            float y1 = corr[bestLag];
            float y2 = corr[bestLag + 1];

            float denom = 2.0f * (2.0f * y1 - y0 - y2);
            if (Math.abs(denom) > 1e-6f) {
                return bestLag + (y0 - y2) / denom;
            }
        }

        return (float) bestLag;
    }
	protected static enum PeakStatistic { MEAN, MODE, MEDIAN; }
	protected static float estimateStrideLocalMaxima(GrayF32 image, PeakStatistic peakStatistic) {
		List<Integer> peakDistances = new ArrayList<>(image.width);
		int lastMaximaIdx = -1;
		for (int i = 0; i < image.width-1; i++) {
			float val = image.data[i];
			float nextVal = image.data[i+1];
			if (nextVal < val) {
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
