package aparmar.nai.data.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import aparmar.nai.utils.InternalResourceLoader;

class UnitTestBase64Image {

	@Test
	void testSerializationNoResize() throws FileNotFoundException, IOException {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Base64Image.class, new Base64Image())
				.create();
		BufferedImage baseImage = ImageIO.read(InternalResourceLoader.getInternalResourceAsStream("sample_lowres.jpg"));
		
		Base64Image testBase64Image = new Base64Image(baseImage);
		Base64Image result = gson.fromJson(gson.toJson(testBase64Image), Base64Image.class);
		BufferedImage resultImg = result.getImage();
		
		assertEquals(baseImage.getWidth(), resultImg.getWidth());
		assertEquals(baseImage.getHeight(), resultImg.getHeight());
		for (int x=0;x<baseImage.getWidth();x++) {
			for (int y=0;y<baseImage.getHeight();y++) {
				assertEquals(
						baseImage.getRGB(x, y), 
						resultImg.getRGB(x, y),
						"Pixel mismatch at ("+x+","+y+")");
			}
		}
	}

	@Test
	void testHashing() throws FileNotFoundException, IOException {
		BufferedImage baseImage = ImageIO.read(InternalResourceLoader.getInternalResourceAsStream("sample_lowres.jpg"));
		Base64Image testBase64Image = new Base64Image(baseImage);
		
		String sha256 = testBase64Image.generateSha256();
		assertEquals("401924e7a87f8904a338996efa5ae50e8ddc45e3e656acac4b5b51b81a663879", sha256);
	}

}
