package aparmar.nai.data.request;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

import aparmar.nai.TestHelpers;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;

class UnitTestV4VibeData {

	@Test
	void testV4VibeDataDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		V4VibeData testInstance1 = new V4VibeData(1, "one", ImageGenModel.ANIME_V4_FULL, new byte[] {1});
		V4VibeData testInstance2 = new V4VibeData(0.8f, "two", ImageGenModel.ANIME_V4_CURATED, new byte[] {2});
		
		TestHelpers.autoTestDataAndToBuilderAnnotation(V4VibeData.class, testInstance1, testInstance2);
	}

}
