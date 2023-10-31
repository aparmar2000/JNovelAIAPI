package test.java.aparmar.nai;

import static org.junit.Assume.assumeNotNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.aparmar.nai.NAIAPI;
import main.java.aparmar.nai.data.request.TextGenModel;
import main.java.aparmar.nai.data.request.TextGenerationParameters;
import main.java.aparmar.nai.data.request.TextGenerationRequest;
import main.java.aparmar.nai.data.response.TextGenerationResponse;
import main.java.aparmar.nai.utils.TextParameterPresets;

class IntegrationTestTextGeneration {
	private NAIAPI apiInstance;

	@BeforeEach
	public void setUp() throws Exception {
		assumeNotNull(TestConstants.getTestAPIKey());
		apiInstance = new NAIAPI(TestConstants.getTestAPIKey());
	}

	@Test
	void testMinimalTextGeneration() throws IOException {
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
	}

}
