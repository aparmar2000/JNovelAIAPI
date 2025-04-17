package aparmar.nai.data.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import aparmar.nai.TestHelpers;
import aparmar.nai.data.request.imagen.AbstractExtraImageParameters;
import aparmar.nai.data.request.imagen.Image2ImageParameters;
import aparmar.nai.data.request.imagen.ImageControlNetParameters;
import aparmar.nai.data.request.imagen.ImageControlNetParameters.ControlnetModel;
import aparmar.nai.data.request.imagen.ImageGenerationRequest;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenAction;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.QualityTagsLocation;
import aparmar.nai.data.request.imagen.ImageInpaintParameters;
import aparmar.nai.data.request.imagen.ImageParameters;
import aparmar.nai.data.request.imagen.ImageParameters.ImageGenSampler;
import aparmar.nai.data.request.imagen.ImageParameters.ImageParametersBuilder;
import aparmar.nai.data.request.imagen.ImageParameters.SamplingSchedule;
import aparmar.nai.data.request.imagen.ImageVibeTransferParameters;
import aparmar.nai.data.request.imagen.V4ImageVibeTransferParameters;
import aparmar.nai.data.response.UserSubscription;
import aparmar.nai.data.response.UserSubscription.ImageGenerationLimit;
import aparmar.nai.data.response.UserSubscription.SubscriptionPerks;
import aparmar.nai.data.response.UserSubscription.SubscriptionTier;
import aparmar.nai.utils.HardDeprecationException;
import aparmar.nai.utils.InternalResourceLoader;
import lombok.Data;

class UnitTestImageGenerationRequest {

    @Nested
    @DisplayName("@Data annotations work properly")
    class DataAnnotationTests {
		@Test
		void testImageGenerationRequestDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			ImageGenerationRequest testInstance1 = ImageGenerationRequest.builder()
					.model(ImageGenModel.ANIME_V3)
					.action(ImageGenAction.GENERATE)
					.build();
			ImageGenerationRequest testInstance2 = ImageGenerationRequest.builder()
					.input("input")
					.model(ImageGenModel.FURRY_V3)
					.action(ImageGenAction.IMG2IMG)
					.parameters(ImageParameters.builder().build())
					.extraParameter(Image2ImageParameters.builder().build())
					.build();
			TestHelpers.autoTestDataAndToBuilderAnnotation(ImageGenerationRequest.class, testInstance1, testInstance2);
		}
	
