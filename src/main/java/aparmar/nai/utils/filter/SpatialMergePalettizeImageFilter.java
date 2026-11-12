package aparmar.nai.utils.filter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.function.BiPredicate;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.jetbrains.annotations.Nullable;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import static aparmar.nai.utils.filter.ImageFilterUtils.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SpatialMergePalettizeImageFilter extends PalettizeImageFilter {
	@Builder.Default
	protected float intraClusterMergeThreshold = 0.005f;
	@Builder.Default
	protected BiPredicate<Integer, Float> mergeStoppingPredicate = similarityThresholdPredicate(0.01f);
	
	public static BiPredicate<Integer, Float> similarityThresholdPredicate(float similarityLimit) {
		return (count, similarity) -> similarity < similarityLimit;
	}
	/**
	 * Note: This quantization algorithm is not very good with low color counts.
	 */
	public static BiPredicate<Integer, Float> colorCountPredicate(int targetCount) {
		return (count, similarity) -> count <= targetCount;
	}

	@Override
	protected Planar<GrayF32> applyPalettization(Planar<GrayF32> colorPlanar) {
		return palettizeSpatialMerge(colorPlanar, intraClusterMergeThreshold, mergeStoppingPredicate);
	}

	@Data
	protected static class ColorCluster {
		protected static final EuclideanDistance DISTANCE = new EuclideanDistance();

		protected final double quantMult;
		protected final float distanceThreshold;
		protected HashMap<QuantizedColor, Integer> colorCounter = new HashMap<>();

		public boolean tryAddColor(float c1, float c2, float c3) {
			double[] color = new double[] { c1, c2, c3 };
			for (QuantizedColor existingColor : colorCounter.keySet()) {
				double dist = DISTANCE.compute(color, existingColor.getUnquantColorDouble());
				if (dist > distanceThreshold) {
					return false;
				}
			}

			addColor(c1, c2, c3);
			return true;
		}

		protected void addColor(float c1, float c2, float c3) {
			colorCounter.merge(QuantizedColor.fromColor(c1, c2, c3, quantMult), 1, (a, b) -> a + b);
		}

		public static ColorCluster mergeClusters(ColorCluster a, ColorCluster b) {
			if (a.quantMult != b.quantMult) {
				throw new IllegalArgumentException("ColorClusters must have identical quantMult to be merged!");
			}

			ColorCluster newCluster = new ColorCluster(a.quantMult, Math.max(a.distanceThreshold, b.distanceThreshold));
			for (Map.Entry<QuantizedColor, Integer> aColor : a.colorCounter.entrySet()) {
				newCluster.colorCounter.merge(aColor.getKey(), aColor.getValue(), (v1, v2) -> v1 + v2);
			}
			for (Map.Entry<QuantizedColor, Integer> bColor : b.colorCounter.entrySet()) {
				newCluster.colorCounter.merge(bColor.getKey(), bColor.getValue(), (v1, v2) -> v1 + v2);
			}

			return newCluster;
		}

		public ColorPoint getCenter() {
			double[] runningSum = new double[3];
			for (Entry<QuantizedColor, Integer> existingColor : colorCounter.entrySet()) {
				double[] c = existingColor.getKey().getUnquantColorDouble();
				runningSum[0] += c[0] * existingColor.getValue();
				runningSum[1] += c[1] * existingColor.getValue();
				runningSum[2] += c[2] * existingColor.getValue();
			}

			long sizeCount = colorCounter.values().stream().mapToLong(i -> i).sum();
			return new ColorPoint(runningSum[0] / sizeCount, runningSum[1] / sizeCount, runningSum[2] / sizeCount);
		}
	}

	@Value
	protected static class MergeCandidate {
		double distance;
		int a;
		int b;
		int verA;
		int verB;
	}

	protected Planar<GrayF32> palettizeSpatialMerge(Planar<GrayF32> colorPlanar, float intraClusterMergeThreshold,
			BiPredicate<Integer, Float> mergeStoppingPredicate) {
		GrayF32 C1 = colorPlanar.getBand(0);
		GrayF32 C2 = colorPlanar.getBand(1);
		GrayF32 C3 = colorPlanar.getBand(2);
		GrayF32 alpha = colorPlanar.getNumBands() == 4 ? colorPlanar.getBand(3) : null;

		// Assign image pixels to initial clusters
		MutableGraph<Integer> clusterAdjacency = GraphBuilder.undirected().build();
		ArrayList<ColorCluster> clusters = new ArrayList<>();
		int[] clusterIds = new int[C1.data.length];
		for (int row = 0; row < colorPlanar.height; row++) {
			int index = C1.startIndex + row * C1.stride;

			for (int col = 0; col < colorPlanar.width; col++, index++) {
				if (clusterIds[index] != 0) {
					continue;
				}
				if (alpha != null && alpha.data[index] < DEFAULT_ALPHA_CLIP_THRESHOLD) {
					continue;
				}

				ColorCluster cluster = new ColorCluster(Math.max(intraClusterMergeThreshold / 4.0, 1e-6), intraClusterMergeThreshold);
				floodCluster(C1, C2, C3, alpha, clusterIds, clusterAdjacency, cluster, clusters.size() + 1, col, row);
				clusters.add(cluster);
			}
		}

		// Find the clusters with the most similar colors that are NOT spatially adjacent and merge them
		// Repeat until mergeStoppingPredicate satisfied
		int clusterCount = clusters.size();
		int[] parent = new int[clusterCount];
		for (int i = 0; i < clusterCount; i++) {
			parent[i] = i;
		}
		if (clusterCount > 1) {
			mergeNonAdjacentClusters(clusters, clusterAdjacency, parent, mergeStoppingPredicate);
		}

		int[] clusterIdIndirection = new int[clusterCount + 1];
		for (int i = 0; i < clusterCount; i++) {
			int root = i;
			while (parent[root] != root) {
				root = parent[root];
			}
			clusterIdIndirection[i + 1] = root;
		}

		// Assign new colors
		for (int row = 0; row < colorPlanar.height; row++) {
			int index = C1.startIndex + row * C1.stride;

			for (int col = 0; col < colorPlanar.width; col++, index++) {
				if (clusterIds[index] == 0) {
					continue;
				}
				if (alpha != null && alpha.data[index] < DEFAULT_ALPHA_CLIP_THRESHOLD) {
					C1.data[index] = 0;
					C2.data[index] = 0;
					C3.data[index] = 0;
				}

				int clusterId = clusterIdIndirection[clusterIds[index]];
				ColorCluster assignedCluster = clusters.get(clusterId);
				double[] newColor = assignedCluster.getCenter().color;
				C1.data[index] = (float) newColor[0];
				C2.data[index] = (float) newColor[1];
				C3.data[index] = (float) newColor[2];
			}
		}

		return colorPlanar;
	}

	protected void mergeNonAdjacentClusters(ArrayList<ColorCluster> clusters, MutableGraph<Integer> clusterAdjacency,
			int[] parent, BiPredicate<Integer, Float> mergeStoppingPredicate) {
		int clusterCount = clusters.size();
		int activeClusterCount = clusterCount;
		boolean[] active = new boolean[clusterCount];
		int[] version = new int[clusterCount];
		double[][] centers = new double[clusterCount][];
		TreeSet<Integer> sortedByC0 = new TreeSet<>((x, y) -> {
			int cmp = Double.compare(centers[x][0], centers[y][0]);
			return cmp != 0 ? cmp : Integer.compare(x, y);
		});

		for (int i = 0; i < clusterCount; i++) {
			active[i] = true;
			centers[i] = clusters.get(i).getCenter().color;
			sortedByC0.add(i);
		}

		PriorityQueue<MergeCandidate> pq = new PriorityQueue<>(Comparator.comparingDouble(MergeCandidate::getDistance));
		for (int i = 0; i < clusterCount; i++) {
			MergeCandidate cand = findNearestNonAdjacent(i, centers, version, sortedByC0, clusterAdjacency);
			if (cand != null) {
				pq.add(cand);
			}
		}

		while (!pq.isEmpty()) {
			MergeCandidate cand = pq.poll();
			int a = cand.getA();
			int b = cand.getB();
			if (!active[a]) {
				continue;
			}
			if (!active[b] || version[a] != cand.getVerA() || version[b] != cand.getVerB()
					|| clusterAdjacency.hasEdgeConnecting(a, b)) {
				// Recompute a's nearest valid non-adjacent neighbor if this candidate became stale
				if (version[a] == cand.getVerA()) {
					MergeCandidate replacement = findNearestNonAdjacent(a, centers, version, sortedByC0, clusterAdjacency);
					if (replacement != null) {
						pq.add(replacement);
					}
				}
				continue;
			}

			if (mergeStoppingPredicate.test(activeClusterCount, (float) cand.getDistance())) {
				break;
			}

			// Absorb b into a
			active[b] = false;
			activeClusterCount--;
			parent[b] = a;
			sortedByC0.remove(a);
			sortedByC0.remove(b);

			clusters.set(a, ColorCluster.mergeClusters(clusters.get(a), clusters.get(b)));
			centers[a] = clusters.get(a).getCenter().color;
			version[a]++;
			sortedByC0.add(a);

			for (Integer neighbor : new ArrayList<>(clusterAdjacency.adjacentNodes(b))) {
				if (neighbor != a) {
					clusterAdjacency.putEdge(a, neighbor);
				}
			}
			clusterAdjacency.removeNode(b);

			MergeCandidate nextForA = findNearestNonAdjacent(a, centers, version, sortedByC0, clusterAdjacency);
			if (nextForA != null) {
				pq.add(nextForA);
			}
		}
	}

	private static @Nullable MergeCandidate findNearestNonAdjacent(int clusterIdx, double[][] centers, int[] version,
			TreeSet<Integer> sortedByC0, MutableGraph<Integer> clusterAdjacency) {
		double[] cA = centers[clusterIdx];
		double bestDistSq = Double.POSITIVE_INFINITY;
		double bestDist = Double.POSITIVE_INFINITY;
		int bestOther = -1;

		Integer left = sortedByC0.lower(clusterIdx);
		Integer right = sortedByC0.higher(clusterIdx);

		while (left != null || right != null) {
			if (left != null) {
				double d0 = cA[0] - centers[left][0];
				if (d0 * d0 >= bestDistSq) {
					left = null;
				} else {
					if (!clusterAdjacency.hasEdgeConnecting(clusterIdx, left)) {
						double d1 = cA[1] - centers[left][1];
						double d2 = cA[2] - centers[left][2];
						double distSq = d0 * d0 + d1 * d1 + d2 * d2;
						if (distSq < bestDistSq) {
							bestDistSq = distSq;
							bestDist = Math.sqrt(distSq);
							bestOther = left;
						}
					}
					left = sortedByC0.lower(left);
				}
			}
			if (right != null) {
				double d0 = centers[right][0] - cA[0];
				if (d0 * d0 >= bestDistSq) {
					right = null;
				} else {
					if (!clusterAdjacency.hasEdgeConnecting(clusterIdx, right)) {
						double d1 = cA[1] - centers[right][1];
						double d2 = cA[2] - centers[right][2];
						double distSq = d0 * d0 + d1 * d1 + d2 * d2;
						if (distSq < bestDistSq) {
							bestDistSq = distSq;
							bestDist = Math.sqrt(distSq);
							bestOther = right;
						}
					}
					right = sortedByC0.higher(right);
				}
			}
		}

		if (bestOther == -1) {
			return null;
		}
		return new MergeCandidate(bestDist, clusterIdx, bestOther, version[clusterIdx], version[bestOther]);
	}

	protected void floodCluster(GrayF32 C1, GrayF32 C2, GrayF32 C3, @Nullable GrayF32 alpha,
			int[] clusterIds, MutableGraph<Integer> clusterAdjacency,
			ColorCluster cluster, int newClusterId, int x, int y) {
		int width = C1.width;
		int height = C1.height;
		int startIdx = C1.getIndex(x, y);
		int clusterIndex = newClusterId - 1;

		cluster.tryAddColor(C1.data[startIdx], C2.data[startIdx], C3.data[startIdx]);
		clusterIds[startIdx] = newClusterId;

		ArrayDeque<Integer> queue = new ArrayDeque<>();
		queue.add(y * width + x);

		final int[] dx = { -1, 1, 0, 0 };
		final int[] dy = { 0, 0, -1, 1 };

		while (!queue.isEmpty()) {
			int pos = queue.poll();
			int px = pos % width;
			int py = pos / width;

			for (int i = 0; i < 4; i++) {
				int nx = px + dx[i];
				int ny = py + dy[i];
				if (nx < 0 || nx >= width || ny < 0 || ny >= height) {
					continue;
				}
				int nIdx = C1.getIndex(nx, ny);
				if (clusterIds[nIdx] != 0) {
					if (clusterIds[nIdx] != newClusterId) {
						clusterAdjacency.putEdge(clusterIndex, clusterIds[nIdx] - 1);
					}
					continue;
				}
				if (alpha != null && alpha.data[nIdx] < DEFAULT_ALPHA_CLIP_THRESHOLD) {
					continue;
				}

				if (cluster.tryAddColor(C1.data[nIdx], C2.data[nIdx], C3.data[nIdx])) {
					clusterIds[nIdx] = newClusterId;
					queue.add(ny * width + nx);
				}
			}
		}

		clusterAdjacency.addNode(clusterIndex);
	}
}
