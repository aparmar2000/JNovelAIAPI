package aparmar.nai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import aparmar.nai.data.request.Base64Image;
import aparmar.nai.data.request.imagen.ImageUpscaleEnhanceRequest;
import aparmar.nai.data.response.ImageSetWrapper;
import aparmar.nai.utils.InternalResourceLoader;

class IntegrationTestImageUpscaling extends AbstractFeatureIntegrationTest {
	
	@EnabledIfEnvironmentVariable(named = "allowNonFreeTests", matches = "True")
	@Test
	void testImageUpscalingEnhanced() throws AssertionError, Exception {
		TestHelpers.runTestToleratingTimeouts(3, 1000, ()->{
			BufferedImage baseImage = ImageIO.read(InternalResourceLoader.getInternalResourceAsStream("sample_lowres.jpg"));
			
			ImageUpscaleEnhanceRequest request = ImageUpscaleEnhanceRequest.builder()
					.image(new Base64Image(baseImage))
					.build();
			ImageSetWrapper result = apiInstance.upscaleEnhanceImage(request);
			
			assertNotNull(result);
			assertEquals(1, result.getImageCount());
			IIOImage resultImage = result.getImage(0);
			assertNotNull(resultImage);
			result.writeImageToFile(0, new File(TestConstants.TEST_IMAGE_FOLDER+"upscale_enhance_test.png"));
			
			assertEquals(getExpectedScale(baseImage.getHeight()*2), resultImage.getRenderedImage().getHeight());
			assertEquals(getExpectedScale(baseImage.getWidth()*2), resultImage.getRenderedImage().getWidth());
		});
	}
	
	public static int getExpectedScale(int rawScale) {
		return (int) (32*(Math.floor(rawScale/32.0)));
	}

}
