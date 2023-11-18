package aparmar.nai.data.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import aparmar.nai.TestHelpers;
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
import aparmar.nai.data.response.UserSubscription;
import aparmar.nai.data.response.UserSubscription.ImageGenerationLimit;
import aparmar.nai.data.response.UserSubscription.SubscriptionPerks;
import aparmar.nai.data.response.UserSubscription.SubscriptionTier;
import aparmar.nai.utils.InternalResourceLoader;
import lombok.Data;

class UnitTestImageGenerationRequest {

	@Test
	void testImageGenerationRequestDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ImageGenerationRequest testInstance1 = ImageGenerationRequest.builder()
				.model(ImageGenModel.ANIME_CURATED)
				.action(ImageGenAction.GENERATE)
				.build();
		ImageGenerationRequest testInstance2 = ImageGenerationRequest.builder()
				.input("input")
				.model(ImageGenModel.FURRY)
				.action(ImageGenAction.IMG2IMG)
				.parameters(ImageParameters.builder().build())
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
	void testImage2ImageParametersDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Image2ImageParameters testInstance1 = Image2ImageParameters.builder()
				.sampler(ImageGenSampler.DDIM)
				.build();
		Image2ImageParameters testInstance2 = Image2ImageParameters.builder()
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
				
				.extraNoiseSeed(11111)
				.strength(0.66)
				.noise(0.77)
				.image(new Base64Image())
				.build();
		TestHelpers.autoTestDataAndToBuilderAnnotation(Image2ImageParameters.class, testInstance1, testInstance2);
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
	void testImageControlNetParametersDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ImageControlNetParameters testInstance1 = ImageControlNetParameters.builder()
				.sampler(ImageGenSampler.DDIM)
				.build();
		ImageControlNetParameters testInstance2 = ImageControlNetParameters.builder()
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
				
				.model(ControlnetModel.FORM_LOCK)
				.conditionImg(new Base64Image())
				.build();
		TestHelpers.autoTestDataAndToBuilderAnnotation(ImageControlNetParameters.class, testInstance1, testInstance2);
	}
	
	@Test
	void testImageGenerationRequestSerialization() {
		ImageGenerationRequest serializationInstance = new ImageGenerationRequest();
		JsonSerializationContext mockJsonSerializationContext = mock(JsonSerializationContext.class);
		when(mockJsonSerializationContext.serialize(any(), any()))
			.then(a->new JsonPrimitive(a.getArgument(0).toString()));
		
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
		assertEquals("ANIME_V2", result.get("model").getAsString());
		assertEquals("GENERATE", result.get("action").getAsString());
		assertEquals(testInstance.getParameters().toString(), result.get("parameters").getAsString());

		testInstance.getParameters().setQualityToggle(true);
		// DEFAULT quality tags insertion location
		result = serializationInstance.serialize(testInstance, ImageParameters.class, mockJsonSerializationContext)
				.getAsJsonObject();
		assertEquals("input, very aesthetic, best quality, absurdres", result.get("input").getAsString());
		assertEquals("ANIME_V2", result.get("model").getAsString());
		assertEquals("GENERATE", result.get("action").getAsString());
		assertEquals(testInstance.getParameters().toString(), result.get("parameters").getAsString());

		// PREPEND quality tags insertion location
		testInstance.getParameters().setQualityInsertLocation(QualityTagsLocation.PREPEND);
		result = serializationInstance.serialize(testInstance, ImageParameters.class, mockJsonSerializationContext)
				.getAsJsonObject();
		assertEquals("very aesthetic, best quality, absurdres, input", result.get("input").getAsString());
		assertEquals("ANIME_V2", result.get("model").getAsString());
		assertEquals("GENERATE", result.get("action").getAsString());
		assertEquals(testInstance.getParameters().toString(), result.get("parameters").getAsString());
		
		// APPEND quality tags insertion location
		testInstance.getParameters().setQualityInsertLocation(QualityTagsLocation.APPEND);
		result = serializationInstance.serialize(testInstance, ImageParameters.class, mockJsonSerializationContext)
				.getAsJsonObject();
		assertEquals("input, very aesthetic, best quality, absurdres", result.get("input").getAsString());
		assertEquals("ANIME_V2", result.get("model").getAsString());
		assertEquals("GENERATE", result.get("action").getAsString());
		assertEquals(testInstance.getParameters().toString(), result.get("parameters").getAsString());
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
		ImageParametersBuilder<?,?> builder;
		if (img2ImgStrength == 0) {
			builder = ImageParameters.builder();
		} else {
			builder = Image2ImageParameters.builder()
					.strength(img2ImgStrength);
		}
		
		int estimatedCost = model.estimateAnlasCost(builder
				.width(width)
				.height(height)
				.steps(steps)
				.ucScale(ucScale)
				.imgCount(images)
				.smeaEnabled(smeaEnabled)
				.dynSmeaEnabled(dynSmeaEnabled)
				.sampler(sampler)
				.build());
		assertEquals(expectedCost*images, estimatedCost);
	}

}
