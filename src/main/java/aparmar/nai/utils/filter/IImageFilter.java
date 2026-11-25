package aparmar.nai.utils.filter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import aparmar.nai.data.response.ImageSetWrapper;
import aparmar.nai.utils.ZipArchiveWrapper;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;

public interface IImageFilter {
	public default ImageSetWrapper applyImageSet(ImageSetWrapper inSet) {
		try {
			ByteArrayOutputStream zipBaos = new ByteArrayOutputStream();
			try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(zipBaos)) {
				for (int i = 0; i < inSet.getImageCount(); i++) {
					IIOImage srcImage = inSet.getImage(i);
					BufferedImage srcBuffered = (BufferedImage) srcImage.getRenderedImage();
					BufferedImage filteredBuffered = applyBuffered(srcBuffered);
					IIOImage outImage = new IIOImage(filteredBuffered, null, srcImage.getMetadata());

					byte[] pngBytes;
					ImageWriter pngWriter = ImageIO.getImageWritersByFormatName("png").next();
					try (ByteArrayOutputStream pngBaos = new ByteArrayOutputStream();
							ImageOutputStream ios = new MemoryCacheImageOutputStream(pngBaos)) {
						pngWriter.setOutput(ios);
						pngWriter.write(outImage);
						ios.flush();
						pngBytes = pngBaos.toByteArray();
					} finally {
						pngWriter.dispose();
					}

					ZipArchiveEntry entry = new ZipArchiveEntry(inSet.getImageName(i));
					entry.setSize(pngBytes.length);
					zos.putArchiveEntry(entry);
					zos.write(pngBytes);
					zos.closeArchiveEntry();
				}
				zos.finish();
			}

			return new ImageSetWrapper(new ZipArchiveWrapper(zipBaos.toByteArray()));
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to apply filter to ImageSetWrapper", e);
		}
	}
	
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
