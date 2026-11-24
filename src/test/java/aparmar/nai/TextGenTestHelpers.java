package aparmar.nai;

import java.util.Arrays;
import java.util.stream.Stream;

import aparmar.nai.data.request.TextGenModel;
import aparmar.nai.utils.AnnotationUtils;

public class TextGenTestHelpers {
	static Stream<TextGenModel> getDeprecatedModels() {
		return Arrays.stream(TextGenModel.values())
				.filter(m -> !AnnotationUtils.isEnumValueExcludedFromTests(m))
				.filter(m -> AnnotationUtils.isEnumValueDeprecated(m));
	}
	static Stream<TextGenModel> getHardDeprecatedModels() {
		return Arrays.stream(TextGenModel.values())
				.filter(m -> !AnnotationUtils.isEnumValueExcludedFromTests(m))
				.filter(m -> AnnotationUtils.isEnumValueHardDeprecated(m));
	}
	static Stream<TextGenModel> getNonDeprecatedModels() {
		return Arrays.stream(TextGenModel.values())
				.filter(m -> !AnnotationUtils.isEnumValueExcludedFromTests(m))
				.filter(m -> !AnnotationUtils.isEnumValueDeprecated(m));
	}
	
	static boolean hasHardDeprecatedModels() {
		return getHardDeprecatedModels().count() > 0;
	}
}
