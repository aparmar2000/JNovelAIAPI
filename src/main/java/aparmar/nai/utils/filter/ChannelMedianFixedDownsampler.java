package aparmar.nai.utils.filter;

import java.util.Arrays;

import com.google.common.math.Quantiles;

import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelMedianFixedDownsampler implements IFixedSizeDownsampler {
	protected float alphaThreshold = 0.0f;

	@Override
	public void downsampleTo(Planar<GrayF32> input, int x0, int y0, int x1, int y1, Planar<GrayF32> out, int outIndex) {
		Planar<GrayF32> region = input.subimage(x0, y0, x1, y1);
		int width = region.getWidth();
		int height = region.getHeight();
		int totalPixels = width * height;

		if (totalPixels == 0) {
			return;
		}

		int numBands = region.getNumBands();
		boolean hasAlpha = (numBands == 4);

		if (totalPixels == 1) {
			for (int b = 0; b < numBands; b++) {
				out.getBand(b).data[outIndex] = region.getBand(b).get(0, 0);
			}
			return;
		}

		if (hasAlpha) {
			GrayF32 bandR = region.getBand(0);
			GrayF32 bandG = region.getBand(1);
			GrayF32 bandB = region.getBand(2);
			GrayF32 bandAlpha = region.getBand(3);

			double[] samplesR = new double[totalPixels];
			double[] samplesG = new double[totalPixels];
			double[] samplesB = new double[totalPixels];
			double[] samplesAlpha = new double[totalPixels];

			int validCount = 0;
			int allIdx = 0;

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					float a = bandAlpha.get(x, y);
					samplesAlpha[allIdx++] = a;

					if (a > alphaThreshold) {
						samplesR[validCount] = bandR.get(x, y);
						samplesG[validCount] = bandG.get(x, y);
						samplesB[validCount] = bandB.get(x, y);
						validCount++;
					}
				}
			}

			float outAlpha = (float) Math.rint(Quantiles.median().compute(samplesAlpha));
			if (outAlpha <= 0.0f || validCount == 0) {
				for (int b = 0; b < numBands; b++) {
					out.getBand(b).data[outIndex] = 0.0f;
				}
				return;
			}

			samplesR = Arrays.copyOf(samplesR, validCount);
			samplesG = Arrays.copyOf(samplesG, validCount);
			samplesB = Arrays.copyOf(samplesB, validCount);

			out.getBand(0).data[outIndex] = (float) Math.rint(Quantiles.median().compute(samplesR));
			out.getBand(1).data[outIndex] = (float) Math.rint(Quantiles.median().compute(samplesG));
			out.getBand(2).data[outIndex] = (float) Math.rint(Quantiles.median().compute(samplesB));
			out.getBand(3).data[outIndex] = outAlpha;
		} else {
			double[] samples = new double[totalPixels];
			for (int b = 0; b < numBands; b++) {
				GrayF32 band = region.getBand(b);
				int idx = 0;
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						samples[idx++] = band.get(x, y);
					}
				}
				out.getBand(b).data[outIndex] = (float) Math.rint(Quantiles.median().compute(samples));
			}
		}
	}
}
