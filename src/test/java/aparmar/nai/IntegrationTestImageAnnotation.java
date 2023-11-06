package aparmar.nai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import aparmar.nai.data.request.Base64Image;
import aparmar.nai.data.request.ImageAnnotateRequest;
import aparmar.nai.data.request.ImageAnnotateRequest.AnnotationModel;
import aparmar.nai.data.response.ImageSetWrapper;
import aparmar.nai.utils.InternalResourceLoader;

class IntegrationTestImageAnnotation extends AbstractFeatureIntegrationTest {

	@ParameterizedTest
	@EnumSource(value = AnnotationModel.class)
	void testImageAnnotation(AnnotationModel testModel) throws AssertionError, Exception {
		TestHelpers.runTestToleratingTimeouts(3, 1000, ()->{
			BufferedImage targetImage = ImageIO.read(InternalResourceLoader.getInternalFile("sample_annotate_target_2.jpg"));
			ImageAnnotateRequest request = ImageAnnotateRequest.defaultRequestForModel(testModel, new Base64Image(targetImage));
			
			ImageSetWrapper result = apiInstance.annotateImage(request);
			
			assertNotNull(result);
			assertEquals(testModel.getReturnImgCount(), result.getImageCount());
			for (int i=0;i<result.getImageCount();i++) {
				IIOImage resultImage = result.getImage(i);
				assertNotNull(resultImage);
				// TODO: change this once we know why/how the models change the image size
		//		assertEquals(targetImage.getHeight(), resultImage.getRenderedImage().getHeight());
		//		assertEquals(targetImage.getWidth(), resultImage.getRenderedImage().getWidth());
				
				result.writeImageToFile(i, new File(TestConstants.TEST_IMAGE_FOLDER+"annotation_"+testModel+"_test-"+i+".png"));
			}
		});
	}

}
