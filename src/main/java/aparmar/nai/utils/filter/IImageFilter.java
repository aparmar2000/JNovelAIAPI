package aparmar.nai.utils.filter;

import java.awt.image.BufferedImage;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;

public interface IImageFilter {
	public default BufferedImage applyBuffered(BufferedImage inImage) {
		Planar<GrayF32> rgba = ConvertBufferedImage.convertFromPlanar(inImage, null, true, GrayF32.class);
		
		Planar<GrayF32> resultRgba = applyPlanar(rgba);
		
		return ConvertBufferedImage.convertTo_F32(resultRgba, null, true);
		
	}
	public Planar<GrayF32> applyPlanar(Planar<GrayF32> rgbaIn);
	
	public default IImageFilter andThen(IImageFilter next) {
		return new SequentialImageFilter(this, next);
	}
	
	public static IImageFilter noOp() {
		return new NoOpImageFilter();
	}
	public static class NoOpImageFilter implements IImageFilter {
		@Override
		public Planar<GrayF32> applyPlanar(Planar<GrayF32> rgbaIn) {
			return rgbaIn;
		}
		
	}
}
