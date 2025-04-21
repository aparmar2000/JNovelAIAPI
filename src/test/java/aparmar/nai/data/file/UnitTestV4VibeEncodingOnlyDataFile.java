package aparmar.nai.data.file;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import aparmar.nai.data.file.V4VibeDataFile.ImportInfo;
import aparmar.nai.data.request.V4VibeData;
import aparmar.nai.data.request.V4VibeData.VibeEncodingType;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;

class UnitTestV4VibeEncodingOnlyDataFile extends UnitTestV4VibeDataFileSharedMethods<V4VibeEncodingOnlyDataFile> {

	@Override
	Class<V4VibeEncodingOnlyDataFile> getTestedClass() {
		return V4VibeEncodingOnlyDataFile.class;
	}

	@Override
	V4VibeEncodingOnlyDataFile makeInstanceOne(Path path) {
		return new V4VibeEncodingOnlyDataFile(
				path,
				1, 
				0, 
				new ImportInfo(ImageGenModel.ANIME_V4_FULL, 1, 0.6f),
				new V4VibeEncodingOnlyDataFile.EncodingEntry(new byte[] {1, 2, 3}, VibeEncodingType.V4_FULL));
	}

	@Override
	V4VibeEncodingOnlyDataFile makeInstanceTwo(Path path) {
		return new V4VibeEncodingOnlyDataFile(
				path, 
				2, 
				100, 
				new ImportInfo(ImageGenModel.ANIME_V4_CURATED, 0.9f, 0.3f), 
				null);
	}

	@Override
	V4VibeEncodingOnlyDataFile makeEmptyInstance(Path path) {
		return new V4VibeEncodingOnlyDataFile(path);
	}


    @Nested
    @DisplayName("Vibe data getters/setters work properly")
    class VibeDataTests {
    	@Test
    	void testAddAndGetVibeData() {
    		V4VibeEncodingOnlyDataFile testInstance = makeEmptyInstance(null);
    		assertEquals(0, testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
    		V4VibeData expectedVibeData1 = new V4VibeData(1f, "", VibeEncodingType.V4_FULL, new byte[] {1});
    		testInstance.addVibeData(expectedVibeData1);
    		
    		List<V4VibeData> storedVibeData = testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL);
    		assertEquals(1, storedVibeData.size());
    		V4VibeData actualVibeData1 = storedVibeData.get(0);
    		assertEquals(null, actualVibeData1.getInfoExtracted());
    		assertEquals(expectedVibeData1.getEncodingType(), actualVibeData1.getEncodingType());
    		assertArrayEquals(expectedVibeData1.getEncoding(), actualVibeData1.getEncoding());
    	}
    	
    	@Test
    	void testVibeDataLimited() {
    		V4VibeEncodingOnlyDataFile testInstance = makeEmptyInstance(null);
    		testInstance.addVibeData(new V4VibeData(1f, "", VibeEncodingType.V4_FULL, new byte[] {1}));
    		assertThrows( UnsupportedOperationException.class, ()->testInstance.addVibeData(new V4VibeData(0.5f, "", VibeEncodingType.V4_FULL, new byte[] {1, 2})) );
    	}
    	
    	@Test
    	void testClearVibeData() {
    		V4VibeEncodingOnlyDataFile testInstance = makeEmptyInstance(null);
    		
    		testInstance.addVibeData(new V4VibeData(1f, "", VibeEncodingType.V4_FULL, new byte[] {1}));
    		assertEquals(1, testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
    		testInstance.clearVibeDataForEncodingType(VibeEncodingType.V4_FULL);
    		assertEquals(0, testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());

    		testInstance.addVibeData(new V4VibeData(1f, "", VibeEncodingType.V4_FULL, new byte[] {1}));
    		assertEquals(1, testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
    		testInstance.clearVibeData();
    		assertEquals(0, testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
    	}
    	
    	@Test
    	void testRemoveVibeData() {
    		V4VibeEncodingOnlyDataFile testInstance = makeEmptyInstance(null);
    		V4VibeData expectedVibeData1 = new V4VibeData(1f, "", VibeEncodingType.V4_FULL, new byte[] {1});
    		testInstance.addVibeData(expectedVibeData1);
    		
    		testInstance.removeVibeData(expectedVibeData1);
    		assertEquals(0, testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
    	}
    	
    	@Test
    	void testGetVibeDataByModelAndInfoExtracted() {
    		V4VibeEncodingOnlyDataFile testInstance = makeEmptyInstance(null);
    		V4VibeData expectedVibeData1 = new V4VibeData(1f, "", VibeEncodingType.V4_FULL, new byte[] {1});
    		testInstance.addVibeData(expectedVibeData1);
    		
    		assertFalse(testInstance.tryGetVibeData(ImageGenModel.ANIME_V4_CURATED, 0, 2).isPresent(), "tryGetVibeData() should return an empty optional for a model with no vibe encodings");
    		assertFalse(testInstance.tryGetVibeData(ImageGenModel.ANIME_V4_FULL, 0, 0.1f).isPresent(), "tryGetVibeData() should return an empty optional when there are no matching vibe encodings");
    	}
    }
}
