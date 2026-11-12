package aparmar.nai.utils.filter;

import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;

@FunctionalInterface
public interface IFixedSizeDownsampler {
	/**
	 * 
	 * @param input Input image (not modified)
	 * @param x0 x-coordinate of top-left corner of the sub-image.
	 * @param y0 y-coordinate of top-left corner of the sub-image.
	 * @param x1 x-coordinate of bottom-right corner of the sub-image.
	 * @param y1 y-coordinate of bottom-right corner of the sub-image.
	 * @param out Output image (modified)
	 * @param outIndex Target output pixel index
	 */
	void downsampleTo(Planar<GrayF32> input, int x0, int y0, int x1, int y1, Planar<GrayF32> out, int outIndex);
}
