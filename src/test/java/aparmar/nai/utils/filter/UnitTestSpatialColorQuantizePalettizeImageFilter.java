package aparmar.nai.utils.filter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Tag("imageFilter")
class UnitTestSpatialColorQuantizePalettizeImageFilter
		extends AbstractUnitTestPalettizeImageFilter<SpatialColorQuantizePalettizeImageFilter> {

	@Override
	protected SpatialColorQuantizePalettizeImageFilter createFilter(boolean useOklab) {
		return SpatialColorQuantizePalettizeImageFilter.builder()
				.useOklabColorSpace(useOklab)
				.colorCount(8)
				.build();
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void testGradientQuantizesToAtMostColorCount(boolean useOklab) {
		BufferedImage img = createGradientImage(32, 32);
		int targetColors = 6;
		SpatialColorQuantizePalettizeImageFilter filter = SpatialColorQuantizePalettizeImageFilter.builder()
				.useOklabColorSpace(useOklab)
				.colorCount(targetColors)
				.build();

		BufferedImage result = filter.applyBuffered(img);
		Set<Integer> distinctColors = collectDistinctRgb(result);
		assertTrue(distinctColors.size() <= targetColors,
				"Expected <= " + targetColors + " colors, got " + distinctColors.size());
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
	void testSingleBlurSigmaProducesValidPalette() {
		BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				img.setRGB(x, y, new Color(x * 16, y * 16, 128).getRGB());
			}
		}

		SpatialColorQuantizePalettizeImageFilter filter = SpatialColorQuantizePalettizeImageFilter.builder()
				.colorCount(4)
				.blurSigmas(List.of(0.5))
				.steps(40)
				.build();
		BufferedImage result = filter.applyBuffered(img);

		Set<Integer> distinct = collectDistinctRgb(result);
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
				.endTau(0.0)
				.build()
				.applyBuffered(img));
		assertThrows(IllegalArgumentException.class, () -> SpatialColorQuantizePalettizeImageFilter.builder()
				.blurSigmas(Collections.emptyList())
				.build()
				.applyBuffered(img));
	}

	@Test
	void testDeterministicWithSameSeed() {
		BufferedImage img = createGradientImage(20, 20);
		SpatialColorQuantizePalettizeImageFilter filterA = SpatialColorQuantizePalettizeImageFilter.builder()
				.colorCount(5)
				.steps(30)
				.seed(12345L)
				.build();
		SpatialColorQuantizePalettizeImageFilter filterB = SpatialColorQuantizePalettizeImageFilter.builder()
				.colorCount(5)
				.steps(30)
				.seed(12345L)
				.build();

		BufferedImage outA = filterA.applyBuffered(img);
		BufferedImage outB = filterB.applyBuffered(img);

		int[] pixelsA = outA.getRGB(0, 0, 20, 20, null, 0, 20);
		int[] pixelsB = outB.getRGB(0, 0, 20, 20, null, 0, 20);
		assertArrayEquals(pixelsA, pixelsB, "Same seed should produce identical output");
	}

	@Test
	void testSingleStepAndColorCountOneFastPath() {
		BufferedImage img = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < 12; y++) {
			for (int x = 0; x < 12; x++) {
				if (x < 4) {
					img.setRGB(x, y, 0x00000000);
				} else {
					img.setRGB(x, y, new Color(x * 20, y * 20, 100, 255).getRGB());
				}
			}
		}

		// colorCount == 1 fast path
		BufferedImage oneColor = SpatialColorQuantizePalettizeImageFilter.builder()
				.colorCount(1)
				.build()
				.applyBuffered(img);
		assertEquals(1, collectDistinctOpaqueRgb(oneColor).size());
		assertEquals(0, (oneColor.getRGB(0, 0) >>> 24) & 0xFF);

		// steps == 1 branch
		BufferedImage singleStep = SpatialColorQuantizePalettizeImageFilter.builder()
				.colorCount(4)
				.steps(1)
				.build()
				.applyBuffered(img);
		Set<Integer> distinct = collectDistinctOpaqueRgb(singleStep);
		assertTrue(distinct.size() >= 1 && distinct.size() <= 4);
	}

	@Test
	void testLargeImageExercisesKMeansSubsampling() {
		// > 16,384 opaque pixels triggers random subsampling in initializePaletteKMeans
		BufferedImage largeImg = createGradientImage(140, 140);
		SpatialColorQuantizePalettizeImageFilter filter = SpatialColorQuantizePalettizeImageFilter.builder()
				.colorCount(4)
				.steps(5)
				.build();

		BufferedImage result = filter.applyBuffered(largeImg);
		Set<Integer> distinct = collectDistinctRgb(result);
		assertTrue(distinct.size() >= 2 && distinct.size() <= 4);
	}
}