		@Test
		void testImageParametersDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			ImageParameters testInstance1 = ImageParameters.builder()
					.sampler(ImageGenSampler.DDIM)
					.build();
			ImageParameters testInstance2 = ImageParameters.builder()
					.seed(12345)
					.height(32)
					.width(64)
					.steps(25)
					.scale(1.5)
					.scaleRescaleFactor(0.26)
					.sampler(ImageGenSampler.DPM_FAST)
					.noiseSchedule(SamplingSchedule.KARRAS)
					.smeaEnabled(true)
					.dynSmeaEnabled(true)
					.decrisperEnabled(true)
					.qualityToggle(true)
					.qualityInsertLocation(QualityTagsLocation.PREPEND)
					.ucPreset(15)
					.undesiredContent("uc")
					.ucScale(2)
					.imgCount(15)
					.build();
			TestHelpers.autoTestDataAndToBuilderAnnotation(ImageParameters.class, testInstance1, testInstance2);
		}
	
		@Test
		void testImageInpaintParametersDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			ImageInpaintParameters testInstance1 = ImageInpaintParameters.builder()
					.sampler(ImageGenSampler.DDIM)
					.build();
			ImageInpaintParameters testInstance2 = ImageInpaintParameters.builder()
					.seed(12345)
					.height(32)
					.width(64)
					.steps(25)
					.scale(1.5)
					.scaleRescaleFactor(0.26)
					.sampler(ImageGenSampler.DPM_FAST)
					.noiseSchedule(SamplingSchedule.KARRAS)
					.smeaEnabled(true)
					.dynSmeaEnabled(true)
					.decrisperEnabled(true)
					.qualityToggle(true)
					.qualityInsertLocation(QualityTagsLocation.PREPEND)
					.ucPreset(15)
					.undesiredContent("uc")
					.ucScale(2)
					.imgCount(15)
					
					.overlayOriginalImage(true)
					.image(new Base64Image())
					.mask(new Base64Image())
					.build();
			TestHelpers.autoTestDataAndToBuilderAnnotation(ImageInpaintParameters.class, testInstance1, testInstance2);
		}
	
		@Test
		void testImage2ImageParametersDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			Image2ImageParameters testInstance1 = Image2ImageParameters.builder()
					.build();
			Image2ImageParameters testInstance2 = Image2ImageParameters.builder()
					.extraNoiseSeed(11111)
					.strength(0.66)
					.noise(0.77)
					.image(new Base64Image())
					.build();
			TestHelpers.autoTestDataAndToBuilderAnnotation(Image2ImageParameters.class, testInstance1, testInstance2);
		}
	
		@Test
		void testImageControlNetParametersDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			ImageControlNetParameters testInstance1 = ImageControlNetParameters.builder()
					.build();
			ImageControlNetParameters testInstance2 = ImageControlNetParameters.builder()
					.model(ControlnetModel.FORM_LOCK)
					.conditionImg(new Base64Image())
					.conditionStrength(0.2)
					.build();
			TestHelpers.autoTestDataAndToBuilderAnnotation(ImageControlNetParameters.class, testInstance1, testInstance2);
		}
	
		@Test
		void testImageVibeTransferParametersDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			ImageVibeTransferParameters testInstance1 = ImageVibeTransferParameters.builder()
					.build();
			ImageVibeTransferParameters testInstance2 = ImageVibeTransferParameters.builder()
					.vibeImage(ImageVibeTransferParameters.VibeTransferImage.builder()
							.referenceStrength(11)
							.referenceInformationExtracted(15)
							.referenceImage(new Base64Image())
							.build())
					.build();
			TestHelpers.autoTestDataAndToBuilderAnnotation(ImageVibeTransferParameters.class, testInstance1, testInstance2);
		}
	
		@Test
		void testV4ImageVibeTransferParametersDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			V4ImageVibeTransferParameters testInstance1 = V4ImageVibeTransferParameters.builder()
					.build();
			V4ImageVibeTransferParameters testInstance2 = V4ImageVibeTransferParameters.builder()
					.vibeData(V4ImageVibeTransferParameters.VibeTransferData.builder()
							.referenceStrength(11)
							.vibeData(new V4VibeData(1, "a", ImageGenModel.ANIME_V4_FULL, new byte[] {}))
							.build())
					.build();
			TestHelpers.autoTestDataAndToBuilderAnnotation(V4ImageVibeTransferParameters.class, testInstance1, testInstance2);
		}
    }

	@ParameterizedTest
	@MethodSource("aparmar.nai.ImageGenTestHelpers#getHardDeprecatedModels")
	void testRemovedModelRejected(ImageGenModel testModel) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		assertThrows(HardDeprecationException.class, ()->ImageGenerationRequest.builder()
				.model(testModel)
				.action(ImageGenAction.GENERATE)
				.parameters(ImageInpaintParameters.builder().build())
				.build());
	}    

	@ParameterizedTest
	@MethodSource("aparmar.nai.ImageGenTestHelpers#getNonInpaintingModels")
	void testNonInpaintModelRejectsImage2ImageParameters(ImageGenModel testModel) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		assertThrows(IllegalArgumentException.class, ()->ImageGenerationRequest.builder()
				.model(testModel)
				.action(ImageGenAction.GENERATE)
				.parameters(ImageInpaintParameters.builder().build())
				.build());
		assertThrows(IllegalArgumentException.class, ()->ImageGenerationRequest.builder()
				.action(ImageGenAction.GENERATE)
				.parameters(ImageInpaintParameters.builder().build())
				.model(testModel)
				.build());
	}

	@ParameterizedTest
	@MethodSource("aparmar.nai.ImageGenTestHelpers#getInpaintingModels")
	void testInpaintModelRejectsNonImage2ImageParameters(ImageGenModel testModel) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		assertThrows(IllegalArgumentException.class, ()->ImageGenerationRequest.builder()
				.model(testModel)
				.action(ImageGenAction.GENERATE)
				.parameters(ImageParameters.builder().build())
				.build());
		assertThrows(IllegalArgumentException.class, ()->ImageGenerationRequest.builder()
				.action(ImageGenAction.GENERATE)
				.parameters(ImageParameters.builder().build())
				.model(testModel)
				.build());
	}

	
	private static class TestIncompatibleExtraParameters extends AbstractExtraImageParameters {}
    @Nested
    @DisplayName("incompatible extra parameters are blocked")
    class IncompatibleExtraParameters {
    	
    	@ParameterizedTest
    	@MethodSource("aparmar.nai.ImageGenTestHelpers#getNonDeprecatedModels")
		void testModelIncompatibleExtraImageParameters(ImageGenModel testModel) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			assertThrows(IllegalArgumentException.class, ()->ImageGenerationRequest.builder()
					.model(testModel)
					.action(ImageGenAction.GENERATE)
					.extraParameter(new TestIncompatibleExtraParameters())
					.build());
			assertThrows(IllegalArgumentException.class, ()->ImageGenerationRequest.builder()
					.action(ImageGenAction.GENERATE)
					.extraParameter(new TestIncompatibleExtraParameters())
					.model(testModel)
					.build());
		}
		
		@Test
		void testBaseParameterIncompatibleExtraImageParameters() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			ImageParameters mockImageParameters = mock(ImageParameters.class);
			when(mockImageParameters.compatibleWith(any())).thenReturn(false);
			
			assertThrows(IllegalArgumentException.class, ()->ImageGenerationRequest.builder()
					.model(ImageGenModel.ANIME_V3)
					.action(ImageGenAction.GENERATE)
					.parameters(mockImageParameters)
					.extraParameter(ImageVibeTransferParameters.builder().build())
					.build());
			assertThrows(IllegalArgumentException.class, ()->ImageGenerationRequest.builder()
					.model(ImageGenModel.ANIME_V3)
					.action(ImageGenAction.GENERATE)
					.extraParameter(ImageVibeTransferParameters.builder().build())
					.parameters(mockImageParameters)
					.build());
		}
		
		@Test
		void testExtraParameterIncompatibleExtraImageParameters() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			AbstractExtraImageParameters mockExtraParameters = mock(AbstractExtraImageParameters.class);
			when(mockExtraParameters.compatibleWith(any())).thenReturn(false);
			
			assertThrows(IllegalArgumentException.class, ()->ImageGenerationRequest.builder()
					.model(ImageGenModel.ANIME_V3)
					.action(ImageGenAction.GENERATE)
					.extraParameter(ImageVibeTransferParameters.builder().build())
					.extraParameter(mockExtraParameters)
					.build());
			assertThrows(IllegalArgumentException.class, ()->ImageGenerationRequest.builder()
					.model(ImageGenModel.ANIME_V3)
					.action(ImageGenAction.GENERATE)
					.extraParameter(mockExtraParameters)
					.extraParameter(ImageVibeTransferParameters.builder().build())
					.build());
		}
		
		@Test
		void testIncompatibleV4VibeEncodings() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			assertThrows(IllegalArgumentException.class, ()->ImageGenerationRequest.builder()
					.model(ImageGenModel.ANIME_V4_FULL)
					.action(ImageGenAction.GENERATE)
					.extraParameter(V4ImageVibeTransferParameters.builder()
							.vibeData(V4ImageVibeTransferParameters.VibeTransferData.builder()
									.vibeData(new V4VibeData(1, "a", ImageGenModel.ANIME_V4_CURATED, new byte[0]))
									.build())
							.build())
					.build());
			assertThrows(IllegalArgumentException.class, ()->ImageGenerationRequest.builder()
					.action(ImageGenAction.GENERATE)
					.extraParameter(V4ImageVibeTransferParameters.builder()
							.vibeData(V4ImageVibeTransferParameters.VibeTransferData.builder()
									.vibeData(new V4VibeData(1, "a", ImageGenModel.ANIME_V4_FULL, new byte[0]))
									.build())
							.build())
					.model(ImageGenModel.ANIME_V4_CURATED)
					.build());
		}
    }

    @Nested
    @DisplayName("serialization works properly")
    class SerializationTests {
		@Test
		void testImageGenerationRequestBaseSerialization() {
			ImageGenerationRequest serializationInstance = new ImageGenerationRequest();
			JsonSerializationContext mockJsonSerializationContext = mock(JsonSerializationContext.class);
			when(mockJsonSerializationContext.serialize(any(), any()))
				.then(a->{
					JsonObject mockObj = new JsonObject();
					mockObj.addProperty(a.getArgument(0).getClass().getSimpleName(),a.getArgument(0).toString());
					return mockObj;
				});
			
			ImageGenerationRequest testInstance = ImageGenerationRequest.builder()
					.input("input")
					.model(ImageGenModel.ANIME_V2)
					.action(ImageGenAction.GENERATE)
					.parameters(ImageParameters.builder()
							.qualityToggle(false)
							.build())
					.build();
			
			JsonObject result = serializationInstance.serialize(testInstance, ImageParameters.class, mockJsonSerializationContext)
					.getAsJsonObject();
			assertEquals("input", result.get("input").getAsString());
			assertEquals("ANIME_V2", result.get("model").getAsJsonObject().get("ImageGenModel").getAsString());
			assertEquals("GENERATE", result.get("action").getAsJsonObject().get("ImageGenAction").getAsString());
			assertEquals(testInstance.getParameters().toString(), result.get("parameters").getAsJsonObject().get("ImageParameters").getAsString());
		}
		
		@Test
		void testImageGenerationRequestPromptSerialization() {
			ImageGenerationRequest serializationInstance = new ImageGenerationRequest();
			JsonSerializationContext mockJsonSerializationContext = mock(JsonSerializationContext.class);
			when(mockJsonSerializationContext.serialize(any(), any()))
				.then(a->{
					JsonObject mockObj = new JsonObject();
					mockObj.addProperty(a.getArgument(0).getClass().getSimpleName(),a.getArgument(0).toString());
					return mockObj;
				});
			
			ImageGenerationRequest testInstance = ImageGenerationRequest.builder()
					.input("input")
					.model(ImageGenModel.ANIME_V2)
					.action(ImageGenAction.GENERATE)
					.parameters(ImageParameters.builder()
							.qualityToggle(false)
							.build())
					.build();
			
			JsonObject result = serializationInstance.serialize(testInstance, ImageParameters.class, mockJsonSerializationContext)
					.getAsJsonObject();
			assertEquals("input", result.get("input").getAsString());
			assertEquals("ANIME_V2", result.get("model").getAsJsonObject().get("ImageGenModel").getAsString());
			assertEquals("GENERATE", result.get("action").getAsJsonObject().get("ImageGenAction").getAsString());
			assertEquals(testInstance.getParameters().toString(), result.get("parameters").getAsJsonObject().get("ImageParameters").getAsString());
	
			testInstance.getParameters().setQualityToggle(true);
			// DEFAULT quality tags insertion location
			result = serializationInstance.serialize(testInstance, ImageParameters.class, mockJsonSerializationContext)
					.getAsJsonObject();
			assertEquals("input, very aesthetic, best quality, absurdres", result.get("input").getAsString());
			assertEquals("ANIME_V2", result.get("model").getAsJsonObject().get("ImageGenModel").getAsString());
			assertEquals("GENERATE", result.get("action").getAsJsonObject().get("ImageGenAction").getAsString());
			assertEquals(testInstance.getParameters().toString(), result.get("parameters").getAsJsonObject().get("ImageParameters").getAsString());
	
			// PREPEND quality tags insertion location
			testInstance.getParameters().setQualityInsertLocation(QualityTagsLocation.PREPEND);
			result = serializationInstance.serialize(testInstance, ImageParameters.class, mockJsonSerializationContext)
					.getAsJsonObject();
			assertEquals("very aesthetic, best quality, absurdres, input", result.get("input").getAsString());
			assertEquals("ANIME_V2", result.get("model").getAsJsonObject().get("ImageGenModel").getAsString());
			assertEquals("GENERATE", result.get("action").getAsJsonObject().get("ImageGenAction").getAsString());
			assertEquals(testInstance.getParameters().toString(), result.get("parameters").getAsJsonObject().get("ImageParameters").getAsString());
			
			// APPEND quality tags insertion location
			testInstance.getParameters().setQualityInsertLocation(QualityTagsLocation.APPEND);
			result = serializationInstance.serialize(testInstance, ImageParameters.class, mockJsonSerializationContext)
					.getAsJsonObject();
			assertEquals("input, very aesthetic, best quality, absurdres", result.get("input").getAsString());
			assertEquals("ANIME_V2", result.get("model").getAsJsonObject().get("ImageGenModel").getAsString());
			assertEquals("GENERATE", result.get("action").getAsJsonObject().get("ImageGenAction").getAsString());
			assertEquals(testInstance.getParameters().toString(), result.get("parameters").getAsJsonObject().get("ImageParameters").getAsString());
		}
		
		@Test
		void testImageGenerationRequestExtraParameterSerialization() {
			ImageGenerationRequest serializationInstance = new ImageGenerationRequest();
			JsonSerializationContext mockJsonSerializationContext = mock(JsonSerializationContext.class);
			when(mockJsonSerializationContext.serialize(any(), any()))
				.then(a->{
					JsonObject mockObj = new JsonObject();
					mockObj.addProperty(a.getArgument(0).getClass().getSimpleName(),a.getArgument(0).toString());
					return mockObj;
				});
			
			ImageGenerationRequest testInstance = ImageGenerationRequest.builder()
					.input("input")
					.model(ImageGenModel.ANIME_V2)
					.action(ImageGenAction.GENERATE)
					.parameters(ImageParameters.builder()
							.qualityToggle(false)
							.build())
					.extraParameter(Image2ImageParameters.builder()
							.extraNoiseSeed(25)
							.build())
					.build();
			
			JsonObject result = serializationInstance.serialize(testInstance, ImageParameters.class, mockJsonSerializationContext)
					.getAsJsonObject();
			assertEquals(testInstance.getParameters().toString(), result.get("parameters").getAsJsonObject().get("ImageParameters").getAsString());
			assertEquals(testInstance.getExtraParameters().get(Image2ImageParameters.class).toString(), result.get("parameters").getAsJsonObject().get("Image2ImageParameters").getAsString());
		}
    }
	
	@Test
	void testImageGenerationRequestOpusValidation() {
		ImageGenerationRequest testInstance = ImageGenerationRequest.builder()
				.input("input")
				.model(ImageGenModel.ANIME_V2)
				.action(ImageGenAction.GENERATE)
				.parameters(ImageParameters.builder()
						.imgCount(10)
						.width(100)
						.height(100)
						.steps(50)
						.build())
				.build();
		SubscriptionPerks testSubscriptionPerks = new SubscriptionPerks(-1, 10, 9999, true, true, true, false, null, 8192);
		UserSubscription testUserSubscription = new UserSubscription(
				SubscriptionTier.OPUS, 
				true, -1, 
				testSubscriptionPerks, 
				JsonNull.INSTANCE, 
				null,
				1);
		
		assertFalse(testInstance.isFreeGeneration(testUserSubscription));
		ImageGenerationLimit testImageGenerationLimit = new ImageGenerationLimit(1, 1000);
		testSubscriptionPerks = new SubscriptionPerks(-1, 10, 9999, true, true, true, true, new ImageGenerationLimit[] {testImageGenerationLimit}, 8192);
		testUserSubscription = new UserSubscription(
				SubscriptionTier.OPUS, 
				true, -1, 
				testSubscriptionPerks, 
				JsonNull.INSTANCE, 
				null,
				1);

		assertFalse(testInstance.isFreeGeneration(testUserSubscription));
		testInstance.getParameters().setSteps(28);
		
		assertFalse(testInstance.isFreeGeneration(testUserSubscription));
		testInstance.getParameters().setImgCount(1);

		assertFalse(testInstance.isFreeGeneration(testUserSubscription));
		testInstance.getParameters().setWidth(10);

		assertTrue(testInstance.isFreeGeneration(testUserSubscription));
	}
	
	@Data
	private static class ImageGenerationTestSample {
		ImageGenModel model;
		int width, height, steps;
		double ucScale, img2ImgStrength;
		boolean smeaEnabled, dynSmeaEnabled;
		ImageGenSampler sampler;
		int expectedCost;
	}
	private static Stream<Arguments> imageGenerationCostEstimationParameterSource() throws FileNotFoundException, IOException {
		Gson gson = new Gson();
		
		ImageGenerationTestSample[] sampleArray = new ImageGenerationTestSample[0];
		try (InputStreamReader in = new InputStreamReader(InternalResourceLoader.getInternalResourceAsStream("image_gen_test_costs.json"))) {
			sampleArray = gson.fromJson(in, ImageGenerationTestSample[].class);
		}
		
		return Arrays.stream(sampleArray)
				.map(sample->{
					return Arguments.of(
							sample.getModel(), 
							sample.getWidth(), sample.getHeight(), sample.getSteps(), sample.getUcScale(), 1, 
							sample.getImg2ImgStrength(),
							sample.isSmeaEnabled(), sample.isDynSmeaEnabled(),
							sample.getSampler(),
							sample.getExpectedCost());
				});
	} 
	
	@ParameterizedTest
	@MethodSource("imageGenerationCostEstimationParameterSource")
	void testImageGenerationCostEstimation(
			ImageGenModel model,
			int width, int height, int steps, double ucScale, int images,
			double img2ImgStrength,
			boolean smeaEnabled, boolean dynSmeaEnabled, 
			ImageGenSampler sampler,
			int expectedCost) {
		ImageParametersBuilder<?,?> builder = ImageParameters.builder();
		
		ImageParameters baseParameters = builder
				.width(width)
				.height(height)
				.steps(steps)
				.ucScale(ucScale)
				.imgCount(images)
				.smeaEnabled(smeaEnabled)
				.dynSmeaEnabled(dynSmeaEnabled)
				.sampler(sampler)
				.build();

		int estimatedCost;
		if (img2ImgStrength == 0) {
			estimatedCost = model.estimateAnlasCost(builder.build());
		} else {
			estimatedCost = model.estimateAnlasCost(
					baseParameters, 
					Image2ImageParameters.builder()
						.strength(img2ImgStrength)
						.build());
		}
		assertEquals(expectedCost*images, estimatedCost);
	}
	
	@Test
	void testImageGenerationCostEstimationWithOpus() {
		ImageParameters testParameters = ImageParameters.builder()
				.imgCount(10)
				.width(100)
				.height(100)
				.steps(50)
				.build();
		SubscriptionPerks testSubscriptionPerks = new SubscriptionPerks(-1, 10, 9999, true, true, true, false, null, 8192);
		UserSubscription testUserSubscription = new UserSubscription(
				SubscriptionTier.OPUS, 
				true, -1, 
				testSubscriptionPerks, 
				JsonNull.INSTANCE, 
				null,
				1);
		
		assertNotEquals(0, ImageGenModel.ANIME_V2.estimateAnlasCostIncludingSubscription(testParameters, testUserSubscription));
		ImageGenerationLimit testImageGenerationLimit = new ImageGenerationLimit(1, 1000);
		testSubscriptionPerks = new SubscriptionPerks(-1, 10, 9999, true, true, true, true, new ImageGenerationLimit[] {testImageGenerationLimit}, 8192);
		testUserSubscription = new UserSubscription(
				SubscriptionTier.OPUS, 
				true, -1, 
				testSubscriptionPerks, 
				JsonNull.INSTANCE, 
				null,
				1);

		assertNotEquals(0, ImageGenModel.ANIME_V2.estimateAnlasCostIncludingSubscription(testParameters, testUserSubscription));
		testParameters.setSteps(28);
		
		assertNotEquals(0, ImageGenModel.ANIME_V2.estimateAnlasCostIncludingSubscription(testParameters, testUserSubscription));
		testParameters.setImgCount(1);

		assertNotEquals(0, ImageGenModel.ANIME_V2.estimateAnlasCostIncludingSubscription(testParameters, testUserSubscription));
		testParameters.setWidth(10);

		assertEquals(0, ImageGenModel.ANIME_V2.estimateAnlasCostIncludingSubscription(testParameters, testUserSubscription));
	}
	
	@Test
	void testImageGenerationCostEstimationWithOverFourV4Vibes() {
		ImageParameters testParameters = ImageParameters.builder()
				.imgCount(1)
				.width(1024)
				.height(1024)
				.steps(28)
				.build();
		V4VibeData dummyVibeData = new V4VibeData(1, "", ImageGenModel.ANIME_V4_FULL, new byte[0]);
		V4ImageVibeTransferParameters vibeParameters = V4ImageVibeTransferParameters.builder()
				.vibeData(V4ImageVibeTransferParameters.VibeTransferData.builder()
						.vibeData(dummyVibeData)
						.referenceStrength(1)
						.build())
				.vibeData(V4ImageVibeTransferParameters.VibeTransferData.builder()
						.vibeData(dummyVibeData)
						.referenceStrength(1)
						.build())
				.vibeData(V4ImageVibeTransferParameters.VibeTransferData.builder()
						.vibeData(dummyVibeData)
						.referenceStrength(1)
						.build())
				.vibeData(V4ImageVibeTransferParameters.VibeTransferData.builder()
						.vibeData(dummyVibeData)
						.referenceStrength(1)
						.build())
				.vibeData(V4ImageVibeTransferParameters.VibeTransferData.builder()
						.vibeData(dummyVibeData)
						.referenceStrength(1)
						.build())
				.build();
		
		assertEquals(22, ImageGenModel.ANIME_V4_FULL.estimateAnlasCost(testParameters, vibeParameters));
		testParameters = testParameters.toBuilder()
				.imgCount(2)
				.build();
		assertEquals(42, ImageGenModel.ANIME_V4_FULL.estimateAnlasCost(testParameters, vibeParameters));
	}

}
