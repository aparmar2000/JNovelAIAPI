package aparmar.nai.data.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import aparmar.nai.TestHelpers;
import aparmar.nai.data.request.imgaug.ImageAugmentColorizeRequest;
import aparmar.nai.data.request.imgaug.ImageAugmentDeclutterRequest;
import aparmar.nai.data.request.imgaug.ImageAugmentEmotionRequest;
import aparmar.nai.data.request.imgaug.ImageAugmentEmotionRequest.Emotion;
import aparmar.nai.data.request.imgaug.ImageAugmentEmotionRequest.EmotionStrength;
import aparmar.nai.data.request.imgaug.ImageAugmentLineArtRequest;
import aparmar.nai.data.request.imgaug.ImageAugmentRemoveBackgroundRequest;
import aparmar.nai.data.request.imgaug.ImageAugmentRequest.DefryFactor;
import aparmar.nai.data.request.imgaug.ImageAugmentSketchRequest;

class UnitTestImageAugmentRequest {

    @Nested
    @DisplayName("@Data annotations work properly")
    class DataAnnotationTests {
		@Test
		void testImageAugmentRemoveBackgroundRequestDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			ImageAugmentRemoveBackgroundRequest testInstance1 = ImageAugmentRemoveBackgroundRequest.builder()
					.image(new Base64Image())
					.build();
			ImageAugmentRemoveBackgroundRequest testInstance2 = ImageAugmentRemoveBackgroundRequest.builder()
					.image(new Base64Image(new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)))
					.build();
			TestHelpers.autoTestDataAndToBuilderAnnotation(ImageAugmentRemoveBackgroundRequest.class, testInstance1, testInstance2);
		}
	
		@Test
		void testImageAugmentLineArtRequestDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			ImageAugmentLineArtRequest testInstance1 = ImageAugmentLineArtRequest.builder()
					.image(new Base64Image())
					.build();
			ImageAugmentLineArtRequest testInstance2 = ImageAugmentLineArtRequest.builder()
					.image(new Base64Image(new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)))
					.build();
			TestHelpers.autoTestDataAndToBuilderAnnotation(ImageAugmentLineArtRequest.class, testInstance1, testInstance2);
		}
	
		@Test
		void testImageImageAugmentSketchRequestDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			ImageAugmentSketchRequest testInstance1 = ImageAugmentSketchRequest.builder()
					.image(new Base64Image())
					.build();
			ImageAugmentSketchRequest testInstance2 = ImageAugmentSketchRequest.builder()
					.image(new Base64Image(new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)))
					.build();
			TestHelpers.autoTestDataAndToBuilderAnnotation(ImageAugmentSketchRequest.class, testInstance1, testInstance2);
		}
	
		@Test
		void testImageAugmentColorizeRequestDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			ImageAugmentColorizeRequest testInstance1 = ImageAugmentColorizeRequest.builder()
					.image(new Base64Image())
					.build();
			ImageAugmentColorizeRequest testInstance2 = ImageAugmentColorizeRequest.builder()
					.image(new Base64Image(new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)))
					.defryFactor(DefryFactor.TWO)
					.prompt("test")
					.build();
			TestHelpers.autoTestDataAndToBuilderAnnotation(ImageAugmentColorizeRequest.class, testInstance1, testInstance2);
		}
	
		@Test
		void testImageAugmentEmotionRequestDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			ImageAugmentEmotionRequest testInstance1 = ImageAugmentEmotionRequest.builder()
					.image(new Base64Image())
					.emotion(Emotion.NEUTRAL)
					.build();
			ImageAugmentEmotionRequest testInstance2 = ImageAugmentEmotionRequest.builder()
					.image(new Base64Image(new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)))
					.emotion(Emotion.HAPPY)
					.emotionStrength(EmotionStrength.WEAK)
					.prompt("test")
					.build();
			TestHelpers.autoTestDataAndToBuilderAnnotation(ImageAugmentEmotionRequest.class, testInstance1, testInstance2);
		}
	
		@Test
		void testImageAugmentDeclutterRequestDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			ImageAugmentDeclutterRequest testInstance1 = ImageAugmentDeclutterRequest.builder()
					.image(new Base64Image())
					.build();
			ImageAugmentDeclutterRequest testInstance2 = ImageAugmentDeclutterRequest.builder()
					.image(new Base64Image(new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)))
					.build();
			TestHelpers.autoTestDataAndToBuilderAnnotation(ImageAugmentDeclutterRequest.class, testInstance1, testInstance2);
		}
    }

	@Test
    void testDefryFactorSerialization() {
    	Gson gson = new GsonBuilder()
    			.registerTypeAdapter(DefryFactor.class, DefryFactor.ZERO)
    			.create();
    	for (DefryFactor factor : DefryFactor.values()) {
    		assertEquals(factor, gson.fromJson(gson.toJson(factor), DefryFactor.class));
    	}
    }
}
