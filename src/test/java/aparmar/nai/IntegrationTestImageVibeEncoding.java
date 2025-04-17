package aparmar.nai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import aparmar.nai.data.request.Base64Image;
import aparmar.nai.data.request.ImageVibeEncodeRequest;
import aparmar.nai.data.request.V4VibeData;
import aparmar.nai.data.request.V4VibeData.VibeEncodingType;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import aparmar.nai.utils.InternalResourceLoader;

class IntegrationTestImageVibeEncoding extends AbstractFeatureIntegrationTest {

	@EnabledIfEnvironmentVariable(named = "allowNonFreeTests", matches = "True")
	@Test
	void testImageVibeEncoding() throws AssertionError, Exception {
		TestHelpers.runTestToleratingTimeouts(3, 1000, ()->{
			BufferedImage baseImage = ImageIO.read(InternalResourceLoader.getInternalResourceAsStream("sample_lowres.jpg"));
			Base64Image baseImage64 = new Base64Image(baseImage);
			
			ImageVibeEncodeRequest request = ImageVibeEncodeRequest.builder()
					.image(baseImage64)
					.model(ImageGenModel.ANIME_V4_FULL)
					.build();
			V4VibeData result = apiInstance.encodeImageVibe(request);
			
			assertNotNull(result);
			assertEquals(1, result.getInfoExtracted());
			assertEquals(VibeEncodingType.V4_FULL, result.getEncodingType());
			assertEquals(baseImage64.generateSha256(), result.getSourceHash());
			assertNotNull(result.getEncoding());
			assertTrue(result.getEncoding().length>0, "Result encoding should not be empty");
		});
	}

}
