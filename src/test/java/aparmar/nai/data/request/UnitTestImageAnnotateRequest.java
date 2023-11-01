package aparmar.nai.data.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import aparmar.nai.TestHelpers;
import aparmar.nai.data.request.ImageAnnotateRequest.AnnotationModel;

class UnitTestImageAnnotateRequest {

	@Test
	void testImageAnnotateRequestDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ImageAnnotateRequest testInstance1 = new ImageAnnotateRequest();
		ImageAnnotateRequest testInstance2 = ImageAnnotateRequest.defaultRequestForModel(
				AnnotationModel.FORM_LOCK_ANNOTATOR, 
				new Base64Image());
		
		TestHelpers.autoTestDataAndToBuilderAnnotation(ImageAnnotateRequest.class, testInstance1, testInstance2);
	}
	
	@Test
	void testStaticFactoryMethods() {
		ImageAnnotateRequest testRequest = ImageAnnotateRequest
				.defaultRequestForModel(AnnotationModel.POSE_ANNOTATOR, new Base64Image());
		assertEquals(AnnotationModel.POSE_ANNOTATOR, testRequest.getModel());
		assertEquals(0, testRequest.getExtraParameters().size());
		
		testRequest = ImageAnnotateRequest.paletteSwapRequest(new Base64Image(), 100, 1000);
		assertEquals(AnnotationModel.PALETTE_SWAP_ANNOTATOR, testRequest.getModel());
		assertEquals(2, testRequest.getExtraParameters().size());
		assertEquals(100, testRequest.getExtraParameters().get("low_threshold"));
		assertEquals(1000, testRequest.getExtraParameters().get("high_threshold"));
		
		testRequest = ImageAnnotateRequest.buildingControlRequest(new Base64Image(), 0.1, 0.5);
		assertEquals(AnnotationModel.BUILDING_CONTROL_ANNOTATOR, testRequest.getModel());
		assertEquals(2, testRequest.getExtraParameters().size());
		assertEquals(0.1, testRequest.getExtraParameters().get("distance_threshold"));
		assertEquals(0.5, testRequest.getExtraParameters().get("value_threshold"));
	}
	
	@Test
	void testCustomSerialization() {
		ImageAnnotateRequest serializationInstance = new ImageAnnotateRequest();
		JsonSerializationContext mockJsonSerializationContext = mock(JsonSerializationContext.class);
		when(mockJsonSerializationContext.serialize(any(), any()))
			.then(a->new JsonPrimitive(a.getArgument(0).toString()));
		
		ImageAnnotateRequest testRequest = ImageAnnotateRequest.paletteSwapRequest(new Base64Image(), 100, 1000);
		JsonObject result = serializationInstance.serialize(testRequest, ImageAnnotateRequest.class, mockJsonSerializationContext)
				.getAsJsonObject();
		assertEquals("PALETTE_SWAP_ANNOTATOR", result.get("model").getAsString());
		JsonObject parameterResult = result.getAsJsonObject("parameters");
		assertNotNull(parameterResult);
		assertEquals(testRequest.getImage().toString(), parameterResult.get("image").getAsString());
		assertEquals(100, parameterResult.get("low_threshold").getAsInt());
		assertEquals(1000, parameterResult.get("high_threshold").getAsInt());
	}

}
