package aparmar.nai.data.request;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

import aparmar.nai.data.request.ImageUpscaleRequest.UpscaleFactor;
import aparmar.nai.TestHelpers;

class UnitTestImageUpscaleRequest {

	@Test
	void testImageUpscaleRequestDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ImageUpscaleRequest testInstance1 = ImageUpscaleRequest.builder()
				.image(new Base64Image())
				.build();
		ImageUpscaleRequest testInstance2 = ImageUpscaleRequest.builder()
				.image(new Base64Image(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)))
				.upscaleFactor(UpscaleFactor.FOUR)
				.build();
		TestHelpers.autoTestDataAndToBuilderAnnotation(ImageUpscaleRequest.class, testInstance1, testInstance2);
	}

}
