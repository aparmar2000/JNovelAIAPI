package test.java.aparmar.nai.data.request;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

import main.java.aparmar.nai.data.request.Base64Image;
import main.java.aparmar.nai.data.request.imagen.Image2ImageParameters;
import main.java.aparmar.nai.data.request.imagen.ImageControlNetParameters;
import main.java.aparmar.nai.data.request.imagen.ImageGenerationRequest;
import main.java.aparmar.nai.data.request.imagen.ImageInpaintParameters;
import main.java.aparmar.nai.data.request.imagen.ImageParameters;
import main.java.aparmar.nai.data.request.imagen.ImageControlNetParameters.ControlnetModel;
import main.java.aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenAction;
import main.java.aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import main.java.aparmar.nai.data.request.imagen.ImageParameters.ImageGenSampler;
import test.java.aparmar.nai.TestHelpers;

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
				.sampler(ImageGenSampler.DPM_FAST)
				.smeaEnabled(true)
				.dynSmeaEnabled(true)
				.decrisperEnabled(true)
				.qualityToggle(true)
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
				.sampler(ImageGenSampler.DPM_FAST)
				.smeaEnabled(true)
				.dynSmeaEnabled(true)
				.decrisperEnabled(true)
				.qualityToggle(true)
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
				.sampler(ImageGenSampler.DPM_FAST)
				.smeaEnabled(true)
				.dynSmeaEnabled(true)
				.decrisperEnabled(true)
				.qualityToggle(true)
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
				.sampler(ImageGenSampler.DPM_FAST)
				.smeaEnabled(true)
				.dynSmeaEnabled(true)
				.decrisperEnabled(true)
				.qualityToggle(true)
				.ucPreset(15)
				.undesiredContent("uc")
				.ucScale(2)
				.imgCount(15)
				
				.model(ControlnetModel.FORM_LOCK)
				.conditionImg(new Base64Image())
				.build();
		TestHelpers.autoTestDataAndToBuilderAnnotation(ImageControlNetParameters.class, testInstance1, testInstance2);
	}

}
