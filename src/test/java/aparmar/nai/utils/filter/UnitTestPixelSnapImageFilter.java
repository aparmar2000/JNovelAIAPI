package aparmar.nai.utils.filter;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import aparmar.nai.TestConstants;
import aparmar.nai.utils.InternalResourceLoader;

class UnitTestPixelSnapImageFilter {

	@ParameterizedTest
	@ValueSource(strings = { "sample_pixel_snap.png", "sample_pixel_snap_2.png" })
	void testPixelSnap(String filename) throws AssertionError, Exception {
		BufferedImage baseImage = ImageIO.read(InternalResourceLoader.getInternalResourceAsStream(filename));
		
		PixelSnapImageFilter imageFilter = new PixelSnapImageFilter();
		BufferedImage result = imageFilter.applyBuffered(baseImage);
		
		ImageIO.write(result, "png", new File(TestConstants.TEST_IMAGE_FOLDER+"out_"+filename));

		
		PalettizeImageFilter imageFilterQuant = KMeansPalettizeImageFilter.builder()
				.build();
		BufferedImage resultQuant = imageFilterQuant.applyBuffered(result);
		ImageIO.write(resultQuant, "png", new File(TestConstants.TEST_IMAGE_FOLDER+"out_quant_kmeans_"+filename));
		
		imageFilterQuant = SpatialMergePalettizeImageFilter.builder()
				.build();
		resultQuant = imageFilterQuant.applyBuffered(result);
		ImageIO.write(resultQuant, "png", new File(TestConstants.TEST_IMAGE_FOLDER+"out_quant_smerge_"+filename));
	}
}
