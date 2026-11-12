package aparmar.nai.utils.filter;

import org.apache.commons.math3.ml.clustering.Clusterable;

import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import static aparmar.nai.utils.filter.ImageFilterUtils.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class PalettizeImageFilter implements IPalettizeImageFilter {
	@Builder.Default
	protected boolean useOklabColorSpace = true;

	@Override
	public Planar<GrayF32> applyPlanar(Planar<GrayF32> rgba) {
		Planar<GrayF32> workingPlanar = rgba;
		if (useOklabColorSpace) {
			workingPlanar = rgbToOklab(workingPlanar, null);
		}

		workingPlanar = applyPalettization(workingPlanar);

		if (useOklabColorSpace) {
			workingPlanar = oklabtoRgb(workingPlanar, workingPlanar);
		}
		return workingPlanar;
	}

	protected abstract Planar<GrayF32> applyPalettization(Planar<GrayF32> colorPlanar);

	public static class ColorPoint implements Clusterable {
		protected final double[] color;

		public ColorPoint(double c1, double c2, double c3) {
			color = new double[] { c1, c2, c3 };
		}

		public ColorPoint(Clusterable clusterable) {
			color = clusterable.getPoint();
		}

		@Override
		public double[] getPoint() {
			return color;
		}
	}

	@Value
	protected static class QuantizedColor {
		double quantMult;
		long[] color;

		public float[] getUnquantColor() {
			return new float[] {
					(float) (color[0] * quantMult),
					(float) (color[1] * quantMult),
					(float) (color[2] * quantMult)
			};
		}

		public double[] getUnquantColorDouble() {
			return new double[] {
					color[0] * quantMult,
					color[1] * quantMult,
					color[2] * quantMult
			};
		}

		public static QuantizedColor fromColor(float c1, float c2, float c3, double quant) {
			return new QuantizedColor(quant, new long[] {
					Math.round(c1 / quant),
					Math.round(c2 / quant),
					Math.round(c3 / quant)
			});
		}
	}
}
