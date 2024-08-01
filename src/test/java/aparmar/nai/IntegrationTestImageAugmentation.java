package aparmar.nai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import aparmar.nai.data.request.Base64Image;
import aparmar.nai.data.request.imgaug.ImageAugmentColorizeRequest;
import aparmar.nai.data.request.imgaug.ImageAugmentDeclutterRequest;
import aparmar.nai.data.request.imgaug.ImageAugmentEmotionRequest;
import aparmar.nai.data.request.imgaug.ImageAugmentLineArtRequest;
import aparmar.nai.data.request.imgaug.ImageAugmentRemoveBackgroundRequest;
import aparmar.nai.data.request.imgaug.ImageAugmentRequest;
import aparmar.nai.data.request.imgaug.ImageAugmentRequest.DefryFactor;
import aparmar.nai.data.request.imgaug.ImageAugmentRequestSingleResult;
import aparmar.nai.data.request.imgaug.ImageAugmentSketchRequest;
import aparmar.nai.data.response.ImageSetWrapper;
import aparmar.nai.utils.InternalResourceLoader;

class IntegrationTestImageAugmentation extends AbstractFeatureIntegrationTest {
	
	static <T extends ImageAugmentRequest> Arguments makeRequestConstructorArguments(Function<Base64Image, T> classConstructor, Class<T> classUnderTest, String imgResourceName) {
		return Arguments.of(classConstructor, classUnderTest, imgResourceName);
	}
	static Stream<Arguments> requestConstructorSupplier() {
		LinkedList<Arguments> argumentSets = new LinkedList<>();
		
		argumentSets.add(makeRequestConstructorArguments(
				ImageAugmentRemoveBackgroundRequest::new, 
				ImageAugmentRemoveBackgroundRequest.class, 
				"sample_annotate_target_2.jpg"));
		argumentSets.add(makeRequestConstructorArguments(
				ImageAugmentLineArtRequest::new, 
				ImageAugmentLineArtRequest.class, 
				"sample_annotate_target.jpg"));
		argumentSets.add(makeRequestConstructorArguments(
				ImageAugmentSketchRequest::new, 
				ImageAugmentSketchRequest.class, 
				"sample_annotate_target.jpg"));
		argumentSets.add(makeRequestConstructorArguments(
				ImageAugmentDeclutterRequest::new, 
				ImageAugmentDeclutterRequest.class, 
				"sample_annotate_target_2.jpg"));
		
		argumentSets.add(makeRequestConstructorArguments(
				i->new ImageAugmentEmotionRequest(null, ImageAugmentEmotionRequest.Emotion.ANGRY, null, i), 
				ImageAugmentEmotionRequest.class, 
				"sample_annotate_target_2.jpg"));
		argumentSets.add(makeRequestConstructorArguments(
				i->new ImageAugmentColorizeRequest(DefryFactor.ONE, null, i), 
				ImageAugmentColorizeRequest.class, 
				"sample_scribbles.png"));
		
		return argumentSets.stream();
	}

	@EnabledIfEnvironmentVariable(named = "allowNonFreeTests", matches = "True")
	@ParameterizedTest
	@MethodSource("requestConstructorSupplier")
	<T extends ImageAugmentRequest> void testImageOnlyAugmentationType(Function<Base64Image, T> classConstructor, Class<T> classUnderTest, String imgResourceName) throws AssertionError, Exception {
		TestHelpers.runTestToleratingTimeouts(3, 1000, ()->{
			BufferedImage targetImage = ImageIO.read(InternalResourceLoader.getInternalResourceAsStream(imgResourceName));
			
			T request = classConstructor.apply(new Base64Image(targetImage));
			
			ImageSetWrapper result;
			if (request instanceof ImageAugmentRequestSingleResult) {
				result = apiInstance.augmentImageSingleResult((ImageAugmentRequestSingleResult) request);
			} else if (request instanceof ImageAugmentRemoveBackgroundRequest) {
				result = apiInstance.augmentImageRemoveBackground((ImageAugmentRemoveBackgroundRequest) request);
			} else {
				result = apiInstance.augmentImageGeneric(request);
			}
			
			assertNotNull(result);
			assertEquals(request.getReturnImgCount(), result.getImageCount());
			for (int i=0;i<result.getImageCount();i++) {
				IIOImage resultImage = result.getImage(i);
				assertNotNull(resultImage);
				
				assertEquals(targetImage.getHeight(), resultImage.getRenderedImage().getHeight());
				assertEquals(targetImage.getWidth(), resultImage.getRenderedImage().getWidth());
				
				result.writeImageToFile(i, new File(TestConstants.TEST_IMAGE_FOLDER+"augment_"+classUnderTest.getSimpleName()+"_test-"+i+".png"));
			}
		});
	}

	@EnabledIfEnvironmentVariable(named = "allowNonFreeTests", matches = "True")
	@Test
	void testGenericAugmentationMethod() throws AssertionError, Exception {
		TestHelpers.runTestToleratingTimeouts(3, 1000, ()->{
			BufferedImage targetImage = ImageIO.read(InternalResourceLoader.getInternalResourceAsStream("sample_annotate_target_2.jpg"));
			
			ImageAugmentLineArtRequest request = new ImageAugmentLineArtRequest(new Base64Image(targetImage));
			
			ImageSetWrapper result = apiInstance.augmentImageGeneric(request);
			
			assertNotNull(result);
			assertEquals(request.getReturnImgCount(), result.getImageCount());
			for (int i=0;i<result.getImageCount();i++) {
				IIOImage resultImage = result.getImage(i);
				assertNotNull(resultImage);
				
				assertEquals(targetImage.getHeight(), resultImage.getRenderedImage().getHeight());
				assertEquals(targetImage.getWidth(), resultImage.getRenderedImage().getWidth());
				
				result.writeImageToFile(i, new File(TestConstants.TEST_IMAGE_FOLDER+"augment_generic_"+ImageAugmentLineArtRequest.class.getSimpleName()+"_test-"+i+".png"));
			}
		});
	}

}
