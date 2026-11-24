package aparmar.nai.utils.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import aparmar.nai.utils.filter.PalettizeImageFilter.ColorPoint;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;

@Tag("imageFilter")
class UnitTestKMeansPalettizeImageFilter extends AbstractUnitTestPalettizeImageFilter<KMeansPalettizeImageFilter> {

	@Override
	protected KMeansPalettizeImageFilter createFilter(boolean useOklab) {
		return KMeansPalettizeImageFilter.builder()
				.useOklabColorSpace(useOklab)
				.colorCount(8)
				.build();
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void testGradientQuantizesToAtMostColorCount(boolean useOklab) {
		BufferedImage img = createGradientImage(32, 32);
		int targetColors = 6;
		KMeansPalettizeImageFilter filter = KMeansPalettizeImageFilter.builder()
				.useOklabColorSpace(useOklab)
				.colorCount(targetColors)
				.build();

		BufferedImage result = filter.applyBuffered(img);
		Set<Integer> distinctColors = collectDistinctRgb(result);
		assertTrue(distinctColors.size() <= targetColors,
				"Expected <= " + targetColors + " colors, got " + distinctColors.size());
	}

	@Test
	void testMaxIterationsOneProducesValidBoundedPalette() {
		BufferedImage img = createGradientImage(24, 24);
		KMeansPalettizeImageFilter filter = KMeansPalettizeImageFilter.builder()
				.colorCount(4)
				.maxIterations(1)
				.build();

		BufferedImage result = filter.applyBuffered(img);
		Set<Integer> distinct = collectDistinctRgb(result);
		assertTrue(distinct.size() >= 1 && distinct.size() <= 4);
	}

	@Test
	void testFourDistinctBlocksRecoverExactFourColorPalette() {
		BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		int[] blockColors = {
				new Color(220, 30, 30).getRGB(),
				new Color(30, 220, 30).getRGB(),
				new Color(30, 30, 220).getRGB(),
				new Color(220, 220, 30).getRGB()
		};
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				int quadrant = (y < 8 ? 0 : 2) + (x < 8 ? 0 : 1);
				img.setRGB(x, y, blockColors[quadrant]);
			}
		}

		KMeansPalettizeImageFilter filter = KMeansPalettizeImageFilter.builder()
				.colorCount(4)
				.build();
		BufferedImage result = filter.applyBuffered(img);
		Set<Integer> distinct = collectDistinctRgb(result);
		assertEquals(4, distinct.size());
	}

	@Test
	void testExtractColorsAndQuantizeColorsDirectly() {
		Planar<GrayF32> planar = new Planar<>(GrayF32.class, 2, 2, 3);
		// Pixel (0,0): close to (10, 0, 0)
		planar.getBand(0).set(0, 0, 12f);
		planar.getBand(1).set(0, 0, 1f);
		planar.getBand(2).set(0, 0, 2f);
		// Pixel (1,0): close to (100, 100, 100)
		planar.getBand(0).set(1, 0, 95f);
		planar.getBand(1).set(1, 0, 102f);
		planar.getBand(2).set(1, 0, 98f);
		// Pixel (0,1): close to (10, 0, 0)
		planar.getBand(0).set(0, 1, 8f);
		planar.getBand(1).set(0, 1, 3f);
		planar.getBand(2).set(0, 1, 0f);
		// Pixel (1,1): close to (100, 100, 100)
		planar.getBand(0).set(1, 1, 105f);
		planar.getBand(1).set(1, 1, 99f);
		planar.getBand(2).set(1, 1, 101f);

		KMeansPalettizeImageFilter filter = KMeansPalettizeImageFilter.builder().build();
		List<ColorPoint> extracted = filter.extractColors(planar);
		assertEquals(4, extracted.size());
		assertEquals(12.0, extracted.get(0).getPoint()[0], 1e-5);
		assertEquals(95.0, extracted.get(1).getPoint()[0], 1e-5);

		ColorPoint pA = new ColorPoint(10, 0, 0);
		ColorPoint pB = new ColorPoint(100, 100, 100);
		filter.quantizeColors(planar, List.of(pA, pB));

		assertEquals(10f, planar.getBand(0).get(0, 0), 1e-5f);
		assertEquals(0f, planar.getBand(1).get(0, 0), 1e-5f);
		assertEquals(100f, planar.getBand(0).get(1, 0), 1e-5f);
		assertEquals(10f, planar.getBand(0).get(0, 1), 1e-5f);
		assertEquals(100f, planar.getBand(0).get(1, 1), 1e-5f);
	}

	@Test
	void testColorCountInterfaceAssignability() {
		IColorCountPalettizeImageFilter filter = createFilter(true);
		assertEquals(8, filter.getColorCount());
		filter.setColorCount(4);
		assertEquals(4, filter.getColorCount());

		BufferedImage result = filter.applyBuffered(createGradientImage(16, 16));
		assertTrue(collectDistinctRgb(result).size() <= 4);
	}

	@Test
	void testTooFewPixelsForColorCountThrows() {
		BufferedImage smallImg = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);

		assertThrows(NumberIsTooSmallException.class, () -> KMeansPalettizeImageFilter.builder()
				.colorCount(8)
				.build()
				.applyBuffered(smallImg));
	}
}
