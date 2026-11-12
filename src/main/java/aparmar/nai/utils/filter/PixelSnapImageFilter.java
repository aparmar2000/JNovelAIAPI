package aparmar.nai.utils.filter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import aparmar.nai.utils.filter.PixelGridAlignSnapImageFilter.DownsamplerFactory;
import aparmar.nai.utils.filter.PixelGridAlignSnapImageFilter.PeakStatistic;
import aparmar.nai.utils.filter.PixelGridAlignSnapImageFilter.StrideEstimator;
import boofcv.struct.convolve.Kernel1D_F32;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class PixelSnapImageFilter implements IImageFilter {
	
	@RequiredArgsConstructor
	@Getter(value = AccessLevel.PROTECTED)
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public static enum EdgeKernel {
		SMALL(PixelGridAlignSnapImageFilter.KERNEL_SMALL),
		MEDIUM(PixelGridAlignSnapImageFilter.KERNEL_MEDIUM);
		
		Kernel1D_F32 edgeKernel;
	}
	
	@RequiredArgsConstructor
	@Getter(value = AccessLevel.PROTECTED)
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public static enum PixelRatioEstimationMode implements StrideEstimator {
		AUTOCORRELATION(s -> PixelGridAlignSnapImageFilter.estimateStrideAutocorrelation(s, 3, 32)),
		PEAK_MEDIAN(s -> PixelGridAlignSnapImageFilter.estimateStrideLocalMaxima(s, PeakStatistic.MEDIAN)),
		PEAK_MODE(s -> PixelGridAlignSnapImageFilter.estimateStrideLocalMaxima(s, PeakStatistic.MODE)),
		PEAK_MEAN(s -> PixelGridAlignSnapImageFilter.estimateStrideLocalMaxima(s, PeakStatistic.MEAN));
		
		StrideEstimator strideEstimator;

		@Override
		public float estimate(GrayF32 strip) {
			return strideEstimator.estimate(strip);
		}
	}
	
	@RequiredArgsConstructor
	@Getter(value = AccessLevel.PROTECTED)
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public static enum DownsamplerMode implements DownsamplerFactory {
		CHANNEL_MEDIAN(oklab -> new ChannelMedianFixedDownsampler()),
		MEDIOID_DUAL(oklab -> new DualImageFixedDownsampler.MedoidDualRgbLabDownsampler(oklab));
		
		Function<Planar<GrayF32>, IFixedSizeDownsampler> downsamplerFactory;

		@Override
		public IFixedSizeDownsampler apply(Planar<GrayF32> t) {
			return downsamplerFactory.apply(t);
		}
	}
	
	@RequiredArgsConstructor
	@Getter(value = AccessLevel.PROTECTED)
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public static enum PalettizeMode implements Supplier<IPalettizeImageFilter> {
		K_MEANS(KMeansPalettizeImageFilter::new),
		SPATIAL_MERGE(SpatialMergePalettizeImageFilter::new),
		SPATIAL_QUANTIZE(SpatialColorQuantizePalettizeImageFilter::new);
		
		Supplier<IPalettizeImageFilter> palettizerFactory;

		@Override
		public IPalettizeImageFilter get() {
			return palettizerFactory.get();
		}

	}

	@Accessors(fluent = true)
	public static abstract class PalettizeTarget {
		@NoArgsConstructor(access = AccessLevel.PRIVATE)
		public static final class NonePalettization extends PalettizeTarget {}
		@Value
		@EqualsAndHashCode(callSuper = false)
		@AllArgsConstructor(access = AccessLevel.PRIVATE)
		public static final class AutoPalettization extends PalettizeTarget {
			float thresholdTarget;
		}
		@Value
		@EqualsAndHashCode(callSuper = false)
		@AllArgsConstructor(access = AccessLevel.PRIVATE)
		public static final class CustomPalettization extends PalettizeTarget {
			int targetColors;
		}
		
		@Getter
		private static final NonePalettization none = new NonePalettization();
		@Getter
		private static final AutoPalettization auto = new AutoPalettization(0.01f);

		public static AutoPalettization auto(float thresholdTarget) {
			return new AutoPalettization(thresholdTarget);
		}
		public static CustomPalettization custom(int targetColors) {
			return new CustomPalettization(targetColors);
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

	@Builder.Default
	private PalettizeMode palettizeMode = PalettizeMode.SPATIAL_MERGE;
	@Builder.Default
	private PalettizeTarget palettizeTarget = PalettizeTarget.auto();
	
	public static PixelSnapImageFilter.PixelSnapImageFilterBuilder naiLike() {
		return PixelSnapImageFilter.builder()
				.edgeKernel(EdgeKernel.MEDIUM)
				.pixelRatioEstimationMode(PixelRatioEstimationMode.AUTOCORRELATION)
				.downsamplerMode(DownsamplerMode.CHANNEL_MEDIAN)
				.performRefinement(true)
				.palettizeMode(PalettizeMode.K_MEANS)
				.palettizeTarget(PalettizeTarget.auto());
	}

	protected static final int MAX_AUTO_COLOR_COUNT = 256;

	@Override
	public Planar<GrayF32> applyPlanar(Planar<GrayF32> rgbaIn) {
		Planar<GrayF32> workingBuffer = rgbaIn;
		
		// -- Grid Alignment
		PixelGridAlignSnapImageFilter gridFilter = new PixelGridAlignSnapImageFilter();
		gridFilter.setEdgeKernel(edgeKernel.getEdgeKernel());
		gridFilter.setStrideEstimator(pixelRatioEstimationMode);
		gridFilter.setDownsamplerFactory(downsamplerMode);
		gridFilter.setPerformRefinement(performRefinement);
		
		workingBuffer = gridFilter.applyPlanar(workingBuffer);
		
		// -- Color Quantization
		IImageFilter palettizeFilter = palettizeMode.get();
		if (palettizeTarget instanceof PalettizeTarget.NonePalettization) {
			palettizeFilter = IImageFilter.noOp();
		} else if (palettizeTarget instanceof PalettizeTarget.AutoPalettization) {
			float thresholdTarget = ((PalettizeTarget.AutoPalettization) palettizeTarget).thresholdTarget();
			
			if (palettizeFilter instanceof ISimilarityThresholdPalettizeImageFilter) {
				((ISimilarityThresholdPalettizeImageFilter) palettizeFilter).setSimilarityThreshold(thresholdTarget);
			} else if (palettizeFilter instanceof IColorCountPalettizeImageFilter) {
				int estimatedColors = estimateAutoColorCount(workingBuffer, thresholdTarget);
				((IColorCountPalettizeImageFilter) palettizeFilter).setColorCount(estimatedColors);
			}
		} else if (palettizeTarget instanceof PalettizeTarget.CustomPalettization) {
			if (palettizeFilter instanceof IColorCountPalettizeImageFilter) {
				((IColorCountPalettizeImageFilter) palettizeFilter).setColorCount( ((PalettizeTarget.CustomPalettization) palettizeTarget).targetColors() );
			} else {
				throw new IllegalArgumentException(String.format("Palettize mode %s does not support custom palettization!", palettizeMode));
			}
		}
		
		workingBuffer = palettizeFilter.applyPlanar(workingBuffer);
		
		return workingBuffer;
	}

	/**
	 * Estimates an appropriate palette size for {@link IColorCountPalettizeImageFilter}
	 * by counting perceptually distinct color clusters in OkLab space separated by at least
	 * {@code thresholdTarget}.
	 */
	protected static int estimateAutoColorCount(Planar<GrayF32> rgbaIn, float thresholdTarget) {
		float effectiveThreshold = Math.max(thresholdTarget, 1e-4f);
		double quantStep = Math.max(effectiveThreshold * 0.25, 1e-4);

		Planar<GrayF32> oklab = ImageFilterUtils.rgbToOklab(rgbaIn, null);
		GrayF32 L = oklab.getBand(0);
		GrayF32 a = oklab.getBand(1);
		GrayF32 b = oklab.getBand(2);
		GrayF32 alpha = oklab.getNumBands() == 4 ? oklab.getBand(3) : null;

		Map<QuantizedOkLabKey, ColorBucket> buckets = new HashMap<>();
		for (int row = 0; row < oklab.height; row++) {
			int idx = L.startIndex + row * L.stride;
			for (int col = 0; col < oklab.width; col++, idx++) {
				if (alpha != null && alpha.data[idx] < ImageFilterUtils.DEFAULT_ALPHA_CLIP_THRESHOLD) {
					continue;
				}

				float lVal = L.data[idx];
				float aVal = a.data[idx];
				float bVal = b.data[idx];
				QuantizedOkLabKey key = new QuantizedOkLabKey(
						Math.round(lVal / quantStep),
						Math.round(aVal / quantStep),
						Math.round(bVal / quantStep));

				buckets.computeIfAbsent(key, k -> new ColorBucket()).add(lVal, aVal, bVal);
			}
		}

		if (buckets.isEmpty()) {
			return 1;
		}

		List<ColorBucket> sortedBuckets = new ArrayList<>(buckets.values());
		sortedBuckets.sort(Comparator.comparingInt((ColorBucket bucket) -> bucket.count).reversed());

		float thresholdSq = effectiveThreshold * effectiveThreshold;
		List<float[]> clusterCenters = new ArrayList<>();
		for (ColorBucket bucket : sortedBuckets) {
			float[] centroid = bucket.centroid();
			boolean withinExisting = false;
			for (float[] center : clusterCenters) {
				float dl = centroid[0] - center[0];
				float da = centroid[1] - center[1];
				float db = centroid[2] - center[2];
				if (dl * dl + da * da + db * db <= thresholdSq) {
					withinExisting = true;
					break;
				}
			}

			if (!withinExisting) {
				clusterCenters.add(centroid);
				if (clusterCenters.size() >= MAX_AUTO_COLOR_COUNT) {
					break;
				}
			}
		}

		return Math.max(1, clusterCenters.size());
	}

	@Value
	private static class QuantizedOkLabKey {
		long l;
		long a;
		long b;
	}

	private static class ColorBucket {
		double lSum;
		double aSum;
		double bSum;
		int count;

		void add(float l, float a, float b) {
			lSum += l;
			aSum += a;
			bSum += b;
			count++;
		}

		float[] centroid() {
			return new float[] {
					(float) (lSum / count),
					(float) (aSum / count),
					(float) (bSum / count)
			};
		}
	}

}
