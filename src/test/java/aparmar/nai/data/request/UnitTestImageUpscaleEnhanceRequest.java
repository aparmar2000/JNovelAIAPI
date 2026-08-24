package aparmar.nai.data.request;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import aparmar.nai.TestHelpers;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import aparmar.nai.data.request.imagen.ImageUpscaleEnhanceRequest;
import aparmar.nai.utils.GsonProvider;

class UnitTestImageUpscaleEnhanceRequest {

	@Test
	void testImageUpscaleEnhanceRequestDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ImageUpscaleEnhanceRequest testInstance1 = ImageUpscaleEnhanceRequest.builder()
				.image(new Base64Image())
				.build();
		ImageUpscaleEnhanceRequest testInstance2 = ImageUpscaleEnhanceRequest.builder()
				.image(new Base64Image(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)))
				.declaredBlurSigma(0.2f)
				.model(ImageGenModel.V5_FULL)
				.build();
		TestHelpers.autoTestDataAndToBuilderAnnotation(ImageUpscaleEnhanceRequest.class, testInstance1, testInstance2);
	}
	
	@Test
	void testImageUpscaleEnhanceRequestModelValidation() {
		assertThrows(IllegalArgumentException.class, ()->ImageUpscaleEnhanceRequest.builder().model(ImageGenModel.V4_5_FULL));
		assertThrows(IllegalArgumentException.class, ()->new ImageUpscaleEnhanceRequest(new Base64Image(), 0.0f, ImageGenModel.V4_5_FULL));
		assertDoesNotThrow(()->ImageUpscaleEnhanceRequest.builder().model(ImageGenModel.V5_FULL));
		assertDoesNotThrow(()->new ImageUpscaleEnhanceRequest(new Base64Image(), 0.0f, ImageGenModel.V5_FULL));
	}
	
	@Test
	void testImageUpscaleEnhanceRequestBlurSigmaRounding() {
		Gson gson = GsonProvider.buildGsonInstance();
		
		JsonObject serialized = gson.toJsonTree(new ImageUpscaleEnhanceRequest(new Base64Image(), 0.0f, ImageGenModel.V5_FULL)).getAsJsonObject();
		assertEquals(0.0, serialized.get("declared_blur_sigma").getAsDouble());
		serialized = gson.toJsonTree(new ImageUpscaleEnhanceRequest(new Base64Image(), -1.0f, ImageGenModel.V5_FULL)).getAsJsonObject();
		assertEquals(0.0, serialized.get("declared_blur_sigma").getAsDouble());
		serialized = gson.toJsonTree(new ImageUpscaleEnhanceRequest(new Base64Image(), 0.05f, ImageGenModel.V5_FULL)).getAsJsonObject();
		assertEquals(0.0, serialized.get("declared_blur_sigma").getAsDouble());
		serialized = gson.toJsonTree(new ImageUpscaleEnhanceRequest(new Base64Image(), 0.25f, ImageGenModel.V5_FULL)).getAsJsonObject();
		assertEquals(0.3, serialized.get("declared_blur_sigma").getAsDouble());
		serialized = gson.toJsonTree(new ImageUpscaleEnhanceRequest(new Base64Image(), 0.99f, ImageGenModel.V5_FULL)).getAsJsonObject();
		assertEquals(0.5, serialized.get("declared_blur_sigma").getAsDouble());
		serialized = gson.toJsonTree(new ImageUpscaleEnhanceRequest(new Base64Image(), 0.44f, ImageGenModel.V5_FULL)).getAsJsonObject();
		assertEquals(0.45, serialized.get("declared_blur_sigma").getAsDouble());
	}
	
	@Test
	void testImageUpscaleEnhanceRequestCostEstimation() {
		ImageUpscaleEnhanceRequest testInst = new ImageUpscaleEnhanceRequest(new Base64Image(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)), 0.0f, ImageGenModel.V5_FULL);
		assertEquals(1, testInst.estimateAnlasCost());
		testInst = new ImageUpscaleEnhanceRequest(new Base64Image(new BufferedImage(1536, 2048, BufferedImage.TYPE_INT_RGB)), 0.0f, ImageGenModel.V5_FULL);
		assertEquals(4, testInst.estimateAnlasCost());
		testInst = new ImageUpscaleEnhanceRequest(new Base64Image(new BufferedImage(832, 1216, BufferedImage.TYPE_INT_RGB)), 0.0f, ImageGenModel.V5_FULL);
		assertEquals(1, testInst.estimateAnlasCost());
		testInst = new ImageUpscaleEnhanceRequest(new Base64Image(new BufferedImage(1024, 1536, BufferedImage.TYPE_INT_RGB)), 0.0f, ImageGenModel.V5_FULL);
		assertEquals(2, testInst.estimateAnlasCost());
	}

}
