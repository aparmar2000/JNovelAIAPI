package aparmar.nai.utils.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Set;

import org.junit.jupiter.api.Test;

import aparmar.nai.utils.filter.SpatialMergePalettizeImageFilter.ColorCluster;

class UnitTestSpatialMergePalettizeImageFilter extends AbstractUnitTestPalettizeImageFilter<SpatialMergePalettizeImageFilter> {

	@Override
	protected SpatialMergePalettizeImageFilter createFilter(boolean useOklab) {
		return SpatialMergePalettizeImageFilter.builder()
				.useOklabColorSpace(useOklab)
				.intraClusterMergeThreshold(useOklab ? 0.005f : 2.0f)
				.mergeStoppingPredicate((count, dist) -> dist > (useOklab ? 0.15f : 40.0f))
				.build();
	}

	@Test
	void testBothTargetInterfacesAssignabilityAndSwitching() {
		SpatialMergePalettizeImageFilter concrete = SpatialMergePalettizeImageFilter.builder().build();

		ISimilarityThresholdPalettizeImageFilter simFilter = concrete;
		assertEquals(SpatialMergePalettizeImageFilter.DEFAULT_SIMILARITY_THRESHOLD, simFilter.getSimilarityThreshold(), 1e-6f);
		simFilter.setSimilarityThreshold(0.05f);
		assertEquals(0.05f, simFilter.getSimilarityThreshold(), 1e-6f);
		assertThrows(IllegalStateException.class, concrete::getColorCount);

		IColorCountPalettizeImageFilter countFilter = concrete;
		countFilter.setColorCount(5);
		assertEquals(5, countFilter.getColorCount());
		assertThrows(IllegalStateException.class, concrete::getSimilarityThreshold);

		concrete.setMergeStoppingPredicate((c, d) -> c <= 2);
		assertThrows(IllegalStateException.class, concrete::getColorCount);
		assertThrows(IllegalStateException.class, concrete::getSimilarityThreshold);
	}

