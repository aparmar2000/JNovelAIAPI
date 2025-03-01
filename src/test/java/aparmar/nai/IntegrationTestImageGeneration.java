package aparmar.nai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import aparmar.nai.data.request.Base64Image;
import aparmar.nai.data.request.imagen.Image2ImageParameters;
import aparmar.nai.data.request.imagen.ImageControlNetParameters;
import aparmar.nai.data.request.imagen.ImageControlNetParameters.ControlnetModel;
import aparmar.nai.data.request.imagen.ImageGenerationRequest;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenAction;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import aparmar.nai.data.request.imagen.ImageInpaintParameters;
import aparmar.nai.data.request.imagen.ImageParameters;
import aparmar.nai.data.request.imagen.ImageVibeTransferParameters;
import aparmar.nai.data.request.imagen.MultiCharacterParameters.CharacterPrompt;
import aparmar.nai.data.request.imagen.V4MultiCharacterParameters;
import aparmar.nai.data.response.ImageSetWrapper;
import aparmar.nai.utils.InternalResourceLoader;

class IntegrationTestImageGeneration extends AbstractFeatureIntegrationTest {
	
	static Stream<ImageGenModel> getNonDepreciatedModels() {
		return Arrays.stream(ImageGenModel.values()).filter(m -> {
			try {
				return !ImageGenModel.class.getField(m.name()).isAnnotationPresent(Deprecated.class);
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
				return false;
			}
		});
	}
	static Stream<ImageGenModel> getNonInpaintingModels() {
		return getNonDepreciatedModels().filter(m->!m.isInpaintingModel());
	}
	static Stream<ImageGenModel> getInpaintingModels() {
		return getNonDepreciatedModels().filter(m->m.isInpaintingModel());
	}
	static Stream<ImageGenModel> getImg2ImgModels() {
		return getNonInpaintingModels().filter(m->m.doesModelSupportExtraParameterType(Image2ImageParameters.class));
	}
	static Stream<ImageGenModel> getControlNetModels() {
		return getNonInpaintingModels().filter(m->m.doesModelSupportExtraParameterType(ImageControlNetParameters.class));
	}
	static Stream<ImageGenModel> getVibeTransferModels() {
		return getNonInpaintingModels().filter(m->m.doesModelSupportExtraParameterType(ImageVibeTransferParameters.class));
	}
	static Stream<ImageGenModel> getVibeTransferInpaintingModels() {
		return getInpaintingModels().filter(m->m.doesModelSupportExtraParameterType(ImageVibeTransferParameters.class));
	}
	static Stream<ImageGenModel> getMultiCharacterModels() {
		return getNonInpaintingModels().filter(m->m.doesModelSupportExtraParameterType(V4MultiCharacterParameters.class));
	}

	@ParameterizedTest
	@MethodSource("getNonInpaintingModels")
	void testBasicImageGeneration(ImageGenModel imageGenModel) throws AssertionError, Exception {
		TestHelpers.runTestToleratingTimeouts(3, 1000, ()->{
			ImageGenerationRequest testGenerationRequest = ImageGenerationRequest.builder()
					.input("portrait of a woman")
					.action(ImageGenAction.GENERATE)
					.model(imageGenModel)
					.parameters(new ImageParameters(
							1,
							512,512,
							28,1,0,
							ImageParameters.ImageGenSampler.DPM_PLUS_PLUS_2S_ANCESTRAL,
							false, false, false, 
							ImageParameters.SamplingSchedule.NATIVE, 
							false, ImageGenerationRequest.QualityTagsLocation.DEFAULT, 
							1, ImageGenerationRequest.ANIME_V3_LIGHT_UC, 1,
							1))
					.build();
			ImageSetWrapper result = apiInstance.generateImage(testGenerationRequest);
			
			assertNotNull(result);
			assertEquals(1, result.getImageCount());
			IIOImage resultImage = result.getImage(0);
			assertNotNull(resultImage);
			assertEquals(512, resultImage.getRenderedImage().getHeight());
			assertEquals(512, resultImage.getRenderedImage().getWidth());
			
			if (!"True".equals(System.getenv("saveTestImages"))) { return; }
			result.writeImageToFile(0, new File(TestConstants.TEST_IMAGE_FOLDER+imageGenModel+"_basic_generation_test.png"));
		});
	}

