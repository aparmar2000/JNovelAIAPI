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
import aparmar.nai.data.file.V4VibeDataFile.ImportInfo;
import aparmar.nai.data.file.V4VibeWithImageDataFile.EmbeddingData;
import aparmar.nai.data.request.ImageVibeEncodeRequest;
import aparmar.nai.data.request.V4VibeData;
import aparmar.nai.data.request.V4VibeData.VibeEncodingType;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;

class UnitTestV4VibeWithImageDataFile extends UnitTestV4VibeDataFile<V4VibeWithImageDataFile> {

	@Override
	Class<V4VibeWithImageDataFile> getTestedClass() {
		return V4VibeWithImageDataFile.class;
	}

	@Override
	V4VibeWithImageDataFile makeInstanceOne(Path path) {
		SetMultimap<VibeEncodingType, EmbeddingData> multimap = MultimapBuilder
				.enumKeys(VibeEncodingType.class)
				.hashSetValues()
				.build();
		multimap.put(VibeEncodingType.V4_FULL, new EmbeddingData(new byte[] {1}, 1));
		multimap.put(VibeEncodingType.V4_FULL, new EmbeddingData(new byte[] {5, 6}, 0.5f));
		multimap.put(VibeEncodingType.V4_CURATED, new EmbeddingData(new byte[] {1, 2, 3}, 0.6f));
		return new V4VibeWithImageDataFile(
				path,
				1, 
				0, 
				new ImportInfo(ImageGenModel.ANIME_V4_FULL, 1, 0.6f),
				new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB), 
				multimap);
	}

	@Override
	V4VibeWithImageDataFile makeInstanceTwo(Path path) {
		SetMultimap<VibeEncodingType, EmbeddingData> multimap = MultimapBuilder
				.enumKeys(VibeEncodingType.class)
				.hashSetValues()
				.build();
		return new V4VibeWithImageDataFile(
				path, 
				2, 
				100, 
				new ImportInfo(ImageGenModel.ANIME_V4_CURATED, 0.9f, 0.3f), 
				new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB), 
				multimap);
	}

	@Override
	V4VibeWithImageDataFile makeEmptyInstance(Path path) {
		return new V4VibeWithImageDataFile(path);
	}


    @Nested
    @DisplayName("Vibe data getters/setters work properly")
    class VibeDataTests {
    	@Test
    	void testAddAndGetVibeData() {
    		V4VibeWithImageDataFile testInstance = makeEmptyInstance(null);
    		assertEquals(0, testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
    		V4VibeData expectedVibeData1 = new V4VibeData(1f, "", VibeEncodingType.V4_FULL, new byte[] {1});
    		V4VibeData expectedVibeData2 = new V4VibeData(0.5f, "", VibeEncodingType.V4_FULL, new byte[] {1, 2});
    		testInstance.addVibeData(expectedVibeData1);
    		testInstance.addVibeData(expectedVibeData2);
    		
    		List<V4VibeData> storedVibeData = testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL);
    		assertEquals(2, storedVibeData.size());
    		V4VibeData actualVibeData1 = storedVibeData.get(0);
    		assertEquals(expectedVibeData1.getInfoExtracted(), actualVibeData1.getInfoExtracted());
    		assertEquals(expectedVibeData1.getEncodingType(), actualVibeData1.getEncodingType());
    		assertArrayEquals(expectedVibeData1.getEncoding(), actualVibeData1.getEncoding());
    		V4VibeData actualVibeData2 = storedVibeData.get(1);
    		assertEquals(expectedVibeData2.getInfoExtracted(), actualVibeData2.getInfoExtracted());
    		assertEquals(expectedVibeData2.getEncodingType(), actualVibeData2.getEncodingType());
    		assertArrayEquals(expectedVibeData2.getEncoding(), actualVibeData2.getEncoding());
    	}
    	
    	@Test
    	void testClearVibeData() {
    		V4VibeWithImageDataFile testInstance = makeEmptyInstance(null);
    		testInstance.addVibeData(new V4VibeData(1f, "", VibeEncodingType.V4_FULL, new byte[] {1}));
    		testInstance.addVibeData(new V4VibeData(0.5f, "", VibeEncodingType.V4_FULL, new byte[] {1,2}));
    		testInstance.addVibeData(new V4VibeData(0.5f, "", VibeEncodingType.V4_CURATED, new byte[] {1,2}));
    		
    		testInstance.clearVibeDataForEncodingType(VibeEncodingType.V4_FULL);
    		assertEquals(0, testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
    		assertEquals(1, testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_CURATED).size());
    		
    		testInstance.clearVibeData();
    		assertEquals(0, testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
    		assertEquals(0, testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_CURATED).size());
    	}
    	
    	@Test
    	void testRemoveVibeData() {
    		V4VibeWithImageDataFile testInstance = makeEmptyInstance(null);
    		V4VibeData expectedVibeData1 = new V4VibeData(1f, "", VibeEncodingType.V4_FULL, new byte[] {1});
    		V4VibeData expectedVibeData2 = new V4VibeData(0.5f, "", VibeEncodingType.V4_FULL, new byte[] {1, 2});
    		testInstance.addVibeData(expectedVibeData1);
    		testInstance.addVibeData(expectedVibeData2);
    		testInstance.addVibeData(new V4VibeData(0.5f, "", VibeEncodingType.V4_CURATED, new byte[] {1,2}));
    		
    		testInstance.removeVibeData(expectedVibeData1);
    		List<V4VibeData> storedVibeData = testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL);
    		assertEquals(1, storedVibeData.size());
    		assertEquals(1, testInstance.getVibeDataForModel(ImageGenModel.ANIME_V4_CURATED).size());
    		V4VibeData actualVibeData = storedVibeData.get(0);
    		assertEquals(expectedVibeData2.getInfoExtracted(), actualVibeData.getInfoExtracted());
    		assertEquals(expectedVibeData2.getEncodingType(), actualVibeData.getEncodingType());
    		assertArrayEquals(expectedVibeData2.getEncoding(), actualVibeData.getEncoding());
    	}
    	
    	@Test
    	void testGetVibeDataByModelAndInfoExtracted() {
    		V4VibeWithImageDataFile testInstance = makeEmptyInstance(null);
    		V4VibeData expectedVibeData1 = new V4VibeData(1f, "", VibeEncodingType.V4_FULL, new byte[] {1});
    		V4VibeData expectedVibeData2 = new V4VibeData(0.5f, "", VibeEncodingType.V4_FULL, new byte[] {1, 2});
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
    		
    		assertArrayEquals(expectedVibeData1.getEncoding(), actualVibeData1.get().getEncoding());
    		assertArrayEquals(expectedVibeData1.getEncoding(), actualVibeData2.get().getEncoding());
    		assertArrayEquals(expectedVibeData2.getEncoding(), actualVibeData3.get().getEncoding());
    		assertArrayEquals(expectedVibeData2.getEncoding(), actualVibeData4.get().getEncoding());
    	}
    	
    	@Test
    	void testGetOrRequestVibeData() throws IOException, InterruptedException, ExecutionException {
    		NAIAPI mockNai = mock(NAIAPI.class);
    		V4VibeData expectedVibeData = new V4VibeData(1f, "", VibeEncodingType.V4_FULL, new byte[] {1, 2});
    		when(mockNai.encodeImageVibe(any())).then(AdditionalAnswers.answersWithDelay(100, i -> expectedVibeData));
    		V4VibeWithImageDataFile testInstance = makeEmptyInstance(null);
    		
    		ExecutorService executor = Executors.newCachedThreadPool();
    		Future<V4VibeData> actualVibeData1Future = executor.submit(()->testInstance.getOrRequestVibeData(mockNai, ImageGenModel.ANIME_V4_FULL, 1));
    		Future<V4VibeData> actualVibeData2Future = executor.submit(()->testInstance.getOrRequestVibeData(mockNai, ImageGenModel.ANIME_V4_FULL, 1));
    		
    		V4VibeData actualVibeData1 = actualVibeData1Future.get();
    		V4VibeData actualVibeData2 = actualVibeData2Future.get();
    		
    		verify(mockNai, times(1)).encodeImageVibe(any());
    		assertEquals(actualVibeData1, actualVibeData2);
    		assertEquals(expectedVibeData.getEncodingType(), actualVibeData1.getEncodingType());
    		assertEquals(expectedVibeData.getInfoExtracted(), actualVibeData1.getInfoExtracted());
    		assertArrayEquals(expectedVibeData.getEncoding(), actualVibeData1.getEncoding());
    	}
    }
    
	@Test
	void testEncodeRequestGeneration() {
		V4VibeWithImageDataFile testInstance = makeEmptyInstance(null);
		BufferedImage expectedImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		testInstance.setImage(expectedImage);
		
		ImageVibeEncodeRequest actualEncodeRequest = testInstance.buildEncodeRequest(ImageGenModel.ANIME_V4_FULL, 0.6f);
		assertEquals(ImageGenModel.ANIME_V4_FULL, actualEncodeRequest.getModel());
		assertEquals(0.6f, actualEncodeRequest.getInformationExtracted());
		assertEquals(expectedImage, actualEncodeRequest.getImage().getImage());
	}
}