	@Test
	void testNonAdjacentSimilarClustersAreMerged() {
		// Left and right regions are similar grays separated by a bright blue barrier
		BufferedImage img = new BufferedImage(12, 4, BufferedImage.TYPE_INT_RGB);
		int grayA = new Color(100, 100, 100).getRGB();
		int grayB = new Color(110, 110, 110).getRGB();
		int barrier = new Color(0, 0, 255).getRGB();

		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 12; x++) {
				if (x < 4) {
					img.setRGB(x, y, grayA);
				} else if (x < 8) {
					img.setRGB(x, y, barrier);
				} else {
					img.setRGB(x, y, grayB);
				}
			}
		}

		SpatialMergePalettizeImageFilter filter = SpatialMergePalettizeImageFilter.builder()
				.useOklabColorSpace(false)
				.intraClusterMergeThreshold(1.0f)
				.mergeStoppingPredicate((count, dist) -> dist > 30.0f)
				.build();

		BufferedImage result = filter.applyBuffered(img);
		Set<Integer> distinct = collectDistinctRgb(result);
		assertEquals(2, distinct.size(), "Non-adjacent similar gray regions should merge into 1 color + 1 barrier");
		assertEquals(result.getRGB(0, 0), result.getRGB(11, 0),
				"Merged non-adjacent regions should share the exact same color");
		assertNotEquals(result.getRGB(0, 0), result.getRGB(5, 0));
	}

	@Test
	void testAdjacentClustersAreNotMergedEvenWithPermissiveStoppingPredicate() {
		// Only two adjacent regions: they have an adjacency edge, so mergeNonAdjacentClusters cannot merge them
		BufferedImage img = new BufferedImage(8, 4, BufferedImage.TYPE_INT_RGB);
		int left = new Color(100, 100, 100).getRGB();
		int right = new Color(110, 110, 110).getRGB();

		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 8; x++) {
				img.setRGB(x, y, x < 4 ? left : right);
			}
		}

		SpatialMergePalettizeImageFilter filter = SpatialMergePalettizeImageFilter.builder()
				.useOklabColorSpace(false)
				.intraClusterMergeThreshold(1.0f)
				.mergeStoppingPredicate(SpatialMergePalettizeImageFilter.colorCountPredicate(1))
				.build();

		BufferedImage result = filter.applyBuffered(img);
		Set<Integer> distinct = collectDistinctRgb(result);
		assertEquals(2, distinct.size(), "Spatially adjacent clusters must not be merged");
		assertNotEquals(result.getRGB(0, 0), result.getRGB(7, 0));
	}

	@Test
	void testIntraClusterMergeThresholdControlsFloodFill() {
		BufferedImage img = new BufferedImage(8, 4, BufferedImage.TYPE_INT_RGB);
		int c1 = new Color(100, 100, 100).getRGB();
		int c2 = new Color(108, 100, 100).getRGB();
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 8; x++) {
				img.setRGB(x, y, x < 4 ? c1 : c2);
			}
		}

		// Tight threshold keeps them in 2 separate adjacent clusters
		BufferedImage tightResult = SpatialMergePalettizeImageFilter.builder()
				.useOklabColorSpace(false)
				.intraClusterMergeThreshold(1.0f)
				.mergeStoppingPredicate(SpatialMergePalettizeImageFilter.similarityThresholdPredicate(0.0f))
				.build()
				.applyBuffered(img);
		assertEquals(2, collectDistinctRgb(tightResult).size());

		// Loose threshold floods both into 1 cluster
		BufferedImage looseResult = SpatialMergePalettizeImageFilter.builder()
				.useOklabColorSpace(false)
				.intraClusterMergeThreshold(20.0f)
				.mergeStoppingPredicate(SpatialMergePalettizeImageFilter.similarityThresholdPredicate(0.0f))
				.build()
				.applyBuffered(img);
		assertEquals(1, collectDistinctRgb(looseResult).size());
	}

	@Test
	void testColorCountPredicateMergesNonAdjacentIslandsToTargetCount() {
		// 4 isolated islands of distinct shades separated by a black background
		BufferedImage img = new BufferedImage(12, 12, BufferedImage.TYPE_INT_RGB);
		int bg = new Color(0, 0, 0).getRGB();
		int[] islands = {
				new Color(200, 50, 50).getRGB(),
				new Color(205, 52, 50).getRGB(),
				new Color(50, 200, 50).getRGB(),
				new Color(52, 205, 50).getRGB()
		};
		for (int y = 0; y < 12; y++) {
			for (int x = 0; x < 12; x++) {
				img.setRGB(x, y, bg);
			}
		}
		// Place 4 2x2 islands with black gaps between them
		for (int dy = 0; dy < 2; dy++) {
			for (int dx = 0; dx < 2; dx++) {
				img.setRGB(1 + dx, 1 + dy, islands[0]);
				img.setRGB(8 + dx, 1 + dy, islands[1]);
				img.setRGB(1 + dx, 8 + dy, islands[2]);
				img.setRGB(8 + dx, 8 + dy, islands[3]);
			}
		}

		SpatialMergePalettizeImageFilter filter = SpatialMergePalettizeImageFilter.builder()
				.useOklabColorSpace(false)
				.intraClusterMergeThreshold(1.0f)
				.mergeStoppingPredicate(SpatialMergePalettizeImageFilter.colorCountPredicate(3))
				.build();

		BufferedImage result = filter.applyBuffered(img);
		Set<Integer> distinct = collectDistinctRgb(result);
		assertEquals(3, distinct.size(), "Expected 1 background + 2 merged island pairs = 3 colors");
	}

	@Test
	void testColorClusterTryAddMergeAndQuantMultMismatch() {
		ColorCluster c1 = new ColorCluster(0.1, 0.5f);
		assertTrue(c1.tryAddColor(1.0f, 1.0f, 1.0f));
		assertTrue(c1.tryAddColor(1.1f, 1.0f, 1.0f));
		assertFalse(c1.tryAddColor(2.5f, 1.0f, 1.0f), "Color beyond distanceThreshold should be rejected");

		ColorCluster c2 = new ColorCluster(0.1, 0.5f);
		assertTrue(c2.tryAddColor(1.2f, 1.0f, 1.0f));

		ColorCluster merged = ColorCluster.mergeClusters(c1, c2);
		double[] center = merged.getCenter().getPoint();
		assertEquals(1.1, center[0], 0.05);
		assertEquals(1.0, center[1], 1e-6);

		ColorCluster mismatched = new ColorCluster(0.2, 0.5f);
		assertThrows(IllegalArgumentException.class, () -> ColorCluster.mergeClusters(c1, mismatched));
	}
}
