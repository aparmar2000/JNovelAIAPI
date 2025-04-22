package aparmar.nai.data.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import aparmar.nai.utils.InternalResourceLoader;

@Tag("integration")
public class IntegrationTestV4VibeDataFile {
	@Test
	void testLoadRealV4VibeWithImageDataFile(@TempDir Path tempDir) throws FileNotFoundException, IOException {
		V4VibeWithImageDataFile vibeFile = new V4VibeWithImageDataFile(null).loadFromStream(InternalResourceLoader.getInternalResourceAsStream("700f94-d2e908.naiv4vibe"));
		
		assertEquals(1, vibeFile.getVersion());
		assertEquals(1744861688908l, vibeFile.getCreatedAt());
		assertEquals(ImageGenModel.ANIME_V4_FULL, vibeFile.getImportInfo().getModel());
		assertEquals(0.61f, vibeFile.getImportInfo().getInformationExtracted());
		assertEquals(0.6f, vibeFile.getImportInfo().getStrength());
		assertEquals(3, vibeFile.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
		assertEquals(2, vibeFile.getVibeDataForModel(ImageGenModel.ANIME_V4_CURATED).size());
		
		Path resavedPath = tempDir.resolve("temp_resave.naiv4vibe");
		vibeFile = vibeFile.saveToFile(resavedPath.toFile());
		V4VibeWithImageDataFile resavedVibeFile = new V4VibeWithImageDataFile(resavedPath).load();
		assertEquals(vibeFile, resavedVibeFile);
	}
	
	@Test
	void testLoadRealV4VibeEncodingOnlyDataFile(@TempDir Path tempDir) throws FileNotFoundException, IOException {
		V4VibeEncodingOnlyDataFile vibeFile = new V4VibeEncodingOnlyDataFile(null).loadFromStream(InternalResourceLoader.getInternalResourceAsStream("c5301c-e4af6f.naiv4vibe"));
		
		assertEquals(1, vibeFile.getVersion());
		assertEquals(1744827046122l, vibeFile.getCreatedAt());
		assertEquals(ImageGenModel.ANIME_V4_FULL, vibeFile.getImportInfo().getModel());
		assertEquals(1f, vibeFile.getImportInfo().getInformationExtracted());
		assertEquals(0.1f, vibeFile.getImportInfo().getStrength());
		assertEquals(1, vibeFile.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
		assertEquals(0, vibeFile.getVibeDataForModel(ImageGenModel.ANIME_V4_CURATED).size());
		
		Path resavedPath = tempDir.resolve("temp_resave.naiv4vibe");
		vibeFile = vibeFile.saveToFile(resavedPath.toFile());
		V4VibeEncodingOnlyDataFile resavedVibeFile = new V4VibeEncodingOnlyDataFile(resavedPath).load();
		assertEquals(vibeFile, resavedVibeFile);
	}
	
	static Stream<Arguments> provideTestVibeFiles() {
		return Stream.of(
				Arguments.of("700f94-d2e908.naiv4vibe", V4VibeWithImageDataFile.class),
				Arguments.of("c5301c-e4af6f.naiv4vibe", V4VibeEncodingOnlyDataFile.class)
				);
	}
	
	@ParameterizedTest
	@MethodSource("provideTestVibeFiles")
	void testLoadRealVibeFiles(String sourceFile, Class<? extends V4VibeDataFile<?>> expectedClass, @TempDir Path tempDir) throws FileNotFoundException, IOException {
		V4VibeDataFile<?> vibeFile = V4VibeDataFile.loadUnknownV4VibeDataFileFromStream(InternalResourceLoader.getInternalResourceAsStream(sourceFile));
		
		assertTrue(expectedClass.isInstance(vibeFile), "File should be loaded into the correct type.");
	}
}
