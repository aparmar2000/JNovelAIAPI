package aparmar.nai.data.request;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

import aparmar.nai.TestHelpers;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import lombok.val;

class UnitTestImageVibeEncodeRequest {

	@Test
	void testImageVibeEncodeRequestDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ImageVibeEncodeRequest testInstance1 = ImageVibeEncodeRequest.builder()
				.model(ImageGenModel.ANIME_V4_FULL)
				.build();
		ImageVibeEncodeRequest testInstance2 = ImageVibeEncodeRequest.builder()
				.image(new Base64Image())
				.informationExtracted(0.6f)
				.mask(new Base64Image())
				.model(ImageGenModel.ANIME_V4_CURATED)
				.build();
		
		TestHelpers.autoTestDataAndToBuilderAnnotation(ImageVibeEncodeRequest.class, testInstance1, testInstance2);
	}
	
	@Test
	void testBuilderValidation() {
		val builder = ImageVibeEncodeRequest.builder();
		
		assertDoesNotThrow(()->builder.informationExtracted(0.8f));
		assertDoesNotThrow(()->builder.informationExtracted(0));
		assertDoesNotThrow(()->builder.informationExtracted(1));
		assertThrows(IllegalArgumentException.class, ()->builder.informationExtracted(-1), "Builder should throw an error if informationExtracted<0");
		assertThrows(IllegalArgumentException.class, ()->builder.informationExtracted(10), "Builder should throw an error if informationExtracted>1");

		assertDoesNotThrow(()->builder.model(ImageGenModel.ANIME_V4_FULL));
		assertThrows(IllegalArgumentException.class, ()->builder.model(ImageGenModel.ANIME_V3_INPAINT), "Builder should throw an error if an unsupported model is used");
	}

}
