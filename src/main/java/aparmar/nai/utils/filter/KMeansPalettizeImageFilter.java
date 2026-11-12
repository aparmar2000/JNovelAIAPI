package aparmar.nai.utils.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import boofcv.concurrency.BoofConcurrency;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class KMeansPalettizeImageFilter extends PalettizeImageFilter {
	@Builder.Default
	protected int colorCount = 8;
	@Builder.Default
	protected int maxIterations = 100;

	@Override
	protected Planar<GrayF32> applyPalettization(Planar<GrayF32> colorPlanar) {
		return palettizeKMeans(colorPlanar, colorCount, maxIterations);
	}

	protected Planar<GrayF32> palettizeKMeans(Planar<GrayF32> colorPlanar, int colorCount, int maxIterations) {
		List<ColorPoint> colors = extractColors(colorPlanar);
		KMeansPlusPlusClusterer<ColorPoint> clusterer = new KMeansPlusPlusClusterer<>(colorCount, maxIterations, new EuclideanDistance());
		List<CentroidCluster<ColorPoint>> clusterResult = clusterer.cluster(colors);

		return quantizeColors(colorPlanar, clusterResult.stream().map(c -> new ColorPoint(c.getCenter())).collect(Collectors.toList()));
	}

	protected List<ColorPoint> extractColors(Planar<GrayF32> colorPlanar) {
		GrayF32 C1 = colorPlanar.getBand(0);
		GrayF32 C2 = colorPlanar.getBand(1);
		GrayF32 C3 = colorPlanar.getBand(2);

		List<ColorPoint> colorPointList = new ArrayList<>(colorPlanar.width * colorPlanar.height);
		for (int row = 0; row < colorPlanar.height; row++) {
			int index = C1.startIndex + row * C1.stride;

			for (int col = 0; col < colorPlanar.width; col++, index++) {
				colorPointList.add(new ColorPoint(C1.data[index], C2.data[index], C3.data[index]));
			}
		}

		return colorPointList;
	}

	protected Planar<GrayF32> quantizeColors(Planar<GrayF32> colorPlanar, Collection<ColorPoint> colorPoints) {
		GrayF32 C1 = colorPlanar.getBand(0);
		GrayF32 C2 = colorPlanar.getBand(1);
		GrayF32 C3 = colorPlanar.getBand(2);

		EuclideanDistance distance = new EuclideanDistance();
		BoofConcurrency.loopFor(0, colorPlanar.height, row -> {
			int index = C1.startIndex + row * C1.stride;

			for (int col = 0; col < colorPlanar.width; col++, index++) {
				ColorPoint initial = new ColorPoint(C1.data[index], C2.data[index], C3.data[index]);

				ColorPoint selected = null;
				double bestDist = Double.POSITIVE_INFINITY;
				for (ColorPoint paletteColor : colorPoints) {
					double dist = distance.compute(initial.color, paletteColor.color);
					if (dist < bestDist) {
						bestDist = dist;
						selected = paletteColor;
					}
				}

				C1.data[index] = (float) selected.color[0];
				C2.data[index] = (float) selected.color[1];
				C3.data[index] = (float) selected.color[2];
			}
		});

		return colorPlanar;
	}
}
