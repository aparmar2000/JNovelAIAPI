package aparmar.nai.data.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import aparmar.nai.utils.InternalResourceLoader;

@Tag("integration")
public class IntegrationTestV4VibeBundleDataFile {
	@Test
	void testLoadRealV4VibeBundleWithImageDataFile(@TempDir Path tempDir) throws FileNotFoundException, IOException {
		V4VibeBundleDataFile<V4VibeWithImageDataFile> vibeBundleFile = new V4VibeBundleDataFile<V4VibeWithImageDataFile>(null)
				.loadFromStream(InternalResourceLoader.getInternalResourceAsStream("image_vibe_bundle.naiv4vibebundle"));

		assertEquals(1, vibeBundleFile.getVersion());
		assertEquals(2, vibeBundleFile.size());
		
		V4VibeWithImageDataFile vibeFile1 = vibeBundleFile.get(0);
		assertEquals(1, vibeFile1.getVersion());
		assertEquals("700f94-d2e908", vibeFile1.getName());
		assertEquals(1744861688908l, vibeFile1.getCreatedAt());
		assertEquals(ImageGenModel.ANIME_V4_FULL, vibeFile1.getImportInfo().getModel());
		assertEquals(1f, vibeFile1.getImportInfo().getInformationExtracted());
		assertEquals(0.6f, vibeFile1.getImportInfo().getStrength());
		assertEquals(3, vibeFile1.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
		assertEquals(2, vibeFile1.getVibeDataForModel(ImageGenModel.ANIME_V4_CURATED).size());
		
		V4VibeWithImageDataFile vibeFile2 = vibeBundleFile.get(1);
		assertEquals(1, vibeFile2.getVersion());
		assertEquals("7380e3-8a50f2", vibeFile2.getName());
		assertEquals(1745291432008l, vibeFile2.getCreatedAt());
		assertEquals(ImageGenModel.ANIME_V4_FULL, vibeFile2.getImportInfo().getModel());
		assertEquals(1f, vibeFile2.getImportInfo().getInformationExtracted());
		assertEquals(0.6f, vibeFile2.getImportInfo().getStrength());
		assertEquals(1, vibeFile2.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
		assertEquals(0, vibeFile2.getVibeDataForModel(ImageGenModel.ANIME_V4_CURATED).size());
		
		Path resavedPath = tempDir.resolve("temp_resave.naiv4vibebundle");
		vibeBundleFile = vibeBundleFile.saveToFile(resavedPath.toFile());
		V4VibeBundleDataFile<V4VibeWithImageDataFile> resavedVibeBundleFile = new V4VibeBundleDataFile<V4VibeWithImageDataFile>(resavedPath).load();
		assertEquals(vibeBundleFile, resavedVibeBundleFile);
	}
	
	@Test
	void testLoadRealV4VibeBundleWithEncodingOnlyDataFile(@TempDir Path tempDir) throws FileNotFoundException, IOException {
		V4VibeBundleDataFile<V4VibeEncodingOnlyDataFile> vibeBundleFile = new V4VibeBundleDataFile<V4VibeEncodingOnlyDataFile>(null)
				.loadFromStream(InternalResourceLoader.getInternalResourceAsStream("encoding_vibe_bundle.naiv4vibebundle"));

		assertEquals(1, vibeBundleFile.getVersion());
		assertEquals(2, vibeBundleFile.size());
		
		V4VibeEncodingOnlyDataFile vibeFile1 = vibeBundleFile.get(0);
		assertEquals(1, vibeFile1.getVersion());
		assertEquals("700f94-d2e908", vibeFile1.getName());
		assertEquals(1744861688908l, vibeFile1.getCreatedAt());
		assertEquals(ImageGenModel.ANIME_V4_FULL, vibeFile1.getImportInfo().getModel());
		assertEquals(1f, vibeFile1.getImportInfo().getInformationExtracted());
		assertEquals(0.6f, vibeFile1.getImportInfo().getStrength());
		assertEquals(1, vibeFile1.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
		assertEquals(0, vibeFile1.getVibeDataForModel(ImageGenModel.ANIME_V4_CURATED).size());
		
		V4VibeEncodingOnlyDataFile vibeFile2 = vibeBundleFile.get(1);
		assertEquals(1, vibeFile2.getVersion());
		assertEquals("7380e3-8a50f2", vibeFile2.getName());
		assertEquals(1745291432008l, vibeFile2.getCreatedAt());
		assertEquals(ImageGenModel.ANIME_V4_FULL, vibeFile2.getImportInfo().getModel());
		assertEquals(1f, vibeFile2.getImportInfo().getInformationExtracted());
		assertEquals(0.6f, vibeFile2.getImportInfo().getStrength());
		assertEquals(1, vibeFile2.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
		assertEquals(0, vibeFile2.getVibeDataForModel(ImageGenModel.ANIME_V4_CURATED).size());
		
		Path resavedPath = tempDir.resolve("temp_resave.naiv4vibebundle");
		vibeBundleFile = vibeBundleFile.saveToFile(resavedPath.toFile());
		V4VibeBundleDataFile<V4VibeEncodingOnlyDataFile> resavedVibeBundleFile = new V4VibeBundleDataFile<V4VibeEncodingOnlyDataFile>(resavedPath).load();
		assertEquals(vibeBundleFile, resavedVibeBundleFile);
	}
	
	@Test
	void testLoadRealV4VibeBundleWithMixedVibeDataFiles(@TempDir Path tempDir) throws FileNotFoundException, IOException {
		V4VibeBundleDataFile<V4VibeDataFile<?>> vibeBundleFile = new V4VibeBundleDataFile<V4VibeDataFile<?>>(null)
				.loadFromStream(InternalResourceLoader.getInternalResourceAsStream("mixed_vibe_bundle.naiv4vibebundle"));

		assertEquals(1, vibeBundleFile.getVersion());
		assertEquals(2, vibeBundleFile.size());
		
		V4VibeDataFile<?> vibeFile1 = vibeBundleFile.get(0);
		assertTrue(V4VibeEncodingOnlyDataFile.class.isInstance(vibeFile1), "Subfile should be loaded into the correct type.");
		assertEquals(1, vibeFile1.getVersion());
		assertEquals("700f94-d2e908", vibeFile1.getName());
		assertEquals(1744861688908l, vibeFile1.getCreatedAt());
		assertEquals(ImageGenModel.ANIME_V4_FULL, vibeFile1.getImportInfo().getModel());
		assertEquals(1f, vibeFile1.getImportInfo().getInformationExtracted());
		assertEquals(0.6f, vibeFile1.getImportInfo().getStrength());
		assertEquals(1, vibeFile1.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
		assertEquals(0, vibeFile1.getVibeDataForModel(ImageGenModel.ANIME_V4_CURATED).size());
		
		V4VibeDataFile<?> vibeFile2 = vibeBundleFile.get(1);
		assertTrue(V4VibeWithImageDataFile.class.isInstance(vibeFile2), "Subfile should be loaded into the correct type.");
		assertEquals(1, vibeFile2.getVersion());
		assertEquals("7380e3-8a50f2", vibeFile2.getName());
		assertEquals(1745291432008l, vibeFile2.getCreatedAt());
		assertEquals(ImageGenModel.ANIME_V4_FULL, vibeFile2.getImportInfo().getModel());
		assertEquals(1f, vibeFile2.getImportInfo().getInformationExtracted());
		assertEquals(0.6f, vibeFile2.getImportInfo().getStrength());
		assertEquals(1, vibeFile2.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
		assertEquals(0, vibeFile2.getVibeDataForModel(ImageGenModel.ANIME_V4_CURATED).size());
		
		Path resavedPath = tempDir.resolve("temp_resave.naiv4vibebundle");
		vibeBundleFile = vibeBundleFile.saveToFile(resavedPath.toFile());
		V4VibeBundleDataFile<V4VibeDataFile<?>> resavedVibeBundleFile = new V4VibeBundleDataFile<V4VibeDataFile<?>>(resavedPath).load();
		assertEquals(vibeBundleFile, resavedVibeBundleFile);
	}
}
