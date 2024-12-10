package aparmar.nai;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import aparmar.nai.data.request.TextGenModel;
import aparmar.nai.data.request.textgen.TextGenerationParameters;
import aparmar.nai.data.request.textgen.TextGenerationRequest;
import aparmar.nai.utils.TextParameterPresets;

class IntegrationTestFetchHiddenState extends AbstractFeatureIntegrationTest {

	@Test
	void testMinimalFetchHiddenState() throws AssertionError, Exception {
		TestHelpers.runTestToleratingTimeouts(3, 1000, ()->{
			String testPresetName = TextParameterPresets.getAssociatedPresets(TextGenModel.EUTERPE)[0];
			TextGenerationParameters testPreset = TextParameterPresets.getPresetByExtendedName(testPresetName);
			TextGenerationRequest testRequest = TextGenerationRequest.builder()
					.model(TextGenModel.EUTERPE)
					.input("This is an API call!\n")
					.parameters(testPreset.toBuilder()
							.useString(true)
							.build())
					.build();
			double[] actualResponse = apiInstance.fetchHiddenStates(testRequest);
			assertNotNull(actualResponse);
			assertTrue(actualResponse.length > 0);
		});
	}

}
