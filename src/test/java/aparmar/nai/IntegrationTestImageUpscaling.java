package aparmar.nai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import aparmar.nai.data.request.Base64Image;
import aparmar.nai.data.request.ImageUpscaleRequest;
import aparmar.nai.data.request.ImageUpscaleRequest.UpscaleFactor;
import aparmar.nai.data.response.ImageSetWrapper;
import aparmar.nai.utils.InternalResourceLoader;

class IntegrationTestImageUpscaling extends AbstractFeatureIntegrationTest {

	@EnabledIfEnvironmentVariable(named = "allowNonFreeTests", matches = "True")
	@Test
	void testImageUpscaling() throws FileNotFoundException, IOException {
		BufferedImage baseImage = ImageIO.read(InternalResourceLoader.getInternalFile("sample_lowres.jpg"));
		
		ImageUpscaleRequest request = ImageUpscaleRequest.builder()
				.image(new Base64Image(baseImage))
				.upscaleFactor(UpscaleFactor.TWO)
				.build();
		ImageSetWrapper result = apiInstance.upscaleImage(request);
		
		assertNotNull(result);
		assertEquals(1, result.getImageCount());
		IIOImage resultImage = result.getImage(0);
		assertNotNull(resultImage);
		assertEquals(baseImage.getHeight()*2, resultImage.getRenderedImage().getHeight());
		assertEquals(baseImage.getWidth()*2, resultImage.getRenderedImage().getWidth());
		
		result.writeImageToFile(0, new File(TestConstants.TEST_IMAGE_FOLDER+"upscale_test.png"));
	}

}
