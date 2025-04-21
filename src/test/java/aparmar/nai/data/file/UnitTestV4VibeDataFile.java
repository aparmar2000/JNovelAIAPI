package aparmar.nai.data.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedConstruction;
import org.mockito.MockedConstruction.Context;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import aparmar.nai.data.file.V4VibeDataFile.VibeFileType;

public class UnitTestV4VibeDataFile {
	final Gson gson = new Gson();
	
	static Stream<Arguments> generateVibeFileTypeTestArguments() {
		return Stream.of(
				Arguments.of(VibeFileType.IMAGE, V4VibeWithImageDataFile.class),
				Arguments.of(VibeFileType.ENCODING, V4VibeEncodingOnlyDataFile.class),
				Arguments.of(null, null)
				);
	}

	@ParameterizedTest
	@MethodSource("generateVibeFileTypeTestArguments")
	<T extends V4VibeDataFile<T>> void testLoadUnknownV4VibeDataFile(VibeFileType vibeFileType, Class<T> expectedLoadedClazz) throws IOException {
		final JsonObject testJsonObject = new JsonObject();
		if (vibeFileType != null) {
			testJsonObject.add("type", gson.toJsonTree(vibeFileType));
		}
		
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		try (OutputStreamWriter outWriter = new OutputStreamWriter(outStream);) {
			gson.toJson(testJsonObject, outWriter);
		}
		byte[] testJsonBytes = outStream.toByteArray();
		outStream.close();
		
		try (
				ByteArrayInputStream testInputStream = new ByteArrayInputStream(testJsonBytes);
				MockedConstruction<V4VibeWithImageDataFile> imageFileConstructorMock = Mockito.mockConstruction(V4VibeWithImageDataFile.class, UnitTestV4VibeDataFile::buildLoadMethodMocksForVibeDataFile);
				MockedConstruction<V4VibeEncodingOnlyDataFile> encodingFileConstructorMock = Mockito.mockConstruction(V4VibeEncodingOnlyDataFile.class, UnitTestV4VibeDataFile::buildLoadMethodMocksForVibeDataFile);
			) {
			if (vibeFileType == null) {
				assertThrows(IOException.class, ()->V4VibeDataFile.loadUnknownV4VibeDataFileFromJson(testJsonObject));
				assertThrows(IOException.class, ()->V4VibeDataFile.loadUnknownV4VibeDataFileFromStream(testInputStream));
			} else {
				assertEquals(expectedLoadedClazz, V4VibeDataFile.loadUnknownV4VibeDataFileFromJson(testJsonObject).getClass());
				assertEquals(expectedLoadedClazz, V4VibeDataFile.loadUnknownV4VibeDataFileFromStream(testInputStream).getClass());
			}
		}
	}
	
	static <T extends V4VibeDataFile<T>> void buildLoadMethodMocksForVibeDataFile(T mock, Context context) throws IOException {
		when(mock.load()).thenReturn(mock);
		when(mock.loadFromStream(any())).thenReturn(mock);
		when(mock.loadFromJson(any())).thenReturn(mock);
	}
}
