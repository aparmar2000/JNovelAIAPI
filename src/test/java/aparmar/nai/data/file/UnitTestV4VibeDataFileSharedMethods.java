package aparmar.nai.data.file;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import aparmar.nai.TestHelpers;
import aparmar.nai.data.file.V4VibeDataFile.ImportInfo;
import aparmar.nai.data.file.V4VibeWithImageDataFile.EmbeddingData;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;

public abstract class UnitTestV4VibeDataFileSharedMethods<T extends V4VibeDataFile<T>> extends UnitTestDataFileSharedMethods<T> {

	@Override
	String getFileExtension() {
		return "naiv4vibe";
	}


    @Nested
    @DisplayName("@Data annotations work properly")
    class DataAnnotationTests {
    	@Test
    	void testV4VibeDataFileDataEmbeddingDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    		EmbeddingData testInstance1 = new EmbeddingData(new byte[] {1}, 1);
    		EmbeddingData testInstance2 = new EmbeddingData(new byte[] {2, 5}, 0.6f);
    		TestHelpers.autoTestDataAndToBuilderAnnotation(EmbeddingData.class, testInstance1, testInstance2);
    	}

    	@Test
    	void testV4VibeDataFileDataImportInfoAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    		ImportInfo testInstance1 = new ImportInfo(ImageGenModel.ANIME_V4_FULL, 1, 0.6f);
    		ImportInfo testInstance2 = new ImportInfo(ImageGenModel.ANIME_V4_CURATED, 0.9f, 0.3f);
    		TestHelpers.autoTestDataAndToBuilderAnnotation(ImportInfo.class, testInstance1, testInstance2);
    	}
    }
}
