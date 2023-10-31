package test.java.aparmar.nai.utils.tokenization;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

import main.java.aparmar.nai.utils.tokenization.TokenizedChunk;
import main.java.aparmar.nai.utils.tokenization.Tokenizers;
import test.java.aparmar.nai.TestHelpers;

public class UnitTestTokenizedChunk {

	@Test
	void testTokenizedChunkUtilityMethods() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		TokenizedChunk testInstance1 = new TokenizedChunk(Tokenizers.GPT2, "Test");
		TokenizedChunk testInstance2 = new TokenizedChunk(Tokenizers.NERDSTASH_V1, "This is a test.");
		TestHelpers.autoTestDataAndToBuilderAnnotation(TokenizedChunk.class, testInstance1, testInstance2);
	}
}
