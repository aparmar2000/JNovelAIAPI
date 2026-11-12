package aparmar.nai.utils.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;

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
}
