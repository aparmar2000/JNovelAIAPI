package aparmar.nai.utils.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.junit.jupiter.api.Test;
import org.w3c.dom.NodeList;

import aparmar.nai.data.response.ImageSetWrapper;
import aparmar.nai.utils.ZipArchiveWrapper;
import boofcv.struct.image.GrayF32;

class UnitTestIImageFilter {

	@Test
	void testApplyImageSetPreservesNamesMetadataAndAppliesFilter() throws IOException {
		byte[] png0 = encodePngWithText(createSolidImage(4, 4, new Color(10, 20, 30)), "Comment", "meta-zero");
		byte[] png1 = encodePngWithText(createSolidImage(4, 4, new Color(40, 50, 60)), "Comment", "meta-one");

		ByteArrayOutputStream zipBaos = new ByteArrayOutputStream();
		try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(zipBaos)) {
			writeZipEntry(zos, "image_0.png", png0);
			writeZipEntry(zos, "nested/image_1.png", png1);
			zos.finish();
		}

		ImageSetWrapper inputSet = new ImageSetWrapper(new ZipArchiveWrapper(zipBaos.toByteArray()));

		// Filter that sets the red channel of every pixel to 200
		IImageFilter setRedFilter = rgbaIn -> {
			GrayF32 red = rgbaIn.getBand(0);
			for (int i = 0; i < red.data.length; i++) {
				red.data[i] = 200f;
			}
			return rgbaIn;
		};

		ImageSetWrapper outputSet = setRedFilter.applyImageSet(inputSet);

		assertEquals(2, outputSet.getImageCount());
		assertEquals("image_0.png", outputSet.getImageName(0));
		assertEquals("nested/image_1.png", outputSet.getImageName(1));

		IIOImage out0 = outputSet.getImage(0);
		BufferedImage buf0 = (BufferedImage) out0.getRenderedImage();
		assertEquals(200, new Color(buf0.getRGB(0, 0)).getRed());
		assertEquals(20, new Color(buf0.getRGB(0, 0)).getGreen());
		assertEquals("meta-zero", extractPngText(out0.getMetadata(), "Comment"));

		IIOImage out1 = outputSet.getImage(1);
		BufferedImage buf1 = (BufferedImage) out1.getRenderedImage();
		assertEquals(200, new Color(buf1.getRGB(0, 0)).getRed());
		assertEquals(50, new Color(buf1.getRGB(0, 0)).getGreen());
		assertEquals("meta-one", extractPngText(out1.getMetadata(), "Comment"));
	}

	private static BufferedImage createSolidImage(int width, int height, Color color) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int rgb = color.getRGB();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				img.setRGB(x, y, rgb);
			}
		}
		return img;
	}

	private static byte[] encodePngWithText(BufferedImage image, String keyword, String text) throws IOException {
		ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageOutputStream ios = new MemoryCacheImageOutputStream(baos)) {
			ImageWriteParam writeParam = writer.getDefaultWriteParam();
			IIOMetadata metadata = writer.getDefaultImageMetadata(ImageTypeSpecifier.createFromRenderedImage(image), writeParam);

			String nativeFormat = metadata.getNativeMetadataFormatName();
			IIOMetadataNode root = new IIOMetadataNode(nativeFormat);
			IIOMetadataNode textNode = new IIOMetadataNode("tEXt");
			IIOMetadataNode entry = new IIOMetadataNode("tEXtEntry");
			entry.setAttribute("keyword", keyword);
			entry.setAttribute("value", text);
			textNode.appendChild(entry);
			root.appendChild(textNode);
			metadata.mergeTree(nativeFormat, root);

			writer.setOutput(ios);
			writer.write(null, new IIOImage(image, null, metadata), writeParam);
			ios.flush();
			return baos.toByteArray();
		} finally {
			writer.dispose();
		}
	}

	private static String extractPngText(IIOMetadata metadata, String keyword) {
		assertNotNull(metadata);
		IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());
		NodeList entries = root.getElementsByTagName("tEXtEntry");
		for (int i = 0; i < entries.getLength(); i++) {
			IIOMetadataNode entry = (IIOMetadataNode) entries.item(i);
			if (keyword.equals(entry.getAttribute("keyword"))) {
				return entry.getAttribute("value");
			}
		}
		return null;
	}

	private static void writeZipEntry(ZipArchiveOutputStream zos, String name, byte[] data) throws IOException {
		ZipArchiveEntry entry = new ZipArchiveEntry(name);
		entry.setSize(data.length);
		zos.putArchiveEntry(entry);
		zos.write(data);
		zos.closeArchiveEntry();
	}
}
