package aparmar.nai.data.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import aparmar.nai.data.file.V4VibeDataFile;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import aparmar.nai.utils.InternalResourceLoader;

@Tag("integration")
public class IntegrationTestV4VibeDataFile {
	@Test
	void testLoadRealVibeFile(@TempDir Path tempDir) throws FileNotFoundException, IOException {
		V4VibeDataFile vibeFile = new V4VibeDataFile(null).loadFromStream(InternalResourceLoader.getInternalResourceAsStream("700f94-d2e908.naiv4vibe"));
		
		assertEquals(1, vibeFile.getVersion());
		assertEquals(1744861688908l, vibeFile.getCreatedAt());
		assertEquals(ImageGenModel.ANIME_V4_FULL, vibeFile.getImportInfo().getModel());
		assertEquals(0.61f, vibeFile.getImportInfo().getInformationExtracted());
		assertEquals(0.6f, vibeFile.getImportInfo().getStrength());
		assertEquals(3, vibeFile.getVibeDataForModel(ImageGenModel.ANIME_V4_FULL).size());
		assertEquals(2, vibeFile.getVibeDataForModel(ImageGenModel.ANIME_V4_CURATED).size());
		
		Path resavedPath = tempDir.resolve("temp_resave.naiv4vibe");
		vibeFile = vibeFile.saveToFile(resavedPath.toFile());
		V4VibeDataFile resavedVibeFile = new V4VibeDataFile(resavedPath).load();
		assertEquals(vibeFile, resavedVibeFile);
	}
}
