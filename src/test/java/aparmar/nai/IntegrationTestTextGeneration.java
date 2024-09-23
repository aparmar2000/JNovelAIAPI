package aparmar.nai;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import aparmar.nai.data.request.TextGenModel;
import aparmar.nai.data.request.TextGenerationRequest;
import aparmar.nai.data.request.textgen.TextGenerationParameters;
import aparmar.nai.data.response.TextGenerationResponse;
import aparmar.nai.utils.TextParameterPresets;
import aparmar.nai.utils.tokenization.TokenizedChunk;

class IntegrationTestTextGeneration extends AbstractFeatureIntegrationTest {

	@ParameterizedTest
	@EnumSource(value = TextGenModel.class, names = {"SIGURD","EUTERPE","KRAKE","CLIO","KAYRA","ERATO"})
	void testMinimalTextGeneration(TextGenModel textGenModel) throws AssertionError, Exception {
		String[] associatedPresets = TextParameterPresets.getAssociatedPresets(textGenModel);
		TextGenerationParameters testPreset = associatedPresets.length>0 ? 
				TextParameterPresets.getPresetByExtendedName(associatedPresets[0]) : 
				TextGenerationParameters.builder()
					.temperature(1)
					.minLength(10)
					.maxLength(30)
					.build();
		TokenizedChunk tokenizedInput = new TokenizedChunk(textGenModel, "This is an API call!\n");
		TestHelpers.runTestToleratingTimeouts(3, 1000, ()->{
			TextGenerationRequest testRequest = TextGenerationRequest.builder()
					.model(textGenModel)
					.input(tokenizedInput.getBase64EncodedTokens())
					.parameters(testPreset.toBuilder()
							.useString(false)
							.build())
					.build();
			TextGenerationResponse actualResponse = apiInstance.generateText(testRequest);
			assertNotNull(actualResponse);
			assertNotNull(actualResponse.getOutput());
			assertTrue(actualResponse.getOutput().getTextChunk().length() > 0);
		});
	}

}
