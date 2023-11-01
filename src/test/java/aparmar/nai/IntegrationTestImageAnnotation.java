package test.java.aparmar.nai;

import static org.junit.Assume.assumeNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import main.java.aparmar.nai.NAIAPI;
import main.java.aparmar.nai.data.request.Base64Image;
import main.java.aparmar.nai.data.request.ImageAnnotateRequest;
import main.java.aparmar.nai.data.request.ImageAnnotateRequest.AnnotationModel;
import main.java.aparmar.nai.data.response.ImageSetWrapper;
import main.java.aparmar.nai.utils.InternalResourceLoader;

class IntegrationTestImageAnnotation {
	private NAIAPI apiInstance;

	@BeforeEach
	void setUp() throws Exception {
		assumeNotNull(TestConstants.getTestAPIKey());
		apiInstance = new NAIAPI(TestConstants.getTestAPIKey());
	}

	@ParameterizedTest
	@EnumSource(value = AnnotationModel.class)
	void testImageAnnotation(AnnotationModel testModel) throws FileNotFoundException, IOException {
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
	}

}
