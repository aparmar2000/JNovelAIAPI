package test.java.aparmar.nai;

import static org.junit.Assume.assumeNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import main.java.aparmar.nai.NAIAPI;
import main.java.aparmar.nai.data.request.Base64Image;
import main.java.aparmar.nai.data.request.imagen.Image2ImageParameters;
import main.java.aparmar.nai.data.request.imagen.ImageControlNetParameters;
import main.java.aparmar.nai.data.request.imagen.ImageControlNetParameters.ControlnetModel;
import main.java.aparmar.nai.data.request.imagen.ImageGenerationRequest;
import main.java.aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenAction;
import main.java.aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import main.java.aparmar.nai.data.request.imagen.ImageInpaintParameters;
import main.java.aparmar.nai.data.request.imagen.ImageParameters;
import main.java.aparmar.nai.data.response.ImageSetWrapper;
import main.java.aparmar.nai.utils.InternalResourceLoader;

class IntegrationTestImageGeneration {
	private NAIAPI apiInstance;

	@BeforeEach
	void setUp() throws Exception {
		assumeNotNull(TestConstants.getTestAPIKey());
		apiInstance = new NAIAPI(TestConstants.getTestAPIKey());
	}

	@Test
	void testBasicImageGeneration() throws IOException {
		ImageGenerationRequest testGenerationRequest = ImageGenerationRequest.builder()
				.input("portrait of a woman")
				.action(ImageGenAction.GENERATE)
				.model(ImageGenModel.ANIME_V2)
				.parameters(new ImageParameters(
						1,
						512,512,
						28,1,
						ImageParameters.ImageGenSampler.DPM_PLUS_PLUS_2S_ANCESTRAL,
						false, false, false, 
						false, 1, ImageGenerationRequest.ANIME_V2_LIGHT_UC, 1,
						1))
				.build();
		ImageSetWrapper result = apiInstance.generateImage(testGenerationRequest);
		
		assertNotNull(result);
		assertEquals(1, result.getImageCount());
		IIOImage resultImage = result.getImage(0);
		assertNotNull(resultImage);
		assertEquals(512, resultImage.getRenderedImage().getHeight());
		assertEquals(512, resultImage.getRenderedImage().getWidth());
		
		result.writeImageToFile(0, new File(TestConstants.TEST_IMAGE_FOLDER+"basic_generation_test.png"));
	}

	@EnabledIfEnvironmentVariable(named = "allowNonFreeTests", matches = "True")
	@Test
	void testImage2ImageGeneration() throws IOException {
		BufferedImage baseImage = ImageIO.read(InternalResourceLoader.getInternalFile("sample_base_image.jpg"));
		
		ImageGenerationRequest testGenerationRequest = ImageGenerationRequest.builder()
				.input("portrait of a woman")
				.action(ImageGenAction.IMG2IMG)
				.model(ImageGenModel.ANIME_V2)
				.parameters(Image2ImageParameters.builder()
						.seed(1)
						.width(512)
						.height(512)
						.steps(28)
						.scale(1)
						.sampler(ImageParameters.ImageGenSampler.DPM_PLUS_PLUS_2S_ANCESTRAL)
						.ucPreset(1)
						.undesiredContent(ImageGenerationRequest.ANIME_V2_LIGHT_UC)
						.extraNoiseSeed(2)
						.strength(0.7)
						.noise(0.0)
						.image(new Base64Image(baseImage, 512, 512, false))
						.build())
				.build();
		ImageSetWrapper result = apiInstance.generateImage(testGenerationRequest);
		
		assertNotNull(result);
		assertEquals(1, result.getImageCount());
		IIOImage resultImage = result.getImage(0);
		assertNotNull(resultImage);
		assertEquals(512, resultImage.getRenderedImage().getHeight());
		assertEquals(512, resultImage.getRenderedImage().getWidth());
		
		result.writeImageToFile(0, new File(TestConstants.TEST_IMAGE_FOLDER+"img2img_test.png"));
	}

	@EnabledIfEnvironmentVariable(named = "allowNonFreeTests", matches = "True")
	@Test
	void testImageInpainting() throws IOException {
		BufferedImage baseImage = ImageIO.read(InternalResourceLoader.getInternalFile("sample_base_image.jpg"));
		BufferedImage maskImage = ImageIO.read(InternalResourceLoader.getInternalFile("sample_mask.png"));
		
		ImageGenerationRequest testGenerationRequest = ImageGenerationRequest.builder()
				.input("portrait of a woman")
				.action(ImageGenAction.INFILL)
				.model(ImageGenModel.ANIME_FULL_INPAINT)
				.parameters(ImageInpaintParameters.builder()
						.seed(1)
						.width(512)
						.height(512)
						.steps(28)
						.scale(1)
						.sampler(ImageParameters.ImageGenSampler.DPM_PLUS_PLUS_2S_ANCESTRAL)
						.ucPreset(1)
						.undesiredContent(ImageGenerationRequest.ANIME_V2_LIGHT_UC)
						.image(new Base64Image(baseImage, 512, 512, false))
						.mask(new Base64Image(maskImage, 512, 512, true))
						.build())
				.build();
		ImageSetWrapper result = apiInstance.generateImage(testGenerationRequest);
		
		assertNotNull(result);
		assertEquals(1, result.getImageCount());
		IIOImage resultImage = result.getImage(0);
		assertNotNull(resultImage);
		assertEquals(512, resultImage.getRenderedImage().getHeight());
		assertEquals(512, resultImage.getRenderedImage().getWidth());
		
		result.writeImageToFile(0, new File(TestConstants.TEST_IMAGE_FOLDER+"inpaint_test.png"));
	}

	@EnabledIfEnvironmentVariable(named = "allowNonFreeTests", matches = "True")
	@Test
	void testControlledImageGeneration() throws IOException {
		BufferedImage comditionImage = ImageIO.read(InternalResourceLoader.getInternalFile("sample_scribbles.png"));
		
		ImageGenerationRequest testGenerationRequest = ImageGenerationRequest.builder()
				.input("portrait of a woman")
				.action(ImageGenAction.GENERATE)
				.model(ImageGenModel.ANIME_V2)
				.parameters(ImageControlNetParameters.builder()
						.seed(1)
						.width(512)
						.height(512)
						.steps(28)
						.scale(1)
						.sampler(ImageParameters.ImageGenSampler.DPM_PLUS_PLUS_2S_ANCESTRAL)
						.ucPreset(1)
						.undesiredContent(ImageGenerationRequest.ANIME_V2_LIGHT_UC)
						.model(ControlnetModel.SCRIBBLER)
						.conditionImg(new Base64Image(comditionImage, 512, 512, true))
						.build())
				.build();
		ImageSetWrapper result = apiInstance.generateImage(testGenerationRequest);
		
		assertNotNull(result);
		assertEquals(1, result.getImageCount());
		IIOImage resultImage = result.getImage(0);
		assertNotNull(resultImage);
		assertEquals(512, resultImage.getRenderedImage().getHeight());
		assertEquals(512, resultImage.getRenderedImage().getWidth());
		
		result.writeImageToFile(0, new File(TestConstants.TEST_IMAGE_FOLDER+"controlnet_conditioned_test.png"));
	}

}
