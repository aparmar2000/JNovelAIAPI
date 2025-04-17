package aparmar.nai.data.file;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;

import aparmar.nai.NAIAPI;
import aparmar.nai.TestHelpers;
import aparmar.nai.data.file.V4VibeDataFile.EmbeddingData;
import aparmar.nai.data.file.V4VibeDataFile.ImportInfo;
import aparmar.nai.data.request.ImageVibeEncodeRequest;
import aparmar.nai.data.request.V4VibeData;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;

class UnitTestV4VibeDataFile extends UnitTestDataFile<V4VibeDataFile> {

	@Override
	Class<V4VibeDataFile> getTestedClass() {
		return V4VibeDataFile.class;
	}

	@Override
	String getFileExtension() {
		return "naiv4vibe";
	}

	@Override
	V4VibeDataFile makeInstanceOne(Path path) {
		SetMultimap<ImageGenModel, EmbeddingData> multimap = MultimapBuilder
				.enumKeys(ImageGenModel.class)
				.hashSetValues()
				.build();
		multimap.put(ImageGenModel.ANIME_V4_FULL, new EmbeddingData(new byte[] {1}, 1));
		multimap.put(ImageGenModel.ANIME_V4_FULL, new EmbeddingData(new byte[] {5, 6}, 0.5f));
		multimap.put(ImageGenModel.ANIME_V4_CURATED, new EmbeddingData(new byte[] {1, 2, 3}, 0.6f));
		return new V4VibeDataFile(
				path,
				1, 
				new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB), 
				multimap, 
				0, 
				new ImportInfo(ImageGenModel.ANIME_V4_FULL, 1, 0.6f));
	}

	@Override
	V4VibeDataFile makeInstanceTwo(Path path) {
		SetMultimap<ImageGenModel, EmbeddingData> multimap = MultimapBuilder
				.enumKeys(ImageGenModel.class)
				.hashSetValues()
				.build();
		return new V4VibeDataFile(
				path, 
				2, 
				new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB), 
				multimap, 
				100, 
				new ImportInfo(ImageGenModel.ANIME_V4_CURATED, 0.9f, 0.3f));
	}

	@Override
	V4VibeDataFile makeEmptyInstance(Path path) {
		return new V4VibeDataFile(path);
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


    @Nested
    @DisplayName("Vibe data getters/setters work properly")
    class VibeDataTests {
    	@Test
    	void testAddAndGetVibeData() {
    		V4VibeDataFile testInstance = makeEmptyInstance(null);
    		assertEquals(0, testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
    		V4VibeData expectedVibeData1 = new V4VibeData(1, "", ImageGenModel.ANIME_V4_FULL, new byte[] {1});
    		V4VibeData expectedVibeData2 = new V4VibeData(0.5f, "", ImageGenModel.ANIME_V4_FULL, new byte[] {1, 2});
    		testInstance.addVibeData(expectedVibeData1);
    		testInstance.addVibeData(expectedVibeData2);
    		
    		List<V4VibeData> storedVibeData = testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL);
    		assertEquals(2, storedVibeData.size());
    		V4VibeData actualVibeData1 = storedVibeData.get(0);
    		assertEquals(actualVibeData1.getInfoExtracted(), expectedVibeData1.getInfoExtracted());
    		assertEquals(actualVibeData1.getModel(), expectedVibeData1.getModel());
    		assertArrayEquals(actualVibeData1.getEncoding(), expectedVibeData1.getEncoding());
    		V4VibeData actualVibeData2 = storedVibeData.get(1);
    		assertEquals(actualVibeData2.getInfoExtracted(), expectedVibeData2.getInfoExtracted());
    		assertEquals(actualVibeData2.getModel(), expectedVibeData2.getModel());
    		assertArrayEquals(actualVibeData2.getEncoding(), expectedVibeData2.getEncoding());
    	}
    	
    	@Test
    	void testClearVibeData() {
    		V4VibeDataFile testInstance = makeEmptyInstance(null);
    		testInstance.addVibeData(new V4VibeData(1, "", ImageGenModel.ANIME_V4_FULL, new byte[] {1}));
    		testInstance.addVibeData(new V4VibeData(0.5f, "", ImageGenModel.ANIME_V4_FULL, new byte[] {1,2}));
    		testInstance.addVibeData(new V4VibeData(0.5f, "", ImageGenModel.ANIME_V4_CURATED, new byte[] {1,2}));
    		
    		testInstance.clearVibeDataForModel(ImageGenModel.ANIME_V4_FULL);
    		assertEquals(0, testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
    		assertEquals(1, testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_CURATED).size());
    		
    		testInstance.clearVibeData();
    		assertEquals(0, testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
    		assertEquals(0, testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_CURATED).size());
    	}
    	
    	@Test
    	void testRemoveVibeData() {
    		V4VibeDataFile testInstance = makeEmptyInstance(null);
    		V4VibeData expectedVibeData1 = new V4VibeData(1, "", ImageGenModel.ANIME_V4_FULL, new byte[] {1});
    		V4VibeData expectedVibeData2 = new V4VibeData(0.5f, "", ImageGenModel.ANIME_V4_FULL, new byte[] {1, 2});
    		testInstance.addVibeData(expectedVibeData1);
    		testInstance.addVibeData(expectedVibeData2);
    		testInstance.addVibeData(new V4VibeData(0.5f, "", ImageGenModel.ANIME_V4_CURATED, new byte[] {1,2}));
    		
    		testInstance.removeVibeData(expectedVibeData1);
    		List<V4VibeData> storedVibeData = testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL);
    		assertEquals(1, storedVibeData.size());
    		assertEquals(1, testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_CURATED).size());
    		V4VibeData actualVibeData = storedVibeData.get(0);
    		assertEquals(actualVibeData.getInfoExtracted(), expectedVibeData2.getInfoExtracted());
    		assertEquals(actualVibeData.getModel(), expectedVibeData2.getModel());
    		assertArrayEquals(actualVibeData.getEncoding(), expectedVibeData2.getEncoding());
    	}
    	
    	@Test
    	void testGetVibeDataByModelAndInfoExtracted() {
    		V4VibeDataFile testInstance = makeEmptyInstance(null);
    		V4VibeData expectedVibeData1 = new V4VibeData(1, "", ImageGenModel.ANIME_V4_FULL, new byte[] {1});
    		V4VibeData expectedVibeData2 = new V4VibeData(0.5f, "", ImageGenModel.ANIME_V4_FULL, new byte[] {1, 2});
    		testInstance.addVibeData(expectedVibeData1);
    		testInstance.addVibeData(expectedVibeData2);
    		
    		assertFalse(testInstance.tryGetVibeData(ImageGenModel.ANIME_V4_CURATED, 0, 2).isPresent(), "tryGetVibeData() should return an empty optional for a model with no vibe encodings");
    		assertFalse(testInstance.tryGetVibeData(ImageGenModel.ANIME_V4_FULL, 0, 0.1f).isPresent(), "tryGetVibeData() should return an empty optional when there are no matching vibe encodings");
    		
    		Optional<V4VibeData> actualVibeData1 = testInstance.tryGetVibeData(ImageGenModel.ANIME_V4_FULL, 1);
    		Optional<V4VibeData> actualVibeData2 = testInstance.tryGetVibeData(ImageGenModel.ANIME_V4_FULL, 0.9f, 0.2f);
    		Optional<V4VibeData> actualVibeData3 = testInstance.tryGetVibeData(ImageGenModel.ANIME_V4_FULL, 0.5f);
    		Optional<V4VibeData> actualVibeData4 = testInstance.tryGetVibeData(ImageGenModel.ANIME_V4_FULL, 0.5f, 2);
    		assertTrue(actualVibeData1.isPresent());
    		assertTrue(actualVibeData2.isPresent());
    		assertTrue(actualVibeData3.isPresent());
    		assertTrue(actualVibeData4.isPresent());
    		
    		assertArrayEquals(actualVibeData1.get().getEncoding(), expectedVibeData1.getEncoding());
    		assertArrayEquals(actualVibeData2.get().getEncoding(), expectedVibeData1.getEncoding());
    		assertArrayEquals(actualVibeData3.get().getEncoding(), expectedVibeData2.getEncoding());
    		assertArrayEquals(actualVibeData4.get().getEncoding(), expectedVibeData2.getEncoding());
    	}
    	
    	@Test
    	void testGetOrRequestVibeData() throws IOException, InterruptedException, ExecutionException {
    		NAIAPI mockNai = mock(NAIAPI.class);
    		V4VibeData expectedVibeData = new V4VibeData(1, "", ImageGenModel.ANIME_V4_FULL, new byte[] {1, 2});
    		when(mockNai.encodeImageVibe(any())).then(AdditionalAnswers.answersWithDelay(100, i -> expectedVibeData));
    		V4VibeDataFile testInstance = makeEmptyInstance(null);
    		
    		ExecutorService executor = Executors.newCachedThreadPool();
    		Future<V4VibeData> actualVibeData1Future = executor.submit(()->testInstance.getOrRequestVibeData(mockNai, ImageGenModel.ANIME_V4_FULL, 1));
    		Future<V4VibeData> actualVibeData2Future = executor.submit(()->testInstance.getOrRequestVibeData(mockNai, ImageGenModel.ANIME_V4_FULL, 1));
    		
    		V4VibeData actualVibeData1 = actualVibeData1Future.get();
    		V4VibeData actualVibeData2 = actualVibeData2Future.get();
    		
    		verify(mockNai, times(1)).encodeImageVibe(any());
    		assertEquals(actualVibeData1, actualVibeData2);
    		assertEquals(actualVibeData1.getModel(), expectedVibeData.getModel());
    		assertEquals(actualVibeData1.getInfoExtracted(), expectedVibeData.getInfoExtracted());
    		assertArrayEquals(actualVibeData1.getEncoding(), expectedVibeData.getEncoding());
    	}
    }
    
	@Test
	void testEncodeRequestGeneration() {
		V4VibeDataFile testInstance = makeEmptyInstance(null);
		BufferedImage expectedImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		testInstance.setImage(expectedImage);
		
		ImageVibeEncodeRequest actualEncodeRequest = testInstance.buildEncodeRequest(ImageGenModel.ANIME_V4_FULL, 0.6f);
		assertEquals(actualEncodeRequest.getModel(), ImageGenModel.ANIME_V4_FULL);
		assertEquals(actualEncodeRequest.getInformationExtracted(), 0.6f);
		assertEquals(actualEncodeRequest.getImage().getImage(), expectedImage);
	}
}
