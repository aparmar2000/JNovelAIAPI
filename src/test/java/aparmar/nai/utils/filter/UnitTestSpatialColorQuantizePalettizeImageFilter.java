package aparmar.nai.utils.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import aparmar.nai.utils.InternalResourceLoader;

class UnitTestSpatialColorQuantizePalettizeImageFilter {

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void testGradientQuantizesToAtMostColorCountAndPreservesMean(boolean useOklab) {
		int width = 32;
		int height = 32;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		double sumR = 0, sumG = 0, sumB = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int r = (x * 255) / (width - 1);
				int g = (y * 255) / (height - 1);
				int b = ((x + y) * 255) / (width + height - 2);
				img.setRGB(x, y, new Color(r, g, b).getRGB());
				sumR += r;
				sumG += g;
				sumB += b;
			}
		}

		int targetColors = 6;
		SpatialColorQuantizePalettizeImageFilter filter = SpatialColorQuantizePalettizeImageFilter.builder()
				.useOklabColorSpace(useOklab)
				.colorCount(targetColors)
				.build();

		BufferedImage result = filter.applyBuffered(img);
		assertEquals(width, result.getWidth());
		assertEquals(height, result.getHeight());

		Set<Integer> distinctColors = new HashSet<>();
		double outR = 0, outG = 0, outB = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rgb = result.getRGB(x, y) & 0x00FFFFFF;
				distinctColors.add(rgb);
				Color c = new Color(rgb);
				outR += c.getRed();
				outG += c.getGreen();
				outB += c.getBlue();
			}
		}

		assertTrue(distinctColors.size() <= targetColors,
				"Expected <= " + targetColors + " colors, got " + distinctColors.size());
		assertTrue(distinctColors.size() >= 2,
				"Expected gradient to use at least 2 colors, got " + distinctColors.size());

		int n = width * height;
		assertEquals(sumR / n, outR / n, 15.0);
		assertEquals(sumG / n, outG / n, 15.0);
		assertEquals(sumB / n, outB / n, 15.0);
	}

	@Test
	void testSolidColorAndSingleColorCount() {
		BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		int solidRgb = new Color(42, 128, 210).getRGB();
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				img.setRGB(x, y, solidRgb);
			}
		}

		SpatialColorQuantizePalettizeImageFilter filter = SpatialColorQuantizePalettizeImageFilter.builder()
				.colorCount(8)
				.build();
		BufferedImage result = filter.applyBuffered(img);

		Set<Integer> distinct = new HashSet<>();
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				distinct.add(result.getRGB(x, y) & 0x00FFFFFF);
			}
		}
		assertEquals(1, distinct.size());
		Color outColor = new Color(distinct.iterator().next());
		assertEquals(42, outColor.getRed(), 2);
		assertEquals(128, outColor.getGreen(), 2);
		assertEquals(210, outColor.getBlue(), 2);
	}

	@Test
	void testAlphaTransparencyPreserved() {
		BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				if (x < 8) {
					img.setRGB(x, y, 0x00000000);
				} else {
					img.setRGB(x, y, new Color(200, 80, 40, 255).getRGB());
				}
			}
		}

		SpatialColorQuantizePalettizeImageFilter filter = SpatialColorQuantizePalettizeImageFilter.builder()
				.colorCount(4)
				.build();
		BufferedImage result = filter.applyBuffered(img);

		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				int argb = result.getRGB(x, y);
				int alpha = (argb >>> 24) & 0xFF;
				if (x < 8) {
					assertEquals(0, alpha, "Transparent pixel should remain transparent");
				} else {
					assertEquals(255, alpha, "Opaque pixel should remain opaque");
				}
			}
		}
	}

	@Test
	void testSingleBlurSigmaProducesValidPalette() {
		BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				img.setRGB(x, y, new Color(x * 16, y * 16, 128).getRGB());
			}
		}

		SpatialColorQuantizePalettizeImageFilter filter = SpatialColorQuantizePalettizeImageFilter.builder()
				.colorCount(4)
				.blurSigmas(java.util.List.of(0.5))
				.steps(40)
				.build();
		BufferedImage result = filter.applyBuffered(img);

		Set<Integer> distinct = new HashSet<>();
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				distinct.add(result.getRGB(x, y) & 0x00FFFFFF);
			}
		}
		assertTrue(distinct.size() <= 4 && distinct.size() >= 1);
	}

	@Test
	void testInvalidParametersThrow() {
		BufferedImage img = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);

		assertThrows(IllegalArgumentException.class, () -> SpatialColorQuantizePalettizeImageFilter.builder()
				.colorCount(0)
				.build()
				.applyBuffered(img));
		assertThrows(IllegalArgumentException.class, () -> SpatialColorQuantizePalettizeImageFilter.builder()
				.steps(0)
				.build()
				.applyBuffered(img));
		assertThrows(IllegalArgumentException.class, () -> SpatialColorQuantizePalettizeImageFilter.builder()
				.startTau(0.01)
				.endTau(0.1)
				.build()
				.applyBuffered(img));
		assertThrows(IllegalArgumentException.class, () -> SpatialColorQuantizePalettizeImageFilter.builder()
				.blurSigmas(java.util.Collections.emptyList())
				.build()
				.applyBuffered(img));
	}

	@Test
	void testRealSampleImage() throws Exception {
		BufferedImage baseImage = ImageIO.read(InternalResourceLoader.getInternalResourceAsStream("sample_pixel_snap.png"));
		BufferedImage snapped = new PixelSnapImageFilter().applyBuffered(baseImage);

		SpatialColorQuantizePalettizeImageFilter filter = SpatialColorQuantizePalettizeImageFilter.builder()
				.colorCount(8)
				.build();
		BufferedImage quantized = filter.applyBuffered(snapped);

		assertEquals(snapped.getWidth(), quantized.getWidth());
		assertEquals(snapped.getHeight(), quantized.getHeight());

		Set<Integer> distinctOpaque = new HashSet<>();
		for (int y = 0; y < quantized.getHeight(); y++) {
			for (int x = 0; x < quantized.getWidth(); x++) {
				int argb = quantized.getRGB(x, y);
				if (((argb >>> 24) & 0xFF) == 255) {
					distinctOpaque.add(argb & 0x00FFFFFF);
				}
			}
		}
		assertTrue(distinctOpaque.size() <= 8,
				"Expected <= 8 opaque colors, got " + distinctOpaque.size());
	}
}
