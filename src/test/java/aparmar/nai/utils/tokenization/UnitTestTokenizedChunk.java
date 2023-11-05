package aparmar.nai.utils.tokenization;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;

import aparmar.nai.TestHelpers;
import aparmar.nai.data.request.TextGenModel;
import aparmar.nai.utils.tokenization.TokenizedChunk.TokenizedChunkSnapshot;

public class UnitTestTokenizedChunk {

	@Test
	void testTokenizedChunkUtilityMethods() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		TokenizedChunk testInstance1 = new TokenizedChunk(Tokenizers.GPT2, "Test");
		TokenizedChunk testInstance2 = new TokenizedChunk(Tokenizers.NERDSTASH_V1, "This is a test.");

		assertEquals(testInstance1, testInstance1.clone());
		assertFalse(testInstance1 == testInstance1.clone());
		
		TestHelpers.autoTestDataAndToBuilderAnnotation(TokenizedChunk.class, testInstance1, testInstance2);
		assertTrue(testInstance1.hasContent());
	}
	
	private static final String TEST_STRING_1A = "This is a test sentence.";
	private static final String TEST_STRING_1B = " And this is another.";
	private static final String TEST_STRING_1 = TEST_STRING_1A+TEST_STRING_1B;
	private static final int[] TEST_TOKENS_1A = new int[]{1374, 401, 333, 2021, 11927, 49230};
	private static final int[] TEST_TOKENS_1B = new int[]{1043, 510, 401, 1342, 49230};
	private static final int[] TEST_TOKENS_1 = new int[]{1374, 401, 333, 2021, 11927, 49230, 1043, 510, 401, 1342, 49230};
	private static final String TEST_STRING_2A = "This is a split sent";
	private static final String TEST_STRING_2B = "ence that is harder to merge.";
	private static final String TEST_STRING_2 = TEST_STRING_2A+TEST_STRING_2B;
	private static final int[] TEST_TOKENS_2A = new int[]{1374, 401, 333, 9019, 2376};
	private static final int[] TEST_TOKENS_2B = new int[]{727, 411, 401, 10078, 357, 36380, 49230};
	private static final int[] TEST_TOKENS_2 = new int[]{1374, 401, 333, 9019, 11927, 411, 401, 10078, 357, 36380, 49230};
	

	@Test
	void testHelperConstructors() {
		TokenizedChunk testChunk1 = new TokenizedChunk(TextGenModel.KAYRA, TEST_STRING_1);
		TokenizedChunk testChunk2 = new TokenizedChunk(TextGenModel.KAYRA.getTokenizerForModel(), TEST_STRING_1);
		assertEquals(testChunk1, testChunk2);
		
		testChunk1 = new TokenizedChunk(TextGenModel.KAYRA, TEST_TOKENS_1);
		testChunk2 = new TokenizedChunk(TextGenModel.KAYRA.getTokenizerForModel(), TEST_TOKENS_1);
		assertEquals(testChunk1, testChunk2);
	}
	
	@Test
	void testBase64Conversion() {
		TokenizedChunk testChunkOriginal = new TokenizedChunk(Tokenizers.NERDSTASH_V2, TEST_STRING_1);
		TokenizedChunk testChunkResult = TokenizedChunk.fromBase64Tokens(Tokenizers.NERDSTASH_V2, testChunkOriginal.getBase64EncodedTokens());
		
		assertEquals(testChunkOriginal, testChunkResult);
		assertEquals(TEST_STRING_1, testChunkResult.getTextChunk());
		assertArrayEquals(TEST_TOKENS_1, testChunkResult.getTokens());
	}
	
	@Test
	void testTokenization() {
		TokenizedChunk testChunk = new TokenizedChunk(Tokenizers.NERDSTASH_V2, "");
		assertArrayEquals(new int[0], testChunk.getTokens());
		assertFalse(testChunk.hasContent());
		
		testChunk.setTextChunk(TEST_STRING_1A);
		assertArrayEquals(TEST_TOKENS_1A, testChunk.getTokens());
		
		testChunk.appendString(TEST_STRING_1B);
		assertEquals(TEST_TOKENS_1.length, testChunk.tokenLength());
		assertArrayEquals(TEST_TOKENS_1, testChunk.getTokens());
		
		testChunk.setTextChunk("");
		assertArrayEquals(new int[0], testChunk.getTokens());
		assertFalse(testChunk.hasContent());
	}
	
	@Test
	void testTokenParsing() {
		TokenizedChunk testChunk = new TokenizedChunk(Tokenizers.NERDSTASH_V2, new int[0]);
		assertEquals("", testChunk.getTextChunk());
		assertFalse(testChunk.hasContent());
		
		testChunk.setTokens(TEST_TOKENS_1A);
		assertEquals(TEST_STRING_1A, testChunk.getTextChunk());
		
		testChunk.appendTokens(TEST_TOKENS_1B);
		assertEquals(TEST_STRING_1, testChunk.getTextChunk());

		testChunk.setTokens(new int[0]);
		assertEquals("", testChunk.getTextChunk());
		assertFalse(testChunk.hasContent());
	}
	
	@Test
	void testDissimilarChunkMerging() {
		TokenizedChunk testChunk1 = new TokenizedChunk(Tokenizers.NERDSTASH_V2, TEST_STRING_2A);
		TokenizedChunk testChunk2 = new TokenizedChunk(Tokenizers.GPT2, TEST_STRING_2B);
		
		TokenizedChunk testMergedChunk = TokenizedChunk.mergeTokenizedChunks(Tokenizers.NERDSTASH_V2, testChunk1, testChunk2);
		assertEquals(TEST_STRING_2, testMergedChunk.getTextChunk());
		assertArrayEquals(TEST_TOKENS_2, testMergedChunk.getTokens());
		
		TokenizedChunk testMultiAppendChunk = new TokenizedChunk(Tokenizers.NERDSTASH_V2, "");
		testMultiAppendChunk.appendTokenizedChunks(testChunk1, testChunk2);
		assertEquals(TEST_STRING_2, testMultiAppendChunk.getTextChunk());
		assertArrayEquals(TEST_TOKENS_2, testMultiAppendChunk.getTokens());
		
		testChunk1.appendTokenizedChunk(testChunk2);
		assertEquals(TEST_STRING_2, testChunk1.getTextChunk());
		assertArrayEquals(TEST_TOKENS_2, testChunk1.getTokens());
	}
	
	@Test
	void testSimilarChunkMerging() {
		TokenizedChunk testChunk1 = new TokenizedChunk(Tokenizers.NERDSTASH_V2, TEST_STRING_2A);
		TokenizedChunk testChunk2 = new TokenizedChunk(Tokenizers.NERDSTASH_V2, TEST_STRING_2B);
		assertArrayEquals(TEST_TOKENS_2A, testChunk1.getTokens());
		assertArrayEquals(TEST_TOKENS_2B, testChunk2.getTokens());
		
		TokenizedChunk testMergedChunk = TokenizedChunk.mergeTokenizedChunks(Tokenizers.NERDSTASH_V2, testChunk1, testChunk2);
		assertEquals(TEST_STRING_2, testMergedChunk.getTextChunk());
		assertArrayEquals(TEST_TOKENS_2, testMergedChunk.getTokens());
		
		TokenizedChunk testMultiAppendChunk = new TokenizedChunk(Tokenizers.NERDSTASH_V2, "");
		testMultiAppendChunk.appendTokenizedChunks(testChunk1, testChunk2);
		assertEquals(TEST_STRING_2, testMultiAppendChunk.getTextChunk());
		assertArrayEquals(TEST_TOKENS_2, testMultiAppendChunk.getTokens());
		
		testChunk1.appendTokenizedChunk(testChunk2);
		assertEquals(TEST_STRING_2, testChunk1.getTextChunk());
		assertArrayEquals(TEST_TOKENS_2, testChunk1.getTokens());
	}
	
	@Test
	void testChunkSnapshot() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		TokenizedChunk testChunk = new TokenizedChunk(Tokenizers.NERDSTASH_V2, TEST_STRING_1);
		TokenizedChunkSnapshot testSnapshot = testChunk.getSnapshot();
		
		assertEquals(testChunk.getTokenizer(), testSnapshot.getTokenizer());
		assertEquals(testChunk.getTextChunk(), testSnapshot.getText());
		assertArrayEquals(testChunk.getTokens(), testSnapshot.getTokens());
		assertEquals(testChunk.getSnapshot(), testSnapshot);
		
		testChunk.setTextChunk(TEST_STRING_2);
		assertNotEquals(testChunk.getSnapshot(), testSnapshot);
		
		testChunk.setTokenizer(Tokenizers.GPT2);
		TestHelpers.autoTestDataAndToBuilderAnnotation(TokenizedChunkSnapshot.class, testSnapshot, testChunk.getSnapshot());
	}
	
	@Test
	void testChunkSnapshotThreadSafety() {
		TokenizedChunk sharedTestChunk = new TokenizedChunk(Tokenizers.NERDSTASH_V2, TEST_STRING_1);
		
		Thread writer = new Thread(()->{
			long startTime = System.currentTimeMillis();
			int option = 2;
			while (System.currentTimeMillis()-startTime<1000) {
				switch(option) {
				case 1:
					sharedTestChunk.setTextChunk(TEST_STRING_1);
					option = 2;
					continue;
				case 2:
					sharedTestChunk.setTextChunk(TEST_STRING_2);
					option = 1;
					continue;
				}
			}
		});
		
		LinkedList<TokenizedChunk.TokenizedChunkSnapshot> samplePairs = new LinkedList<>();
		writer.start();
		while (writer.isAlive()) {
			samplePairs.add(sharedTestChunk.getSnapshot());
		}
		
		for (TokenizedChunk.TokenizedChunkSnapshot samplePair : samplePairs) {
			switch (samplePair.getText()) {
			case TEST_STRING_1:
				assertArrayEquals(TEST_TOKENS_1, samplePair.getTokens(), "String and tokens of TokenizedChunk do not match!");
				break;
			case TEST_STRING_2:
				assertArrayEquals(TEST_TOKENS_2, samplePair.getTokens(), "String and tokens of TokenizedChunk do not match!");
				break;
			default:
				fail("String value of TokenizedChunk was invalid!");
				break;
			}
		}
	}
}
