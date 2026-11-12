package aparmar.nai.utils.filter;

import boofcv.alg.color.ColorXyz;
import boofcv.concurrency.BoofConcurrency;
import boofcv.struct.convolve.Kernel1D_F32;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;

public class ImageFilterUtils {
	public static GrayF32 sumColumns(GrayF32 image, GrayF32 out) {
	    for (int y = 0; y < image.height; y++) {
	        int index = image.startIndex + y * image.stride;
	        
	        for (int x = 0; x < image.width; x++) {
	        	out.data[x] += image.data[index + x];
	        }
	    }
	    return out;
	}
	public static GrayF32 sumRows(GrayF32 image, GrayF32 out) {
	    for (int y = 0; y < image.height; y++) {
	        int index = image.startIndex + y * image.stride;

	        for (int x = 0; x < image.width; x++) {
	        	out.data[y] += image.data[index + x];
	        }
	    }
	    return out;
	}
	
	public static Kernel1D_F32 makeAveragingKernel(int width) {
		Kernel1D_F32 kernel = new Kernel1D_F32(width);
		kernel.offset = width/2;
		
		for (int i=0; i<width; i++) {
			kernel.data[i] = 1f/width;
		}
		
		return kernel;
	}
	
	public static Planar<GrayF32> downsampleByCuts(Planar<GrayF32> in, int[] horizontalCuts, int[] verticalCuts, IFixedSizeDownsampler downsampler) {
		return downsampleByCuts(in, horizontalCuts, verticalCuts, downsampler, 0.2f);
	}
	public static Planar<GrayF32> downsampleByCuts(Planar<GrayF32> in, int[] horizontalCuts, int[] verticalCuts, IFixedSizeDownsampler downsampler, float marginRatio) {
		int width = horizontalCuts.length - 1;
		int height = verticalCuts.length - 1;
		Planar<GrayF32> out = in.createNew(width, height);

		BoofConcurrency.loopFor(0, height, row -> {
			int outIndex = out.startIndex + row * out.stride;
			
			int yRawStart = verticalCuts[row];
			int yRawEnd = verticalCuts[row+1];
			int yMargin = Math.round((yRawEnd - yRawStart) * marginRatio);
			
			float yWidthAfterMargin = (yRawEnd - yRawStart - 2 * yMargin);
			int yAdjStart = yWidthAfterMargin >= 1 ? yRawStart + yMargin : yRawStart;
			int yAdjEnd = yWidthAfterMargin >= 1 ? yRawEnd - yMargin : yRawEnd;

			for (int col = 0; col < width; col++, outIndex++) {
				int xRawStart = horizontalCuts[col];
				int xRawEnd = horizontalCuts[col+1];
				int xMargin = Math.round((xRawEnd - xRawStart) * marginRatio);
				
				float xWidthAfterMargin = (xRawEnd - xRawStart - 2 * xMargin);
				int xAdjStart = xWidthAfterMargin >= 1 ? xRawStart + xMargin : xRawStart;
				int xAdjEnd = xWidthAfterMargin >= 1 ? xRawEnd - xMargin : xRawEnd;
				
				downsampler.downsampleTo(in, xAdjStart, yAdjStart, xAdjEnd, yAdjEnd, out, outIndex);
			}
		});
		
		return out;
	}

	public static final float DEFAULT_ALPHA_CLIP_THRESHOLD = 0.005f;
	/**
	 * Color space conversion from sRGB to OkLab
	 */
	public static Planar<GrayF32> rgbToOklab(Planar<GrayF32> rgb) {
		return rgbToOklab(rgb, DEFAULT_ALPHA_CLIP_THRESHOLD);
	}