	@EnabledIfEnvironmentVariable(named = "allowNonFreeTests", matches = "True")
	@ParameterizedTest
	@MethodSource("getInpaintingModels")
	void testImageInpainting(ImageGenModel imageGenModel) throws AssertionError, Exception {
		TestHelpers.runTestToleratingTimeouts(3, 1000, ()->{
			BufferedImage baseImage = ImageIO.read(InternalResourceLoader.getInternalResourceAsStream("sample_base_image.jpg"));
			BufferedImage maskImage = ImageIO.read(InternalResourceLoader.getInternalResourceAsStream("sample_mask.png"));
			
			ImageGenerationRequest testGenerationRequest = ImageGenerationRequest.builder()
					.input("portrait of a woman")
					.action(ImageGenAction.INFILL)
					.model(imageGenModel)
					.parameters(ImageInpaintParameters.builder()
							.seed(1)
							.width(512)
							.height(512)
							.steps(28)
							.scale(1)
							.sampler(ImageParameters.ImageGenSampler.DPM_PLUS_PLUS_2S_ANCESTRAL)
							.ucPreset(1)
							.undesiredContent(ImageGenerationRequest.ANIME_V3_LIGHT_UC)
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

			if (!"True".equals(System.getenv("saveTestImages"))) { return; }
			result.writeImageToFile(0, new File(TestConstants.TEST_IMAGE_FOLDER+imageGenModel+"_inpaint_test.png"));
		});
	}

	@EnabledIfEnvironmentVariable(named = "allowNonFreeTests", matches = "True")
	@ParameterizedTest
	@MethodSource("getImg2ImgModels")
	void testImage2ImageGeneration(ImageGenModel imageGenModel) throws AssertionError, Exception {
		TestHelpers.runTestToleratingTimeouts(3, 1000, ()->{
			BufferedImage baseImage = ImageIO.read(InternalResourceLoader.getInternalResourceAsStream("sample_base_image.jpg"));
			
			ImageGenerationRequest testGenerationRequest = ImageGenerationRequest.builder()
					.input("portrait of a woman")
					.action(ImageGenAction.IMG2IMG)
					.model(imageGenModel)
					.parameters(ImageParameters.builder()
							.seed(1)
							.width(512)
							.height(512)
							.steps(28)
							.scale(1)
							.sampler(ImageParameters.ImageGenSampler.DPM_PLUS_PLUS_2S_ANCESTRAL)
							.ucPreset(1)
							.undesiredContent(ImageGenerationRequest.ANIME_V3_LIGHT_UC)
							.build())
					.extraParameter(Image2ImageParameters.builder()
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

			if (!"True".equals(System.getenv("saveTestImages"))) { return; }
			result.writeImageToFile(0, new File(TestConstants.TEST_IMAGE_FOLDER+imageGenModel+"_img2img_test.png"));
		});
	}

	@EnabledIfEnvironmentVariable(named = "allowNonFreeTests", matches = "True")
	@ParameterizedTest
	@MethodSource("getControlNetModels")
	void testControlledImageGeneration(ImageGenModel imageGenModel) throws AssertionError, Exception {
		TestHelpers.runTestToleratingTimeouts(3, 1000, ()->{
			BufferedImage comditionImage = ImageIO.read(InternalResourceLoader.getInternalResourceAsStream("sample_scribbles.png"));
			
			ImageGenerationRequest testGenerationRequest = ImageGenerationRequest.builder()
					.input("portrait of a woman")
					.action(ImageGenAction.GENERATE)
					.model(imageGenModel)
					.parameters(ImageParameters.builder()
							.seed(1)
							.width(512)
							.height(512)
							.steps(28)
							.scale(1)
							.sampler(ImageParameters.ImageGenSampler.DPM_PLUS_PLUS_2S_ANCESTRAL)
							.ucPreset(1)
							.undesiredContent(ImageGenerationRequest.ANIME_V3_LIGHT_UC)
							.build())
					.extraParameter(ImageControlNetParameters.builder()
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

			if (!"True".equals(System.getenv("saveTestImages"))) { return; }
			result.writeImageToFile(0, new File(TestConstants.TEST_IMAGE_FOLDER+imageGenModel+"_controlnet_conditioned_test.png"));
		});
	}

	@EnabledIfEnvironmentVariable(named = "allowNonFreeTests", matches = "True")
	@ParameterizedTest
	@MethodSource("getVibeTransferModels")
	void testVibeTransferImageGeneration(ImageGenModel imageGenModel) throws AssertionError, Exception {
		TestHelpers.runTestToleratingTimeouts(3, 1000, ()->{
			BufferedImage conditionImage = ImageIO.read(InternalResourceLoader.getInternalResourceAsStream("sample_base_image.jpg"));
			
			ImageGenerationRequest testGenerationRequest = ImageGenerationRequest.builder()
					.input("portrait of a woman")
					.action(ImageGenAction.GENERATE)
					.model(imageGenModel)
					.parameters(ImageParameters.builder()
							.seed(1)
							.width(512)
							.height(512)
							.steps(28)
							.scale(1)
							.sampler(ImageParameters.ImageGenSampler.DPM_PLUS_PLUS_2S_ANCESTRAL)
							.ucPreset(1)
							.undesiredContent(ImageGenerationRequest.ANIME_V3_LIGHT_UC)
							.build())
					.extraParameter(ImageVibeTransferParameters.builder()
							.vibeImage(ImageVibeTransferParameters.VibeTransferImage.builder()
									.referenceImage(new Base64Image(conditionImage, 512, 512, true))
									.build())
							.build())
					.build();
			ImageSetWrapper result = apiInstance.generateImage(testGenerationRequest);
			
			assertNotNull(result);
			assertEquals(1, result.getImageCount());
			IIOImage resultImage = result.getImage(0);
			assertNotNull(resultImage);
			assertEquals(512, resultImage.getRenderedImage().getHeight());
			assertEquals(512, resultImage.getRenderedImage().getWidth());

			if (!"True".equals(System.getenv("saveTestImages"))) { return; }
			result.writeImageToFile(0, new File(TestConstants.TEST_IMAGE_FOLDER+imageGenModel+"_vibe_transfer_test.png"));
		});
	}

	@EnabledIfEnvironmentVariable(named = "allowNonFreeTests", matches = "True")
	@ParameterizedTest
	@MethodSource("getVibeTransferInpaintingModels")
	void testVibeTransferInpaintingImageGeneration(ImageGenModel imageGenModel) throws AssertionError, Exception {
		TestHelpers.runTestToleratingTimeouts(3, 1000, ()->{
			BufferedImage baseImage = ImageIO.read(InternalResourceLoader.getInternalResourceAsStream("sample_base_image.jpg"));
			BufferedImage maskImage = ImageIO.read(InternalResourceLoader.getInternalResourceAsStream("sample_mask.png"));
			BufferedImage conditionImage = ImageIO.read(InternalResourceLoader.getInternalResourceAsStream("sample_base_image.jpg"));
			
			ImageGenerationRequest testGenerationRequest = ImageGenerationRequest.builder()
					.input("portrait of a woman")
					.action(ImageGenAction.INFILL)
					.model(imageGenModel)
					.parameters(ImageInpaintParameters.builder()
							.seed(1)
							.width(512)
							.height(512)
							.steps(28)
							.scale(1)
							.sampler(ImageParameters.ImageGenSampler.DPM_PLUS_PLUS_2S_ANCESTRAL)
							.ucPreset(1)
							.undesiredContent(ImageGenerationRequest.ANIME_V3_LIGHT_UC)
							.image(new Base64Image(baseImage, 512, 512, false))
							.mask(new Base64Image(maskImage, 512, 512, true))
							.build())
					.extraParameter(ImageVibeTransferParameters.builder()
							.vibeImage(ImageVibeTransferParameters.VibeTransferImage.builder()
									.referenceImage(new Base64Image(conditionImage, 512, 512, true))
									.build())
							.build())
					.build();
			ImageSetWrapper result = apiInstance.generateImage(testGenerationRequest);
			
			assertNotNull(result);
			assertEquals(1, result.getImageCount());
			IIOImage resultImage = result.getImage(0);
			assertNotNull(resultImage);
			assertEquals(512, resultImage.getRenderedImage().getHeight());
			assertEquals(512, resultImage.getRenderedImage().getWidth());

			if (!"True".equals(System.getenv("saveTestImages"))) { return; }
			result.writeImageToFile(0, new File(TestConstants.TEST_IMAGE_FOLDER+imageGenModel+"_vibe_transfer_inpaint_test.png"));
		});
	}

	@ParameterizedTest
	@MethodSource("getMultiCharacterModels")
	void testV4SingleCharacterImageGeneration(ImageGenModel imageGenModel) throws AssertionError, Exception {
		TestHelpers.runTestToleratingTimeouts(3, 1000, ()->{
			ImageGenerationRequest testGenerationRequest = ImageGenerationRequest.builder()
					.input("portrait of a woman")
					.action(ImageGenAction.GENERATE)
					.model(imageGenModel)
					.parameters(new ImageParameters(
							1,
							512,512,
							28,1,0,
							ImageParameters.ImageGenSampler.DPM_PLUS_PLUS_2S_ANCESTRAL,
							false, false, false, 
							ImageParameters.SamplingSchedule.NATIVE, 
							true, ImageGenerationRequest.QualityTagsLocation.DEFAULT, 
							1, ImageGenerationRequest.ANIME_V4_LIGHT_UC, 1,
							1))
					.build();
			ImageSetWrapper result = apiInstance.generateImage(testGenerationRequest);
			
			assertNotNull(result);
			assertEquals(1, result.getImageCount());
			IIOImage resultImage = result.getImage(0);
			assertNotNull(resultImage);
			assertEquals(512, resultImage.getRenderedImage().getHeight());
			assertEquals(512, resultImage.getRenderedImage().getWidth());

			if (!"True".equals(System.getenv("saveTestImages"))) { return; }
			result.writeImageToFile(0, new File(TestConstants.TEST_IMAGE_FOLDER+imageGenModel+"_v4_single_character_test.png"));
		});
	}

	@ParameterizedTest
	@MethodSource("getMultiCharacterModels")
	void testV4MultiCharacterImageGeneration(ImageGenModel imageGenModel) throws AssertionError, Exception {
		TestHelpers.runTestToleratingTimeouts(3, 1000, ()->{
			ImageGenerationRequest testGenerationRequest = ImageGenerationRequest.builder()
					.input("2girls. Painting of two women.")
					.action(ImageGenAction.GENERATE)
					.model(imageGenModel)
					.parameters(new ImageParameters(
							1,
							512,512,
							28,1,0,
							ImageParameters.ImageGenSampler.DPM_PLUS_PLUS_2S_ANCESTRAL,
							false, false, false, 
							ImageParameters.SamplingSchedule.NATIVE, 
							true, ImageGenerationRequest.QualityTagsLocation.DEFAULT, 
							1, ImageGenerationRequest.ANIME_V4_LIGHT_UC, 1,
							1))
					.extraParameter(V4MultiCharacterParameters.builder()
							.characterPrompt(CharacterPrompt.builder()
									.prompt("A tall woman with dark hair.")
									.build())
							.characterPrompt(CharacterPrompt.builder()
									.prompt("A short woman with blonde hair.")
									.build())
							.build())
					.build();
			ImageSetWrapper result = apiInstance.generateImage(testGenerationRequest);
			
			assertNotNull(result);
			assertEquals(1, result.getImageCount());
			IIOImage resultImage = result.getImage(0);
			assertNotNull(resultImage);
			assertEquals(512, resultImage.getRenderedImage().getHeight());
			assertEquals(512, resultImage.getRenderedImage().getWidth());

			if (!"True".equals(System.getenv("saveTestImages"))) { return; }
			result.writeImageToFile(0, new File(TestConstants.TEST_IMAGE_FOLDER+imageGenModel+"_v4_multi_character_test.png"));
		});
	}

}
