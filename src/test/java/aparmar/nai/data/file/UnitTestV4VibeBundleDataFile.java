package aparmar.nai.data.file;

import java.nio.file.Path;
import java.util.Collections;

import aparmar.nai.data.file.V4VibeDataFile.ImportInfo;
import aparmar.nai.data.request.V4VibeData.VibeEncodingType;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import lombok.val;

public class UnitTestV4VibeBundleDataFile extends UnitTestNestedDataFileSharedMethods<V4VibeBundleDataFile<V4VibeEncodingOnlyDataFile>, V4VibeEncodingOnlyDataFile> {

	@SuppressWarnings("unchecked")
	@Override
	Class<V4VibeBundleDataFile<V4VibeEncodingOnlyDataFile>> getTestedClass() {
		return (Class<V4VibeBundleDataFile<V4VibeEncodingOnlyDataFile>>) makeEmptyInstance(null).getClass();
	}

	@Override
	String getFileExtension() {
		return "naiv4vibebundle";
	}

	@Override
	V4VibeBundleDataFile<V4VibeEncodingOnlyDataFile> makeInstanceOne(Path path) {
		val encodingFile = new V4VibeEncodingOnlyDataFile(
				null,
				1, 
				"instance_one",
				0, 
				new ImportInfo(ImageGenModel.ANIME_V4_FULL, 1, 0.6f),
				new V4VibeEncodingOnlyDataFile.EncodingEntry(new byte[] {1, 2}, VibeEncodingType.V4_FULL));
		return new V4VibeBundleDataFile<>(path, 1, Collections.singletonList(encodingFile));
	}

	@Override
	V4VibeBundleDataFile<V4VibeEncodingOnlyDataFile> makeInstanceTwo(Path path) {
		val encodingFile = new V4VibeEncodingOnlyDataFile(
				null,
				1, 
				"instance_two",
				0, 
				new ImportInfo(ImageGenModel.ANIME_V4_FULL, 1, 0.6f),
				new V4VibeEncodingOnlyDataFile.EncodingEntry(new byte[] {4, 9, 3}, VibeEncodingType.V4_FULL));
		return new V4VibeBundleDataFile<>(path, 1, Collections.singletonList(encodingFile));
	}

	@Override
	V4VibeBundleDataFile<V4VibeEncodingOnlyDataFile> makeEmptyInstance(Path path) {
		return new V4VibeBundleDataFile<>(path);
	}
}