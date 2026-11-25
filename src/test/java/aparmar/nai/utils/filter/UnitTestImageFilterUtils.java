package aparmar.nai.utils.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;

@Tag("imageFilter")
class UnitTestImageFilterUtils {

	@Test
	void testColorSpaceConversionsInPlaceAndOutBuffer() {
		Planar<GrayF32> srgb = new Planar<>(GrayF32.class, 4, 2, 4);
		srgb.getBand(0).set(0, 0, 255f);
		srgb.getBand(1).set(0, 0, 128f);
		srgb.getBand(2).set(0, 0, 64f);
		srgb.getBand(3).set(0, 0, 255f);
		srgb.getBand(0).set(1, 0, 200f);
		srgb.getBand(1).set(1, 0, 100f);
		srgb.getBand(2).set(1, 0, 50f);
		srgb.getBand(3).set(1, 0, 0f);

		Planar<GrayF32> outBuf = new Planar<>(GrayF32.class, 1, 1, 1);
		Planar<GrayF32> linear = ImageFilterUtils.srgbToLinearRgb(srgb, outBuf);
		assertEquals(0f, linear.getBand(0).get(1, 0), 1e-5f);
		assertEquals(1f, linear.getBand(0).get(0, 0), 1e-5f);

		ImageFilterUtils.linearRgbToSrgb(linear, linear);
		assertEquals(255f, linear.getBand(0).get(0, 0), 0.1f);
		assertEquals(128f, linear.getBand(1).get(0, 0), 0.1f);
		assertEquals(64f, linear.getBand(2).get(0, 0), 0.1f);
		assertEquals(255f, linear.getBand(3).get(0, 0), 0.1f);

		Planar<GrayF32> oklabCopy = srgb.clone();
		ImageFilterUtils.rgbToOklab(oklabCopy, oklabCopy);
		ImageFilterUtils.oklabtoRgb(oklabCopy, oklabCopy);
		assertEquals(255f, oklabCopy.getBand(0).get(0, 0), 0.5f);
		assertEquals(128f, oklabCopy.getBand(1).get(0, 0), 0.5f);
		assertEquals(64f, oklabCopy.getBand(2).get(0, 0), 0.5f);
	}

	@Test
	void testOklabToRgbClampsOutOfGamutValues() {
		Planar<GrayF32> oklab = new Planar<>(GrayF32.class, 2, 1, 3);
		// Out-of-gamut cyan/teal (would produce negative linear R)
		oklab.getBand(0).set(0, 0, 0.75f);
		oklab.getBand(1).set(0, 0, -0.25f);
		oklab.getBand(2).set(0, 0, 0.05f);
		// Out-of-gamut bright color (would produce > 1.0 linear RGB)
		oklab.getBand(0).set(1, 0, 1.1f);
		oklab.getBand(1).set(1, 0, 0.3f);
		oklab.getBand(2).set(1, 0, 0.3f);

		Planar<GrayF32> rgb = ImageFilterUtils.oklabtoRgb(oklab, null);
		for (int x = 0; x < 2; x++) {
			for (int b = 0; b < 3; b++) {
				float val = rgb.getBand(b).get(x, 0);
				assertTrue(val >= 0f && val <= 255f,
						"Expected clamped sRGB in [0, 255] at x=" + x + ", band=" + b + ", got " + val);
			}
		}
	}
}
