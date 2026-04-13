package aparmar.nai.data.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import aparmar.nai.TestHelpers;
import aparmar.nai.data.request.textgen.TextGenerationParameters;
import aparmar.nai.data.request.textgen.TextGenerationParameters.SamplerStepId;
import aparmar.nai.data.request.textgen.TextGenerationParameters.SamplingStep;
import aparmar.nai.data.request.textgen.TextGenerationRequest;
import aparmar.nai.utils.GsonProvider;

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
	
	class SerializationTests {
		Gson gson;
		@BeforeEach
		void setUp() {
			gson = GsonProvider.buildGsonInstance();
		}
		
		@Test
		void testTextGenerationRequestOrderSerialization() {
			TextGenerationRequest testInstance = TextGenerationRequest.builder()
					.input("prompt")
					.model(TextGenModel.CLIO)
					.parameters(TextGenerationParameters.builder()
							.maxLength(1024)
							.minLength(1)
							.temperature(0.2)
							.order(Lists.newArrayList(
									new SamplingStep(SamplerStepId.TEMPERATURE, true),
									new SamplingStep(SamplerStepId.TOP_K, false),
									new SamplingStep(SamplerStepId.TYPICAL, true)
									))
							.build())
					.build();
			
			JsonObject instanceJsonObj = (JsonObject) gson.toJsonTree(testInstance);
			JsonObject parametersJsonObj = instanceJsonObj.getAsJsonObject("parameters");
			assertTrue(parametersJsonObj.has("order"), "Parameters Json lacks 'order' member");
			assertTrue(parametersJsonObj.get("order").isJsonArray(), "Parameters Json 'order' member is not an array");
			JsonArray orderJsonArr = parametersJsonObj.getAsJsonArray("order");
			assertEquals(2, orderJsonArr.size());
			assertEquals(SamplerStepId.TEMPERATURE.getId(), orderJsonArr.get(0).getAsInt());
			assertEquals(SamplerStepId.TYPICAL.getId(), orderJsonArr.get(1).getAsInt());
		}
		
		@Test
		void testTextGenerationRequestEmptyBadWordsIdsSerialization() {
			TextGenerationRequest testInstance = TextGenerationRequest.builder()
					.input("prompt")
					.model(TextGenModel.CLIO)
					.parameters(TextGenerationParameters.builder()
							.maxLength(1024)
							.minLength(1)
							.temperature(0.2)
							.build())
					.build();
			
			JsonObject instanceJsonObj = (JsonObject) gson.toJsonTree(testInstance);
			JsonObject parametersJsonObj = instanceJsonObj.getAsJsonObject("parameters");
			assertFalse(parametersJsonObj.has("bad_words_ids"), "Parameters Json posesses 'bad_words_ids' member");
		}
		
		@Test
		void testTextGenerationRequestNonEmptyBadWordsIdsSerialization() {
			TextGenerationRequest testInstance = TextGenerationRequest.builder()
					.input("prompt")
					.model(TextGenModel.CLIO)
					.parameters(TextGenerationParameters.builder()
							.maxLength(1024)
							.minLength(1)
							.temperature(0.2)
							.badWordIds(Lists.newArrayList(new int[] {15, 6}, new int[] {}))
							.build())
					.build();
			
			JsonObject instanceJsonObj = (JsonObject) gson.toJsonTree(testInstance);
			JsonObject parametersJsonObj = instanceJsonObj.getAsJsonObject("parameters");
			assertTrue(parametersJsonObj.has("bad_words_ids"), "Parameters Json lacks 'bad_words_ids' member");
			assertTrue(parametersJsonObj.get("bad_words_ids").isJsonArray(), "Parameters Json 'bad_words_ids' member is not an array");
			JsonArray badWordsJsonArr = parametersJsonObj.getAsJsonArray("bad_words_ids");
			assertEquals(1, badWordsJsonArr.size());
			assertTrue(badWordsJsonArr.get(0).isJsonArray(), "'bad_words_ids' does not contain arrays");
		}
	}

}
