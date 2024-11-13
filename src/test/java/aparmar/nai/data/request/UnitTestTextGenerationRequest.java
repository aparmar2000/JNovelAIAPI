package aparmar.nai.data.request;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

import aparmar.nai.TestHelpers;
import aparmar.nai.data.request.textgen.TextGenerationParameters;

class UnitTestTextGenerationRequest {

	@Test
	void testTextGenerationRequestDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		TextGenerationRequest testInstance1 = new TextGenerationRequest();
		TextGenerationRequest testInstance2 = TextGenerationRequest.builder()
				.input("prompt")
				.model(TextGenModel.CLIO)
				.parameters(new TextGenerationParameters())
				.build();
		
		TestHelpers.autoTestDataAndToBuilderAnnotation(TextGenerationRequest.class, testInstance1, testInstance2);
	}

}
