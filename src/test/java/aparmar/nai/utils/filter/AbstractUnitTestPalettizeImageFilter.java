package aparmar.nai.utils.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import aparmar.nai.utils.InternalResourceLoader;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;

@Tag("slow")
@Tag("imageFilter")
abstract class AbstractUnitTestPalettizeImageFilter<T extends PalettizeImageFilter> {

	protected abstract T createFilter(boolean useOklab);

	protected static BufferedImage createGradientImage(int width, int height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int r = width > 1 ? (x * 255) / (width - 1) : 128;
				int g = height > 1 ? (y * 255) / (height - 1) : 128;
				int b = (width + height > 2) ? ((x + y) * 255) / (width + height - 2) : 128;
				img.setRGB(x, y, new Color(r, g, b).getRGB());
			}
		}
		return img;
	}

	protected static Set<Integer> collectDistinctRgb(BufferedImage img) {
		Set<Integer> distinct = new HashSet<>();
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				distinct.add(img.getRGB(x, y) & 0x00FFFFFF);
			}
		}
		return distinct;
	}

	protected static Set<Integer> collectDistinctOpaqueRgb(BufferedImage img) {
		Set<Integer> distinctOpaque = new HashSet<>();
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				int argb = img.getRGB(x, y);
				if (((argb >>> 24) & 0xFF) == 255) {
					distinctOpaque.add(argb & 0x00FFFFFF);
				}
			}
		}
		return distinctOpaque;
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void testGradientReducesColorsAndPreservesMean(boolean useOklab) {
		int width = 32;
		int height = 32;
		BufferedImage img = createGradientImage(width, height);
		double sumR = 0, sumG = 0, sumB = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color c = new Color(img.getRGB(x, y));
				sumR += c.getRed();
				sumG += c.getGreen();
				sumB += c.getBlue();
			}
		}

		T filter = createFilter(useOklab);
		BufferedImage result = filter.applyBuffered(img);
		assertEquals(width, result.getWidth());
		assertEquals(height, result.getHeight());

		Set<Integer> distinctColors = collectDistinctRgb(result);
		int inputColorCount = collectDistinctRgb(img).size();
		assertTrue(distinctColors.size() < inputColorCount,
				"Expected color reduction from " + inputColorCount + ", got " + distinctColors.size());
		assertTrue(distinctColors.size() >= 2,
				"Expected gradient to use at least 2 colors, got " + distinctColors.size());

		double outR = 0, outG = 0, outB = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color c = new Color(result.getRGB(x, y));
				outR += c.getRed();
				outG += c.getGreen();
				outB += c.getBlue();
			}
		}

		int n = width * height;
		assertEquals(sumR / n, outR / n, 25.0);
		assertEquals(sumG / n, outG / n, 25.0);
		assertEquals(sumB / n, outB / n, 25.0);
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void testSolidColorPreserved(boolean useOklab) {
		BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		int solidRgb = new Color(42, 128, 210).getRGB();
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				img.setRGB(x, y, solidRgb);
			}
		}

		BufferedImage result = createFilter(useOklab).applyBuffered(img);

		Set<Integer> distinct = collectDistinctRgb(result);
		assertEquals(1, distinct.size());
		Color outColor = new Color(distinct.iterator().next());
		assertEquals(42, outColor.getRed(), 2);
		assertEquals(128, outColor.getGreen(), 2);
		assertEquals(210, outColor.getBlue(), 2);
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void testAlphaTransparencyPreserved(boolean useOklab) {
		BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				if (x < 6) {
					img.setRGB(x, y, 0x00000000);
				} else if (x < 10) {
					img.setRGB(x, y, new Color(80, 180, 120, 128).getRGB());
				} else {
					img.setRGB(x, y, new Color(200, 80, 40, 255).getRGB());
				}
			}
		}

		BufferedImage result = createFilter(useOklab).applyBuffered(img);

		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				int argb = result.getRGB(x, y);
				int alpha = (argb >>> 24) & 0xFF;
				if (x < 6) {
					assertEquals(0, alpha, "Transparent pixel should remain transparent");
				} else if (x < 10) {
					assertEquals(128, alpha, 1, "Semi-transparent pixel should preserve alpha");
				} else {
					assertEquals(255, alpha, "Opaque pixel should remain opaque");
				}
			}
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void testAllTransparentImageHandled(boolean useOklab) {
		BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				img.setRGB(x, y, 0x00000000);
			}
		}

		BufferedImage result = createFilter(useOklab).applyBuffered(img);
		assertEquals(8, result.getWidth());
		assertEquals(8, result.getHeight());
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				assertEquals(0, (result.getRGB(x, y) >>> 24) & 0xFF);
			}
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void testTwoColorCheckerboardPreserved(boolean useOklab) {
		BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		int red = new Color(220, 40, 40).getRGB();
		int blue = new Color(40, 40, 220).getRGB();
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				boolean isRed = ((x / 4) + (y / 4)) % 2 == 0;
				img.setRGB(x, y, isRed ? red : blue);
			}
		}

		BufferedImage result = createFilter(useOklab).applyBuffered(img);
		Set<Integer> distinct = collectDistinctRgb(result);
		assertEquals(2, distinct.size(), "Expected 2 distinct colors for red/blue checkerboard");

		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				boolean isRed = ((x / 4) + (y / 4)) % 2 == 0;
				Color c = new Color(result.getRGB(x, y));
				if (isRed) {
					assertTrue(c.getRed() > 200 && c.getGreen() < 60 && c.getBlue() < 60,
							"Expected red, got " + c);
				} else {
					assertTrue(c.getBlue() > 200 && c.getRed() < 60 && c.getGreen() < 60,
							"Expected blue, got " + c);
				}
			}
		}
	}

	@Test
	void testNonSquareDimensionsAndInputImmutability() {
		int width = 17;
		int height = 9;
		BufferedImage img = createGradientImage(width, height);
		int[] originalPixels = img.getRGB(0, 0, width, height, null, 0, width);

		BufferedImage result = createFilter(true).applyBuffered(img);
		assertEquals(width, result.getWidth());
		assertEquals(height, result.getHeight());

		int[] afterPixels = img.getRGB(0, 0, width, height, null, 0, width);
		for (int i = 0; i < originalPixels.length; i++) {
			assertEquals(originalPixels[i], afterPixels[i], "Input BufferedImage must not be mutated");
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void testThreeBandPlanarWithoutAlpha(boolean useOklab) {
		int width = 10;
		int height = 8;
		Planar<GrayF32> planar = new Planar<>(GrayF32.class, width, height, 3);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				planar.getBand(0).set(x, y, x * 25f);
				planar.getBand(1).set(x, y, y * 30f);
				planar.getBand(2).set(x, y, 128f);
			}
		}

		Planar<GrayF32> result = createFilter(useOklab).applyPlanar(planar);
		assertEquals(width, result.width);
		assertEquals(height, result.height);
		assertEquals(3, result.getNumBands());

		for (int b = 0; b < 3; b++) {
			GrayF32 band = result.getBand(b);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					float v = band.get(x, y);
					assertTrue(Float.isFinite(v), "Output channel value must be finite");
				}
			}
		}
	}

	@Test
	void testRealSampleImage() throws Exception {
		BufferedImage baseImage = ImageIO.read(InternalResourceLoader.getInternalResourceAsStream("sample_pixel_snap.png"));
		BufferedImage snapped = new PixelGridAlignSnapImageFilter().applyBuffered(baseImage);

		BufferedImage quantized = createFilter(true).applyBuffered(snapped);

		assertEquals(snapped.getWidth(), quantized.getWidth());
		assertEquals(snapped.getHeight(), quantized.getHeight());

		Set<Integer> inputOpaque = collectDistinctOpaqueRgb(snapped);
		Set<Integer> outputOpaque = collectDistinctOpaqueRgb(quantized);
		assertTrue(!outputOpaque.isEmpty(), "Expected at least 1 opaque color");
		assertTrue(outputOpaque.size() < inputOpaque.size(),
				"Expected opaque color reduction from " + inputOpaque.size() + ", got " + outputOpaque.size());
	}

	@Test
	void testQuantizedColorAndColorPointHelpers() {
		PalettizeImageFilter.ColorPoint pt = new PalettizeImageFilter.ColorPoint(0.25, 0.5, 0.75);
		PalettizeImageFilter.ColorPoint copy = new PalettizeImageFilter.ColorPoint(pt);
		assertEquals(0.25, copy.getPoint()[0], 1e-9);
		assertEquals(0.5, copy.getPoint()[1], 1e-9);
		assertEquals(0.75, copy.getPoint()[2], 1e-9);

		PalettizeImageFilter.QuantizedColor q1 = PalettizeImageFilter.QuantizedColor.fromColor(0.24f, 0.51f, 0.74f, 0.1);
		PalettizeImageFilter.QuantizedColor q2 = PalettizeImageFilter.QuantizedColor.fromColor(0.21f, 0.49f, 0.66f, 0.1);
		assertEquals(q1, q2, "Colors rounding to the same bucket should be equal");
		assertEquals(0.2f, q1.getUnquantColor()[0], 1e-5f);
		assertEquals(0.5, q1.getUnquantColorDouble()[1], 1e-9);
		assertEquals(0.7, q1.getUnquantColorDouble()[2], 1e-9);
	}
}
