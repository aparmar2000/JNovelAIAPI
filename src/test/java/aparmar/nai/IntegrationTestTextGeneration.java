package aparmar.nai;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import aparmar.nai.data.request.TextGenModel;
import aparmar.nai.data.request.TextGenerationParameters;
import aparmar.nai.data.request.TextGenerationRequest;
import aparmar.nai.data.response.TextGenerationResponse;
import aparmar.nai.utils.TextParameterPresets;

class IntegrationTestTextGeneration extends AbstractFeatureIntegrationTest {

	@Test
	void testMinimalTextGeneration() throws AssertionError, Exception {
		TestHelpers.runTestToleratingTimeouts(3, 1000, ()->{
			String testPresetName = TextParameterPresets.getAssociatedPresets(TextGenModel.CLIO)[0];
			TextGenerationParameters testPreset = TextParameterPresets.getPresetByExtendedName(testPresetName);
			TextGenerationRequest testRequest = TextGenerationRequest.builder()
					.model(TextGenModel.CLIO)
					.input("This is an API call!\n")
					.parameters(testPreset.toBuilder()
							.useString(true)
							.build())
					.build();
			TextGenerationResponse actualResponse = apiInstance.generateText(testRequest);
			assertNotNull(actualResponse);
			assertNotNull(actualResponse.getOutput());
			assertTrue(actualResponse.getOutput().getTextChunk().length() > 0);
		});
	}

}
