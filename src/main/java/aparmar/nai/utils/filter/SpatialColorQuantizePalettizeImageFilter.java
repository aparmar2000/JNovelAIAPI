package aparmar.nai.utils.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.util.FastMath;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Floats;

import boofcv.alg.filter.convolve.GConvolveImageOps;
import boofcv.factory.filter.kernel.FactoryKernelGaussian;
import boofcv.struct.border.BorderType;
import boofcv.struct.convolve.Kernel1D_F32;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import static aparmar.nai.utils.filter.ImageFilterUtils.DEFAULT_ALPHA_CLIP_THRESHOLD;
import static aparmar.nai.utils.filter.ImageFilterUtils.linearRgbToSrgb;
import static aparmar.nai.utils.filter.ImageFilterUtils.srgbToLinearRgb;

/**
 * Differentiable Spatial Color Quantization implementation of {@link PalettizeImageFilter}.
 * <p>
 * Jointly optimizes the color palette and per-pixel assignment logits using analytical
 * gradient descent (Adam) with multi-scale Gaussian spatial perceptual blur, temperature-annealed
 * softmax, entropy regularization, and palette sparsity pruning.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SpatialColorQuantizePalettizeImageFilter extends PalettizeImageFilter implements IColorCountPalettizeImageFilter {
	@Builder.Default
	protected int colorCount = 8;
	@Builder.Default
	protected int steps = 150;
	@Builder.Default
	protected double startTau = 1.0;
	@Builder.Default
	protected double endTau = 0.05;
	@Builder.Default
	protected double paletteLr = 0.02;
	@Builder.Default
	protected double logitLr = 0.15;
	@Builder.Default
	protected double maxEntropyWeight = 0.05;
	@Builder.Default
	protected double pruneWeight = 0.01;
	@Builder.Default
	protected List<Double> blurSigmas = ImmutableList.of(0.5, 1.0, 2.0);
	@Builder.Default
	protected long seed = 42L;

	private static final class FloatAdam {
		private final float[] m;
		private final float[] v;
		private final float lr;
		private int t = 0;

		FloatAdam(int size, float lr) {
			this.m = new float[size];
			this.v = new float[size];
			this.lr = lr;
		}

		void step(float[] params, float[] grads) {
			t++;
			float beta1Correction = 1.0f - (float) FastMath.pow(0.9, t);
			float beta2Correction = 1.0f - (float) FastMath.pow(0.999, t);

			for (int i = 0; i < params.length; i++) {
				float g = grads[i];
				m[i] = 0.9f * m[i] + 0.1f * g;
				v[i] = 0.999f * v[i] + 0.001f * g * g;

				float mHat = m[i] / beta1Correction;
				float vHat = v[i] / beta2Correction;
				params[i] -= lr * mHat / ((float) FastMath.sqrt(vHat) + 1e-8f);
			}
		}
	}

	@Override
	protected Planar<GrayF32> applyPalettization(Planar<GrayF32> colorPlanar) {
		if (!useOklabColorSpace) {
			srgbToLinearRgb(colorPlanar, colorPlanar);
		}
		palettizeSpatialQuantize(colorPlanar);
		if (!useOklabColorSpace) {
			linearRgbToSrgb(colorPlanar, colorPlanar);
		}
		return colorPlanar;
	}

	protected Planar<GrayF32> palettizeSpatialQuantize(Planar<GrayF32> colorPlanar) {
		Preconditions.checkArgument(colorCount >= 1, "colorCount must be >= 1");
		Preconditions.checkArgument(steps >= 1, "steps must be >= 1");
		Preconditions.checkArgument(startTau >= endTau && endTau > 0, "Invalid temperature schedule");
		Preconditions.checkArgument(blurSigmas != null && !blurSigmas.isEmpty(), "At least one blur sigma required");

		int width = colorPlanar.width;
		int height = colorPlanar.height;
		int numPixels = width * height;
		if (numPixels == 0) {
			return colorPlanar;
		}

		GrayF32 c0 = colorPlanar.getBand(0);
		GrayF32 c1 = colorPlanar.getBand(1);
		GrayF32 c2 = colorPlanar.getBand(2);
		GrayF32 alpha = colorPlanar.getNumBands() == 4 ? colorPlanar.getBand(3) : null;

		float[] target0 = new float[numPixels];
		float[] target1 = new float[numPixels];
		float[] target2 = new float[numPixels];
		boolean[] opaque = new boolean[numPixels];
		float[] minCh = { Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY };
		float[] maxCh = { Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY };
		double[] mean = new double[3];
		int opaqueCount = 0;

		for (int row = 0; row < height; row++) {
			int srcIdx = c0.startIndex + row * c0.stride;
			int dstBase = row * width;
			for (int col = 0; col < width; col++, srcIdx++) {
				int pIdx = dstBase + col;
				if (alpha != null && alpha.data[srcIdx] < DEFAULT_ALPHA_CLIP_THRESHOLD) {
					continue;
				}
				opaque[pIdx] = true;
				opaqueCount++;
				float v0 = c0.data[srcIdx];
				float v1 = c1.data[srcIdx];
				float v2 = c2.data[srcIdx];
				target0[pIdx] = v0;
				target1[pIdx] = v1;
				target2[pIdx] = v2;
				mean[0] += v0;
				mean[1] += v1;
				mean[2] += v2;
				if (v0 < minCh[0]) minCh[0] = v0;
				if (v0 > maxCh[0]) maxCh[0] = v0;
				if (v1 < minCh[1]) minCh[1] = v1;
				if (v1 > maxCh[1]) maxCh[1] = v1;
				if (v2 < minCh[2]) minCh[2] = v2;
				if (v2 > maxCh[2]) maxCh[2] = v2;
			}
		}

		if (opaqueCount == 0) {
			return colorPlanar;
		}

		mean[0] /= opaqueCount;
		mean[1] /= opaqueCount;
		mean[2] /= opaqueCount;

		if (colorCount == 1 || (maxCh[0] - minCh[0] < 1e-6f && maxCh[1] - minCh[1] < 1e-6f && maxCh[2] - minCh[2] < 1e-6f)) {
			writeConstantColor(c0, c1, c2, alpha, opaque, width, height,
					(float) mean[0], (float) mean[1], (float) mean[2]);
			return colorPlanar;
		}

		int kMax = colorCount;

		// Seed initial palette C (K x 3) using K-Means++
		RealMatrix palette = initializePaletteKMeans(target0, target1, target2, opaque, opaqueCount, kMax, seed);
		float[] cFlat = flattenMatrix(palette); // Row-major [k * 3 + channel]

		// Initialize assignment logits L (N x K) biased toward nearest palette entry
		float[] logits = new float[numPixels * kMax];
		float[] weights = new float[numPixels * kMax];
		float[] gradLogits = new float[numPixels * kMax];
		float[] gradCFlat = new float[kMax * 3];

		for (int i = 0; i < numPixels; i++) {
			if (!opaque[i]) {
				continue;
			}
			float r = target0[i], g = target1[i], b = target2[i];
			int rowOffset = i * kMax;
			for (int k = 0; k < kMax; k++) {
				float dr = r - cFlat[k * 3];
				float dg = g - cFlat[k * 3 + 1];
				float db = b - cFlat[k * 3 + 2];
				logits[rowOffset + k] = -10.0f * (dr * dr + dg * dg + db * db);
			}
		}

		// Precompute fused forward+backward 1D Gaussian kernels: G_sigma * G_sigma = G_{sqrt(2)*sigma}
		List<Kernel1D_F32> fusedKernels = new ArrayList<>(blurSigmas.size());
		for (double sigma : blurSigmas) {
			double fusedSigma = FastMath.sqrt(2.0) * sigma;
			fusedKernels.add(FactoryKernelGaussian.gaussian1D(GrayF32.class, fusedSigma, -1));
		}

		// Reusable BoofCV work buffers
		Planar<GrayF32> residual = new Planar<>(GrayF32.class, width, height, 3);
		GrayF32 scratchBand = new GrayF32(width, height);
		GrayF32 blurredBand = new GrayF32(width, height);
		float[] gradIHat0 = new float[numPixels];
		float[] gradIHat1 = new float[numPixels];
		float[] gradIHat2 = new float[numPixels];
		float[] usage = new float[kMax];
		float[] gradWRow = new float[kMax];

		FloatAdam optC = new FloatAdam(kMax * 3, (float) paletteLr);
		FloatAdam optL = new FloatAdam(numPixels * kMax, (float) logitLr);

		// Analytical Gradient Descent Loop
		for (int step = 0; step < steps; step++) {
			double progress = (steps == 1) ? 1.0 : (double) step / (steps - 1);
			float tau = (float) (startTau * FastMath.pow(endTau / startTau, progress));
			float lambdaEnt = (float) (0.1 * maxEntropyWeight + 0.9 * maxEntropyWeight * progress);
			float lambdaPrune = (float) pruneWeight;

			Arrays.fill(usage, 0.0f);
			Arrays.fill(gradCFlat, 0.0f);
			Arrays.fill(gradIHat0, 0.0f);
			Arrays.fill(gradIHat1, 0.0f);
			Arrays.fill(gradIHat2, 0.0f);

			float[] res0 = residual.getBand(0).data;
			float[] res1 = residual.getBand(1).data;
			float[] res2 = residual.getBand(2).data;

			// --- FORWARD PASS: Softmax(L / tau) & Residual = W*C - I ---
			for (int i = 0; i < numPixels; i++) {
				if (!opaque[i]) {
					res0[i] = 0.0f;
					res1[i] = 0.0f;
					res2[i] = 0.0f;
					continue;
				}
				int rowOffset = i * kMax;

				float maxVal = Float.NEGATIVE_INFINITY;
				for (int k = 0; k < kMax; k++) {
					float scaled = logits[rowOffset + k] / tau;
					if (scaled > maxVal) maxVal = scaled;
				}

				float sumExp = 0.0f;
				for (int k = 0; k < kMax; k++) {
					float e = (float) FastMath.exp((logits[rowOffset + k] / tau) - maxVal);
					weights[rowOffset + k] = e;
					sumExp += e;
				}

				float invSum = 1.0f / sumExp;
				float hat0 = 0.0f, hat1 = 0.0f, hat2 = 0.0f;

				for (int k = 0; k < kMax; k++) {
					float w = weights[rowOffset + k] * invSum;
					weights[rowOffset + k] = w;
					usage[k] += w;

					hat0 += w * cFlat[k * 3];
					hat1 += w * cFlat[k * 3 + 1];
					hat2 += w * cFlat[k * 3 + 2];
				}

				res0[i] = hat0 - target0[i];
				res1[i] = hat1 - target1[i];
				res2[i] = hat2 - target2[i];
			}

			for (int k = 0; k < kMax; k++) {
				usage[k] /= opaqueCount;
			}

			// --- BACKWARD PASS 1: Spatial Perceptual Gradient via BoofCV Separable Blur ---
			for (Kernel1D_F32 kernel : fusedKernels) {
				accumulateBlurredBand(residual.getBand(0), kernel, scratchBand, blurredBand, gradIHat0);
				accumulateBlurredBand(residual.getBand(1), kernel, scratchBand, blurredBand, gradIHat1);
				accumulateBlurredBand(residual.getBand(2), kernel, scratchBand, blurredBand, gradIHat2);
			}

			// --- BACKWARD PASS 2: Palette Gradient (W^T * gradIHat) & Logit Softmax VJP ---
			float entScale = lambdaEnt / opaqueCount;
			float pruneScale = (lambdaPrune / opaqueCount) * 0.5f;

			for (int i = 0; i < numPixels; i++) {
				if (!opaque[i]) {
					continue;
				}
				int rowOffset = i * kMax;
				float g0 = gradIHat0[i];
				float g1 = gradIHat1[i];
				float g2 = gradIHat2[i];

				float rowDot = 0.0f;
				for (int k = 0; k < kMax; k++) {
					float w = weights[rowOffset + k];

					// Accumulate gradC
					gradCFlat[k * 3] += w * g0;
					gradCFlat[k * 3 + 1] += w * g1;
					gradCFlat[k * 3 + 2] += w * g2;

					// Compute gradW[k] = spatial + pixel entropy + usage sparsity
					float spatialGrad = g0 * cFlat[k * 3] + g1 * cFlat[k * 3 + 1] + g2 * cFlat[k * 3 + 2];
					float entropyGrad = -entScale * ((float) FastMath.log(w + 1e-8f) + 1.0f);
					float pruneGrad = pruneScale * (float) FastMath.pow(usage[k] + 1e-6f, -0.5);

					float gw = spatialGrad + entropyGrad + pruneGrad;
					gradWRow[k] = gw;
					rowDot += w * gw;
				}

				// Softmax Jacobian-Vector Product
				for (int k = 0; k < kMax; k++) {
					float w = weights[rowOffset + k];
					gradLogits[rowOffset + k] = (w * (gradWRow[k] - rowDot)) / tau;
				}
			}

			// --- ADAM STEP & CLAMP ---
			optC.step(cFlat, gradCFlat);
			optL.step(logits, gradLogits);

			for (int k = 0; k < kMax; k++) {
				cFlat[k * 3] = Floats.constrainToRange(cFlat[k * 3], minCh[0], maxCh[0]);
				cFlat[k * 3 + 1] = Floats.constrainToRange(cFlat[k * 3 + 1], minCh[1], maxCh[1]);
				cFlat[k * 3 + 2] = Floats.constrainToRange(cFlat[k * 3 + 2], minCh[2], maxCh[2]);
			}
		}

		// Hard Assignment & Write Output into colorPlanar
		for (int row = 0; row < height; row++) {
			int dstIdx = c0.startIndex + row * c0.stride;
			int pBase = row * width;
			for (int col = 0; col < width; col++, dstIdx++) {
				int i = pBase + col;
				if (!opaque[i]) {
					if (alpha != null) {
						c0.data[dstIdx] = 0.0f;
						c1.data[dstIdx] = 0.0f;
						c2.data[dstIdx] = 0.0f;
					}
					continue;
				}
				int rowOffset = i * kMax;
				int bestK = 0;
				float bestLogit = logits[rowOffset];
				for (int k = 1; k < kMax; k++) {
					if (logits[rowOffset + k] > bestLogit) {
						bestLogit = logits[rowOffset + k];
						bestK = k;
					}
				}
				c0.data[dstIdx] = cFlat[bestK * 3];
				c1.data[dstIdx] = cFlat[bestK * 3 + 1];
				c2.data[dstIdx] = cFlat[bestK * 3 + 2];
			}
		}

		return colorPlanar;
	}

	private static void accumulateBlurredBand(
			GrayF32 source,
			Kernel1D_F32 kernel,
			GrayF32 scratch,
			GrayF32 dest,
			float[] gradAccumulator
	) {
		GConvolveImageOps.horizontal(kernel, source, scratch, BorderType.EXTENDED);
		GConvolveImageOps.vertical(kernel, scratch, dest, BorderType.EXTENDED);
		float[] blurred = dest.data;
		for (int i = 0; i < blurred.length; i++) {
			gradAccumulator[i] += blurred[i];
		}
	}

	private static RealMatrix initializePaletteKMeans(
			float[] c0, float[] c1, float[] c2, boolean[] opaque, int opaqueCount, int k, long seed
	) {
		int n = c0.length;
		int maxSamples = Math.min(opaqueCount, 16_384);
		Random rng = new Random(seed);
		List<DoublePoint> points = new ArrayList<>(maxSamples);

		if (maxSamples == opaqueCount) {
			for (int i = 0; i < n; i++) {
				if (opaque[i]) {
					points.add(new DoublePoint(new double[] { c0[i], c1[i], c2[i] }));
				}
			}
		} else {
			while (points.size() < maxSamples) {
				int idx = rng.nextInt(n);
				if (opaque[idx]) {
					points.add(new DoublePoint(new double[] { c0[idx], c1[idx], c2[idx] }));
				}
			}
		}

		int clusterK = Math.min(k, points.size());
		KMeansPlusPlusClusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<>(
				clusterK, 30, new EuclideanDistance(), new JDKRandomGenerator((int) seed));
		List<CentroidCluster<DoublePoint>> clusters = clusterer.cluster(points);

		RealMatrix palette = new Array2DRowRealMatrix(k, 3);
		for (int i = 0; i < k; i++) {
			if (i < clusters.size()) {
				palette.setRow(i, clusters.get(i).getCenter().getPoint());
			} else {
				palette.setRow(i, points.get(rng.nextInt(points.size())).getPoint());
			}
		}
		return palette;
	}

	private static float[] flattenMatrix(RealMatrix matrix) {
		int rows = matrix.getRowDimension();
		float[] flat = new float[rows * 3];
		for (int r = 0; r < rows; r++) {
			flat[r * 3] = (float) matrix.getEntry(r, 0);
			flat[r * 3 + 1] = (float) matrix.getEntry(r, 1);
			flat[r * 3 + 2] = (float) matrix.getEntry(r, 2);
		}
		return flat;
	}

	private static void writeConstantColor(
			GrayF32 c0, GrayF32 c1, GrayF32 c2, GrayF32 alpha,
			boolean[] opaque, int width, int height, float v0, float v1, float v2
	) {
		for (int row = 0; row < height; row++) {
			int dstIdx = c0.startIndex + row * c0.stride;
			int pBase = row * width;
			for (int col = 0; col < width; col++, dstIdx++) {
				if (!opaque[pBase + col]) {
					if (alpha != null) {
						c0.data[dstIdx] = 0.0f;
						c1.data[dstIdx] = 0.0f;
						c2.data[dstIdx] = 0.0f;
					}
					continue;
				}
				c0.data[dstIdx] = v0;
				c1.data[dstIdx] = v1;
				c2.data[dstIdx] = v2;
			}
		}
	}
}
