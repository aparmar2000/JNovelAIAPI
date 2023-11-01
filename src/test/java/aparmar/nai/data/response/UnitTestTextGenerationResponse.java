package aparmar.nai.data.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import aparmar.nai.TestHelpers;
import aparmar.nai.data.response.TextGenerationResponse.LogProb;
import aparmar.nai.data.response.TextGenerationResponse.LogProbStep;
import aparmar.nai.utils.tokenization.TokenizedChunk;
import aparmar.nai.utils.tokenization.Tokenizers;

class UnitTestTextGenerationResponse {

	@Test
	void testTextGenerationResponseDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		TextGenerationResponse testInstance1 = new TextGenerationResponse();
		TextGenerationResponse testInstance2 = new TextGenerationResponse();
		testInstance2.setOutput(new TokenizedChunk(Tokenizers.NERDSTASH_V2, ""));
		testInstance2.setLogprobs(new LogProbStep[0]);
		
		TestHelpers.autoTestDataAndToBuilderAnnotation(TextGenerationResponse.class, testInstance1, testInstance2);
	}
	
	@Test
	void testLogProbStepUtilityFunctions() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		LogProbStep testInstance1 = new LogProbStep();
		LogProbStep testInstance2 = new LogProbStep(
				new LogProb[0],
				new LogProb[] {new LogProb(10, -1.4, -1)},
				new LogProb[] {new LogProb()}
				);

		assertNull(testInstance1.getChosenProb());
		assertNull(testInstance2.getChosenProb());
		TestHelpers.autoTestDataAndToBuilderAnnotation(LogProbStep.class, testInstance1, testInstance2);
		
		LogProbStep singletonTestInstance = LogProbStep.singletonLogProbStep(10);
		assertEquals(new LogProb(10, Math.log(1), Math.log(1)), singletonTestInstance.getChosenProb());
	}
	
	@Test
	void testLogProbUtilityFunctions() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		LogProb testInstance1 = new LogProb();
		LogProb testInstance2 = new LogProb(10, Math.log(0.5), Math.log(0.9));

		TestHelpers.autoTestDataAndToBuilderAnnotation(LogProb.class, testInstance1, testInstance2);
		assertEquals(0.5, testInstance2.getPercentageBefore());
		assertEquals(0.9, testInstance2.getPercentageAfter());
	}
	
	@Test
	void testLogProbDeserialization() {
		LogProb testInstance = new LogProb();
		
		assertNull(testInstance.deserialize(JsonNull.INSTANCE, LogProb.class, null));
		assertThrows(JsonParseException.class, ()->{
			testInstance.deserialize(new JsonPrimitive(false), LogProb.class, null);
			});
		assertThrows(JsonParseException.class, ()->{
			testInstance.deserialize(new JsonArray(), LogProb.class, null);
			});
		assertThrows(JsonParseException.class, ()->{
			JsonArray testElement = new JsonArray(2);
			testElement.add(10);
			testElement.add(0);
			testInstance.deserialize(testElement, LogProb.class, null);
			});
		assertThrows(JsonParseException.class, ()->{
			JsonArray testElement = new JsonArray(2);
			testElement.add(10);
			testElement.add(new JsonArray());
			testInstance.deserialize(testElement, LogProb.class, null);
			});

		JsonArray testElement = new JsonArray(2);
		testElement.add(10);
		JsonArray testProbArray = new JsonArray();
		testProbArray.add(Math.log(0.5));
		testProbArray.add(Math.log(0.9));
		testElement.add(testProbArray);
		assertEquals(
				new LogProb(10, Math.log(0.5), Math.log(0.9)), 
				testInstance.deserialize(testElement, LogProb.class, null));
	}

}
