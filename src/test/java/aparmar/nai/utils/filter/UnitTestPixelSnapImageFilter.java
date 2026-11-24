package aparmar.nai.utils.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import aparmar.nai.TestConstants;
import aparmar.nai.utils.InternalResourceLoader;
import aparmar.nai.utils.filter.PixelSnapImageFilter.PalettizeMode;
import aparmar.nai.utils.filter.PixelSnapImageFilter.PalettizeTarget;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;

@Tag("imageFilter")
class UnitTestPixelSnapImageFilter {

	@ParameterizedTest
	@ValueSource(strings = { "sample_pixel_snap.png", "sample_pixel_snap_2.png" })
	void testPixelSnap(String filename) throws AssertionError, Exception {
		BufferedImage baseImage = ImageIO.read(InternalResourceLoader.getInternalResourceAsStream(filename));
		
		PixelSnapImageFilter imageFilter = new PixelSnapImageFilter();
		BufferedImage result = imageFilter.applyBuffered(baseImage);
		
		ImageIO.write(result, "png", new File(TestConstants.TEST_IMAGE_FOLDER+"out_"+filename));
	}

	@Test
	void testEstimateAutoColorCountEdgeCasesAndDiversity() {
		Planar<GrayF32> solid = ConvertBufferedImage.convertFromPlanar(
				createSolidImage(16, 16, new Color(40, 120, 200)), null, true, GrayF32.class);
		assertEquals(1, PixelSnapImageFilter.estimateAutoColorCount(solid, 0.01f));

		Planar<GrayF32> transparent = new Planar<>(GrayF32.class, 8, 8, 4);
		assertEquals(1, PixelSnapImageFilter.estimateAutoColorCount(transparent, 0.01f));

		Planar<GrayF32> fourColors = ConvertBufferedImage.convertFromPlanar(
				createFourColorGridImage(4), null, true, GrayF32.class);
		assertEquals(4, PixelSnapImageFilter.estimateAutoColorCount(fourColors, 0.01f));

		Planar<GrayF32> gradient = ConvertBufferedImage.convertFromPlanar(
				createGradientImage(32, 32), null, true, GrayF32.class);
		int fineEstimate = PixelSnapImageFilter.estimateAutoColorCount(gradient, 0.01f);
		int coarseEstimate = PixelSnapImageFilter.estimateAutoColorCount(gradient, 0.15f);
		assertTrue(fineEstimate > 4 && fineEstimate <= PixelSnapImageFilter.MAX_AUTO_COLOR_COUNT,
				"Expected fine gradient estimate in (4, " + PixelSnapImageFilter.MAX_AUTO_COLOR_COUNT + "], got " + fineEstimate);
		assertTrue(coarseEstimate < fineEstimate && coarseEstimate >= 2,
				"Expected coarse estimate < fine estimate (" + coarseEstimate + " vs " + fineEstimate + ")");
	}

	@ParameterizedTest
	@ValueSource(strings = { "K_MEANS", "SPATIAL_QUANTIZE" })
	void testAutoPalettizationWithColorCountModes(String modeName) {
		PalettizeMode mode = PalettizeMode.valueOf(modeName);
		BufferedImage input = createFourColorGridImage(4);

		PixelSnapImageFilter filter = PixelSnapImageFilter.naiLike()
				.palettizeMode(mode)
				.palettizeTarget(PalettizeTarget.auto())
				.build();

		BufferedImage result = filter.applyBuffered(input);
		Set<Integer> distinctColors = collectDistinctRgb(result);
		assertTrue(distinctColors.size() >= 2 && distinctColors.size() <= 4,
				"Expected 2..4 colors for 4-color input with " + mode + ", got " + distinctColors.size());
	}

	private static BufferedImage createSolidImage(int width, int height, Color color) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int rgb = color.getRGB();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				img.setRGB(x, y, rgb);
			}
		}
		return img;
	}

	private static BufferedImage createFourColorGridImage(int scale) {
		Color[] palette = {
				new Color(20, 20, 20),
				new Color(220, 40, 40),
				new Color(40, 200, 60),
				new Color(40, 80, 230)
		};
		int gridSize = 16;
		BufferedImage img = new BufferedImage(gridSize * scale, gridSize * scale, BufferedImage.TYPE_INT_RGB);
		for (int gy = 0; gy < gridSize; gy++) {
			for (int gx = 0; gx < gridSize; gx++) {
				int rgb = palette[((gx % 2) * 2 + (gy % 2)) % palette.length].getRGB();
				for (int dy = 0; dy < scale; dy++) {
					for (int dx = 0; dx < scale; dx++) {
						img.setRGB(gx * scale + dx, gy * scale + dy, rgb);
					}
				}
			}
		}
		return img;
	}

	private static BufferedImage createGradientImage(int width, int height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int r = (x * 255) / (width - 1);
				int g = (y * 255) / (height - 1);
				int b = ((x + y) * 255) / (width + height - 2);
				img.setRGB(x, y, new Color(r, g, b).getRGB());
			}
		}
		return img;
	}

	private static Set<Integer> collectDistinctRgb(BufferedImage img) {
		Set<Integer> distinct = new HashSet<>();
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				distinct.add(img.getRGB(x, y) & 0x00FFFFFF);
			}
		}
		return distinct;
	}
}
