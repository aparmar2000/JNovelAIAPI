package aparmar.nai.data.file;

import java.nio.file.Path;
import java.util.UUID;

import aparmar.nai.data.request.TextGenModel;
import aparmar.nai.data.request.textgen.TextGenerationParameters;

public class UnitTestTextGenPresetDataFile extends UnitTestDataFileSharedMethods<TextGenPresetDataFile> {
	public static final UUID TEST_UUID_ONE = UUID.fromString("4b545ebb-c2b8-40bb-8758-af22e1f3c4e7");
	public static final UUID TEST_UUID_TWO = UUID.fromString("0dff6e2a-c0f5-4e46-9eb6-b1a592d4f197");
	public static final UUID TEST_REMOTE_UUID = UUID.fromString("0875b204-ed7f-4b87-9702-14d8ed3bd4d0");

	@Override
	Class<TextGenPresetDataFile> getTestedClass() {
		return TextGenPresetDataFile.class;
	}

	@Override
	String getFileExtension() {
		return "preset";
	}

	@Override
	TextGenPresetDataFile makeInstanceOne(Path path) {
		return new TextGenPresetDataFile(path, "Test Preset One", TEST_UUID_ONE, null, TextGenModel.CASSANDRA, TextGenerationParameters.builder()
				.temperature(0.5)
				.maxLength(200)
				.minLength(1)
				.build());
	}

	@Override
	TextGenPresetDataFile makeInstanceTwo(Path path) {
		return new TextGenPresetDataFile(path, "Test Preset Two", TEST_UUID_TWO, TEST_REMOTE_UUID, TextGenModel.ERATO, TextGenerationParameters.builder()
				.temperature(0.2)
				.maxLength(1024)
				.minLength(1)
				.build());
	}

	@Override
	TextGenPresetDataFile makeEmptyInstance(Path path) {
		return new TextGenPresetDataFile(path);
	}

}