	/**
	 * Color space conversion from sRGB to OkLab
	 */
	public static Planar<GrayF32> rgbToOklab(Planar<GrayF32> rgb, float alphaClipThreshold) {
		boolean hasAlpha = rgb.getNumBands() == 4;
		Planar<GrayF32> okLab = rgb.createSameShape();

		GrayF32 R = rgb.getBand(0);
		GrayF32 G = rgb.getBand(1);
		GrayF32 B = rgb.getBand(2);
		GrayF32 srcAlpha = hasAlpha ? rgb.getBand(3) : null;

		GrayF32 L = okLab.getBand(0);
		GrayF32 a = okLab.getBand(1);
		GrayF32 b = okLab.getBand(2);
		GrayF32 alpha = hasAlpha ? okLab.getBand(3) : null;

		BoofConcurrency.loopFor(0, rgb.height, row -> {
			int indexRgb = R.startIndex + row * R.stride;
			int indexOkLab = L.startIndex + row * L.stride;

			for (int col = 0; col < okLab.width; col++, indexOkLab++, indexRgb++) {				
				float R1 = (float) ColorXyz.invGamma(R.data[indexRgb] / 255f);
				float G1 = (float) ColorXyz.invGamma(G.data[indexRgb] / 255f);
				float B1 = (float) ColorXyz.invGamma(B.data[indexRgb] / 255f);
				
				if (hasAlpha) {
					alpha.data[indexOkLab] = srcAlpha.data[indexRgb] / 255f;
					
					if (alpha.data[indexOkLab] < alphaClipThreshold) {
						R1 = 0;
						G1 = 0;
						B1 = 0;
					}
				}

				double l = 0.4122214708 * R1 + 0.5363325363 * G1 + 0.0514459929 * B1;
				double m = 0.2119034982 * R1 + 0.6806995451 * G1 + 0.1073969566 * B1;
				double s = 0.0883024619 * R1 + 0.2817188376 * G1 + 0.6299787005 * B1;

				double l_ = Math.cbrt(l);
				double m_ = Math.cbrt(m);
				double s_ = Math.cbrt(s);

				L.data[indexOkLab] = (float) (0.2104542553 * l_ + 0.7936177850 * m_ - 0.0040720468 * s_);
				a.data[indexOkLab] = (float) (1.9779984951 * l_ - 2.4285922050 * m_ + 0.4505937099 * s_);
				b.data[indexOkLab] = (float) (0.0259040371 * l_ + 0.7827717662 * m_ - 0.8086757660 * s_);
			}
		});

		return okLab;
	}

	/**
	 * Color space conversion from OkLab to sRGB
	 */
	public static Planar<GrayF32> oklabtoRgb(Planar<GrayF32> okLab) {
		boolean hasAlpha = okLab.getNumBands() == 4;
		Planar<GrayF32> rgb = okLab.createSameShape();

		GrayF32 L = okLab.getBand(0);
		GrayF32 a = okLab.getBand(1);
		GrayF32 b = okLab.getBand(2);
		GrayF32 srcAlpha = hasAlpha ? okLab.getBand(3) : null;

		GrayF32 R = rgb.getBand(0);
		GrayF32 G = rgb.getBand(1);
		GrayF32 B = rgb.getBand(2);
		GrayF32 alpha = hasAlpha ? rgb.getBand(3) : null;

		BoofConcurrency.loopFor(0, okLab.height, row -> {
			int indexOkLab = L.startIndex + row * L.stride;
			int indexRgb = R.startIndex + row * R.stride;

			for (int col = 0; col < okLab.width; col++, indexOkLab++, indexRgb++) {
				float L1 = L.data[indexOkLab];
				float a1 = a.data[indexOkLab];
				float b1 = b.data[indexOkLab];

				double l_ = L1 + 0.3963377774 * a1 + 0.2158037573 * b1;
				double m_ = L1 - 0.1055613458 * a1 - 0.0638541728 * b1;
				double s_ = L1 - 0.0894841775 * a1 - 1.2914855480 * b1;

				double l = l_ * l_ * l_;
				double m = m_ * m_ * m_;
				double s = s_ * s_ * s_;

				if (hasAlpha) {
					alpha.data[indexRgb] = srcAlpha.data[indexOkLab] * 255f;
				}
				R.data[indexRgb] = (float) (255.0 * ColorXyz.gamma(+4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s));
				G.data[indexRgb] = (float) (255.0 * ColorXyz.gamma(-1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s));
				B.data[indexRgb] = (float) (255.0 * ColorXyz.gamma(-0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s));
			}
		});

		return rgb;
	}
}
