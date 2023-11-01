package aparmar.nai.data.response;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

import aparmar.nai.utils.ZipArchiveWrapper;

/**
 * A wrapper around a zip file of images, as is generally returned from image endpoints.
 * Reads out full <code>IIOImage</code>s, complete with metadata.
 */
public class ImageSetWrapper {
	
	protected final ZipArchiveWrapper zipArchive;
	protected final IIOImage[] iioImageCache;
	
	public ImageSetWrapper(ZipArchiveWrapper zipArchive) throws IOException {
		this.zipArchive = zipArchive;
		
		for (int i=0;i<zipArchive.getEntryCount();i++) {
			if (!zipArchive.getEntry(i).getName().endsWith(".png")) {
				throw new UnsupportedEncodingException("ImageSetWrapper only supports png files - entry "+i+" is "+zipArchive.getEntry(i).getName());
			}
		}
		iioImageCache = new IIOImage[zipArchive.getEntryCount()];
	}
	
	public int getImageCount() { return zipArchive.getEntryCount(); }
	
	public IIOImage getImage(int idx) throws IOException {
		IIOImage image = iioImageCache[idx];
		
		if (image == null) {
			ImageReader pngReader = ImageIO.getImageReadersByFormatName("png").next();
			
			try (ImageInputStream iis = new MemoryCacheImageInputStream(new ByteArrayInputStream(zipArchive.getEntryBytes(idx)))) {
				pngReader.setInput(iis);
				image = pngReader.readAll(0, new ImageReadParam());
				iioImageCache[idx] = image;
			} finally {
				pngReader.dispose();
			}
		}
		
		return image;
	}
	
	public void writeImageToFile(int idx, File outputFile) throws IOException {
		ImageWriter pngWriter = ImageIO.getImageWritersByFormatName("png").next();
		IIOImage image = getImage(idx);
		
		try (ImageOutputStream ios = new FileImageOutputStream(outputFile)) {
			pngWriter.setOutput(ios);
			pngWriter.write(image);
		} finally {
			pngWriter.dispose();
		}
	}
}
