package aparmar.nai.utils.filter;

import java.util.Arrays;

import com.google.common.math.Quantiles;

import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public abstract class DualImageFixedDownsampler implements IFixedSizeDownsampler {
	protected final Planar<GrayF32> secondaryImage;
	
	public void downsampleTo(Planar<GrayF32> input, int x0, int y0, int x1, int y1, Planar<GrayF32> out, int outIndex) {
		if (!input.isSameShape(secondaryImage)) {
			throw new IllegalArgumentException(String.format(
						"Expected input and secondary images to have the same shape, but were (%d,%d,%d) and (%d,%d,%d)", 
						input.height, input.width, input.getNumBands(), secondaryImage.height, secondaryImage.width, secondaryImage.getNumBands()
					));
		}
		
		Planar<GrayF32> primaryRegion = input.subimage(x0, y0, x1, y1);
		Planar<GrayF32> secondaryRegion = secondaryImage.subimage(x0, y0, x1, y1);
		downsampleTo(primaryRegion, secondaryRegion, out, outIndex);
	}
	
	abstract void downsampleTo(Planar<GrayF32> primaryRegion, Planar<GrayF32> secondaryRegion, Planar<GrayF32> out, int outIndex);
	

	@Getter
	@Setter
	public static abstract class DualRgbLabDownsampler extends DualImageFixedDownsampler {
		protected float alphaThreshold;
		
		public DualRgbLabDownsampler(Planar<GrayF32> oklabImage) {
			super(oklabImage);
			alphaThreshold = 0.05f;
		}
	}
	
	public static class MedoidDualRgbLabDownsampler extends DualRgbLabDownsampler {		
		public MedoidDualRgbLabDownsampler(Planar<GrayF32> oklabImage) {
			super(oklabImage);
		}

		@Override
		void downsampleTo(Planar<GrayF32> rgbRegion, Planar<GrayF32> oklabRegion, Planar<GrayF32> out, int outIndex) {
			int width = oklabRegion.getWidth();
			int height = oklabRegion.getHeight();
			int totalPixels = width * height;

			if (totalPixels == 0) {
				return;
			}

			int numBands = rgbRegion.getNumBands();
			boolean hasAlpha = (numBands == 4);

			// Special-casing for 1x1 sub-regions
			if (totalPixels == 1) {
				for (int b = 0; b < numBands; b++) {
					out.getBand(b).data[outIndex] = rgbRegion.getBand(b).get(0, 0);
				}
				return;
			}

			GrayF32 bandL = oklabRegion.getBand(0);
			GrayF32 bandA = oklabRegion.getBand(1);
			GrayF32 bandB = oklabRegion.getBand(2);
			GrayF32 bandAlpha = hasAlpha ? oklabRegion.getBand(3) : null;

			// Collect samples (ignoring transparent pixels when alpha exists)
			double[] labL = new double[totalPixels];
			double[] labA = new double[totalPixels];
			double[] labB = new double[totalPixels];
			int[] pixelIndices = new int[totalPixels]; // Maps valid index back to (y * width + x)

			int validCount = 0;
			int opaqueCount = 0;

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int linearIdx = y * width + x;
					float a = hasAlpha ? bandAlpha.get(x, y) : 1.0f;

					if (a > alphaThreshold) {
						labL[validCount] = bandL.get(x, y);
						labA[validCount] = bandA.get(x, y);
						labB[validCount] = bandB.get(x, y);
						pixelIndices[validCount] = linearIdx;
						
						validCount++;
						opaqueCount++;
					}
				}
			}
			labL = Arrays.copyOf(labL, validCount);
			labA = Arrays.copyOf(labA, validCount);
			labB = Arrays.copyOf(labB, validCount);

			// If the cell is mostly transparent (or completely empty), write transparent pixel
			if (hasAlpha && (opaqueCount <= totalPixels / 2)) {
				for (int b = 0; b < numBands; b++) {
					out.getBand(b).data[outIndex] = 0.0f;
				}
				return;
			}

			float medianL = (float) Quantiles.median().compute(labL);
			float medianA = (float) Quantiles.median().compute(labA);
			float medianB = (float) Quantiles.median().compute(labB);

			// Find candidate pixel closest to median in OkLab space
			int bestCandidateSubIdx = 0;
			float minSqDist = Float.MAX_VALUE;

			for (int i = 0; i < validCount; i++) {
				double dL = labL[i] - medianL;
				double da = labA[i] - medianA;
				double db = labB[i] - medianB;
				double sqDist = dL * dL + da * da + db * db;

				if (sqDist < minSqDist) {
					minSqDist = (float) sqDist;
					bestCandidateSubIdx = i;
				}
			}

			int bestLinearIdx = pixelIndices[bestCandidateSubIdx];
			int bestX = bestLinearIdx % width;
			int bestY = bestLinearIdx / width;

			// Copy matching pixel values to output
			for (int b = 0; b < numBands; b++) {
				out.getBand(b).data[outIndex] = rgbRegion.getBand(b).get(bestX, bestY);
			}
		}
		
	}
}
