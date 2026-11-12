package aparmar.nai.utils.filter;

import org.jetbrains.annotations.Nullable;

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
	 * Color space conversion from sRGB ([0, 255]) to linear sRGB ([0, 1]).
	 */
	public static Planar<GrayF32> srgbToLinearRgb(Planar<GrayF32> srgb, @Nullable Planar<GrayF32> out) {
		return srgbToLinearRgb(srgb, out, DEFAULT_ALPHA_CLIP_THRESHOLD);
	}

	/**
	 * Color space conversion from sRGB ([0, 255]) to linear sRGB ([0, 1]).
	 * Supports in-place conversion when {@code out == srgb}.
	 */
	public static Planar<GrayF32> srgbToLinearRgb(Planar<GrayF32> srgb, @Nullable Planar<GrayF32> out, float alphaClipThreshold) {
		boolean hasAlpha = srgb.getNumBands() == 4;
		if (out == null) {
			out = srgb.createSameShape();
		} else if (out != srgb) {
			out.reshape(srgb.width, srgb.height, srgb.getNumBands());
		}

		GrayF32 R = srgb.getBand(0);
		GrayF32 G = srgb.getBand(1);
		GrayF32 B = srgb.getBand(2);
		GrayF32 srcAlpha = hasAlpha ? srgb.getBand(3) : null;

		GrayF32 linR = out.getBand(0);
		GrayF32 linG = out.getBand(1);
		GrayF32 linB = out.getBand(2);
		GrayF32 outAlpha = hasAlpha ? out.getBand(3) : null;

		BoofConcurrency.loopFor(0, srgb.height, row -> {
			int indexSrgb = R.startIndex + row * R.stride;
			int indexOut = linR.startIndex + row * linR.stride;

			for (int col = 0; col < srgb.width; col++, indexSrgb++, indexOut++) {
				float r1 = (float) ColorXyz.invGamma(R.data[indexSrgb] / 255f);
				float g1 = (float) ColorXyz.invGamma(G.data[indexSrgb] / 255f);
				float b1 = (float) ColorXyz.invGamma(B.data[indexSrgb] / 255f);

				if (hasAlpha) {
					float a = srcAlpha.data[indexSrgb] / 255f;
					outAlpha.data[indexOut] = a;
					if (a < alphaClipThreshold) {
						r1 = 0;
						g1 = 0;
						b1 = 0;
					}
				}

				linR.data[indexOut] = r1;
				linG.data[indexOut] = g1;
				linB.data[indexOut] = b1;
			}
		});

		return out;
	}

	/**
	 * Color space conversion from linear sRGB ([0, 1]) to sRGB ([0, 255]).
	 * Supports in-place conversion when {@code out == linearRgb}.
	 */
	public static Planar<GrayF32> linearRgbToSrgb(Planar<GrayF32> linearRgb, @Nullable Planar<GrayF32> out) {
		boolean hasAlpha = linearRgb.getNumBands() == 4;
		if (out == null) {
			out = linearRgb.createSameShape();
		} else if (out != linearRgb) {
			out.reshape(linearRgb.width, linearRgb.height, linearRgb.getNumBands());
		}

		GrayF32 linR = linearRgb.getBand(0);
		GrayF32 linG = linearRgb.getBand(1);
		GrayF32 linB = linearRgb.getBand(2);
		GrayF32 srcAlpha = hasAlpha ? linearRgb.getBand(3) : null;

		GrayF32 R = out.getBand(0);
		GrayF32 G = out.getBand(1);
		GrayF32 B = out.getBand(2);
		GrayF32 outAlpha = hasAlpha ? out.getBand(3) : null;

		BoofConcurrency.loopFor(0, linearRgb.height, row -> {
			int indexLin = linR.startIndex + row * linR.stride;
			int indexOut = R.startIndex + row * R.stride;

			for (int col = 0; col < linearRgb.width; col++, indexLin++, indexOut++) {
				float r1 = Math.max(0f, Math.min(1f, linR.data[indexLin]));
				float g1 = Math.max(0f, Math.min(1f, linG.data[indexLin]));
				float b1 = Math.max(0f, Math.min(1f, linB.data[indexLin]));

				if (hasAlpha) {
					outAlpha.data[indexOut] = srcAlpha.data[indexLin] * 255f;
				}
				R.data[indexOut] = (float) (255.0 * ColorXyz.gamma(r1));
				G.data[indexOut] = (float) (255.0 * ColorXyz.gamma(g1));
				B.data[indexOut] = (float) (255.0 * ColorXyz.gamma(b1));
			}
		});

		return out;
	}

	/**
	 * Color space conversion from sRGB to OkLab.
	 * Supports in-place conversion when {@code out == rgb}.
	 */
	public static Planar<GrayF32> rgbToOklab(Planar<GrayF32> rgb, @Nullable Planar<GrayF32> out) {
		return rgbToOklab(rgb, out, DEFAULT_ALPHA_CLIP_THRESHOLD);
	}

	/**
	 * Color space conversion from sRGB to OkLab.
	 * Supports in-place conversion when {@code out == rgb}.
	 */
	public static Planar<GrayF32> rgbToOklab(Planar<GrayF32> rgb, @Nullable Planar<GrayF32> out, float alphaClipThreshold) {
		boolean hasAlpha = rgb.getNumBands() == 4;
		if (out == null) {
			out = rgb.createSameShape();
		} else if (out != rgb) {
			out.reshape(rgb.width, rgb.height, rgb.getNumBands());
		}

		GrayF32 R = rgb.getBand(0);
		GrayF32 G = rgb.getBand(1);
		GrayF32 B = rgb.getBand(2);
		GrayF32 srcAlpha = hasAlpha ? rgb.getBand(3) : null;

		GrayF32 L = out.getBand(0);
		GrayF32 a = out.getBand(1);
		GrayF32 b = out.getBand(2);
		GrayF32 alpha = hasAlpha ? out.getBand(3) : null;

		BoofConcurrency.loopFor(0, rgb.height, row -> {
			int indexRgb = R.startIndex + row * R.stride;
			int indexOkLab = L.startIndex + row * L.stride;

			for (int col = 0; col < rgb.width; col++, indexOkLab++, indexRgb++) {
				float R1 = (float) ColorXyz.invGamma(R.data[indexRgb] / 255f);
				float G1 = (float) ColorXyz.invGamma(G.data[indexRgb] / 255f);
				float B1 = (float) ColorXyz.invGamma(B.data[indexRgb] / 255f);
				
				if (hasAlpha) {
					float alphaVal = srcAlpha.data[indexRgb] / 255f;
					alpha.data[indexOkLab] = alphaVal;
					
					if (alphaVal < alphaClipThreshold) {
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

		return out;
	}

	/**
	 * Color space conversion from OkLab to sRGB.
	 * Supports in-place conversion when {@code out == okLab}.
	 */
	public static Planar<GrayF32> oklabtoRgb(Planar<GrayF32> okLab, @Nullable Planar<GrayF32> out) {
		boolean hasAlpha = okLab.getNumBands() == 4;
		if (out == null) {
			out = okLab.createSameShape();
		} else if (out != okLab) {
			out.reshape(okLab.width, okLab.height, okLab.getNumBands());
		}

		GrayF32 L = okLab.getBand(0);
		GrayF32 a = okLab.getBand(1);
		GrayF32 b = okLab.getBand(2);
		GrayF32 srcAlpha = hasAlpha ? okLab.getBand(3) : null;

		GrayF32 R = out.getBand(0);
		GrayF32 G = out.getBand(1);
		GrayF32 B = out.getBand(2);
		GrayF32 alpha = hasAlpha ? out.getBand(3) : null;

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

		return out;
	}
}
