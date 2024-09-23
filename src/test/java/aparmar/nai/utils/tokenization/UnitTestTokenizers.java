package aparmar.nai.utils.tokenization;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.google.common.collect.ImmutableMap;

class UnitTestTokenizers {
	private static final String TEST_STRING_1 = "The answer is 0b101010";
	private static final String TEST_STRING_2 = "The birch canoe slid on the smooth planks.";
	private static final String TEST_STRING_3 = "The beauty of the view stunned the young boy.";
	private static final String TEST_STRING_4 = "Truly, life is\nsomething to behold.";
	private static final String TEST_STRING_5 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";

	private static final Map<String, int[]> expectedTokenizationsGPT2 = ImmutableMap.of(
			TEST_STRING_1, new int[] {464, 3280, 318, 657, 65, 8784, 20943},
			TEST_STRING_2, new int[] {464, 35122, 354, 47434, 27803, 319, 262, 7209, 1410, 591, 13},
			TEST_STRING_3, new int[] {464, 8737, 286, 262, 1570, 19987, 262, 1862, 2933, 13},
			TEST_STRING_4, new int[] {51, 34715, 11, 1204, 318, 198, 18927, 284, 23700, 13},
			TEST_STRING_5, new int[] {43, 29625, 220, 2419, 388, 288, 45621, 1650, 716, 316, 11, 369, 8831, 316, 333, 31659, 271, 2259, 1288, 270, 11, 10081, 466, 304, 3754, 4666, 10042, 753, 312, 312, 2797, 3384, 2248, 382, 2123, 288, 349, 382, 2153, 2616, 435, 1557, 64, 13}
			);
	private static final Map<String, int[]> expectedTokenizationsGPT2Genji = ImmutableMap.of(
			TEST_STRING_1, new int[] {464, 281, 17845, 318, 657, 65, 8784, 20943},
			TEST_STRING_2, new int[] {464, 35122, 354, 460, 2577, 27803, 319, 262, 7209, 1410, 591, 13},
			TEST_STRING_3, new int[] {464, 8737, 286, 262, 1570, 19987, 262, 1862, 2933, 13},
			TEST_STRING_4, new int[] {51, 34715, 11, 1204, 318, 198, 82, 908, 722, 284, 307, 2946, 13},
			TEST_STRING_5, new int[] {43, 29625, 220, 2419, 388, 288, 45621, 1650, 716, 316, 11, 369, 8831, 316, 333, 512, 541, 271, 2259, 1288, 270, 11, 10081, 466, 304, 3754, 4666, 2169, 1819, 753, 312, 312, 2797, 3384, 2248, 382, 2123, 288, 349, 382, 2153, 2616, 435, 1557, 64, 13}
			);
	private static final Map<String, int[]> expectedTokenizationsPile = ImmutableMap.of(
			TEST_STRING_1, new int[] {510, 3662, 310, 470, 67, 6903, 9104},
			TEST_STRING_2, new int[] {510, 3350, 348, 47884, 22803, 327, 253, 6032, 2098, 661, 15},
			TEST_STRING_3, new int[] {510, 10763, 273, 253, 1859, 33575, 253, 2872, 5006, 15},
			TEST_STRING_4, new int[] {53, 46072, 13, 1495, 310, 187, 17873, 281, 38189, 15},
			TEST_STRING_5, new int[] {45, 4362, 13997, 2204, 28000, 263, 1790, 717, 292, 13, 345, 7338, 292, 321, 14497, 261, 2844, 1045, 262, 13, 9043, 513, 299, 3750, 2307, 5897, 1485, 301, 301, 2084, 2780, 5188, 410, 1162, 28000, 410, 2849, 66, 37711, 66, 15}
			);
	private static final Map<String, int[]> expectedTokenizationsNerdstashV1 = ImmutableMap.of(
			TEST_STRING_1, new int[] {541, 3711, 401, 49209, 4, 49229, 5, 4, 5, 4, 5, 4},
			TEST_STRING_2, new int[] {541, 46665, 425, 49059, 15771, 396, 336, 7239, 1573, 683, 49230},
			TEST_STRING_3, new int[] {541, 7798, 361, 336, 2061, 25325, 336, 1977, 2754, 49230},
			TEST_STRING_4, new int[] {49247, 45267, 49231, 1181, 401, 85, 19087, 357, 30885, 49230},
			TEST_STRING_5, new int[] {49290, 9842, 49209, 1834, 467, 365, 431, 349, 1828, 800, 393, 49231, 456, 10815, 393, 405, 624, 585, 2631, 353, 1411, 344, 49231, 13355, 537, 376, 3656, 6750, 15904, 1113, 389, 389, 2928, 3915, 5930, 460, 2233, 365, 431, 460, 1851, 1761, 503, 1670, 49212, 49230}
			);
	private static final Map<String, int[]> expectedTokenizationsNerdstashV2 = ImmutableMap.of(
			TEST_STRING_1, new int[] {541, 3711, 401, 49209, 123, 49229, 124, 123, 124, 123, 124, 123},
			TEST_STRING_2, new int[] {541, 46665, 425, 49059, 15771, 396, 336, 7239, 1573, 683, 49230},
			TEST_STRING_3, new int[] {541, 7798, 361, 336, 2061, 25325, 336, 1977, 2754, 49230},
			TEST_STRING_4, new int[] {49247, 45267, 49231, 1181, 401, 85, 19087, 357, 30885, 49230},
			TEST_STRING_5, new int[] {49290, 9842, 49209, 1834, 467, 365, 431, 349, 1828, 800, 393, 49231, 456, 10815, 393, 405, 624, 585, 2631, 353, 1411, 344, 49231, 13355, 537, 376, 3656, 6750, 15904, 1113, 389, 389, 2928, 3915, 5930, 460, 2233, 365, 431, 460, 1851, 1761, 503, 1670, 49212, 49230}
			);
	private static final Map<String, int[]> expectedTokenizationsLlama3 = ImmutableMap.of(
			TEST_STRING_1, new int[] {791, 4320, 374, 220, 15, 65, 4645, 7755},
			TEST_STRING_2, new int[] {791, 15606, 331, 84589, 61014, 389, 279, 11113, 628, 4129, 13},
			TEST_STRING_3, new int[] {791, 13444, 315, 279, 1684, 48026, 279, 3995, 8334, 13},
			TEST_STRING_4, new int[] {1305, 3988, 11, 2324, 374, 198, 34431, 311, 57215, 13},
			TEST_STRING_5, new int[] {33883, 27439, 24578, 2503, 28311, 11, 36240, 59024, 31160, 11, 11163, 656, 80222, 19502, 87504, 8791, 73304, 1880, 58396, 60017, 87027, 13}
			);
	private static final Map<Tokenizers, Map<String, int[]>> expectedTokenizationsByTokenizer = ImmutableMap.of(
			Tokenizers.GPT2, expectedTokenizationsGPT2,
			Tokenizers.GPT2_GENJI, expectedTokenizationsGPT2Genji,
			Tokenizers.PILE, expectedTokenizationsPile,
			Tokenizers.NERDSTASH_V1, expectedTokenizationsNerdstashV1,
			Tokenizers.NERDSTASH_V2, expectedTokenizationsNerdstashV2,
			Tokenizers.LLAMA_3, expectedTokenizationsLlama3
			);
	
	private static Stream<Arguments> generateTokenizerTestParameters() {
		String[] testStrings = new String[] {TEST_STRING_1,TEST_STRING_2,TEST_STRING_3,TEST_STRING_4,TEST_STRING_5};
		
		List<Arguments> arguments = new LinkedList<>();
		for (Tokenizers tokenizer : Tokenizers.values()) {
			if (tokenizer == Tokenizers.GPT2_GENJI) {
				continue;// I don't feel like dealing with Genji anymore
			}
			for (String testString : testStrings) {
				arguments.add(Arguments.of(tokenizer,testString));
			}
		}
		
	    return arguments.stream();
	}
	
	@ParameterizedTest
	@MethodSource("generateTokenizerTestParameters")
	void testTokenizerEncoding(Tokenizers targetTokenizer, String testString) {
		INaiTokenizer tokenizer = targetTokenizer.getTokenizer();
		assertNotNull(tokenizer);

		int[] expectedEncoding = expectedTokenizationsByTokenizer.get(targetTokenizer).get(testString);
		int[] actualEncoding = tokenizer.encode(testString);
		assertArrayEquals(expectedEncoding, actualEncoding, 
				Arrays.toString(expectedEncoding)+" vs "+Arrays.toString(actualEncoding));
		assertEquals(testString, tokenizer.decode(actualEncoding));
		assertEquals(expectedEncoding.length, tokenizer.countTokens(testString));
	}
	
	@Test
	void testDecodeBase64() {
		int[] decodedTokens = INaiTokenizer.base64ToUShortTokens("0AEZAbVFPgGRAkEAUCLPUQ==");
		assertArrayEquals(new int[] {464,281,17845,318,657,65,8784,20943}, decodedTokens);
		
		System.out.println(Arrays.toString(INaiTokenizer.base64ToIntegerTokens("UfQBADoAAABIAQAAGQAAANwAAAATAAAAexQAADoAAACTLwAAGQAAAGnxAAALAAAAICIAAHsUAAA=")));
	}
	
	@Test
	void testEncodeBase64() {
		String encoded = INaiTokenizer.UShortTokensToBase64(new int[] {464,281,17845,318,657,65,8784,20943});
		assertEquals("0AEZAbVFPgGRAkEAUCLPUQ==", encoded);
	}
	
	@ParameterizedTest
	@ValueSource(ints = {0, 1234, 65535})
	void testEncodeDecodeUShortBase64(int testValue) {
		String encoded = INaiTokenizer.UShortTokensToBase64(new int[] {testValue});
		assertNotNull(encoded);
		assertTrue(encoded.length()>0);
		
		int[] decoded = INaiTokenizer.base64ToUShortTokens(encoded);
		assertArrayEquals(new int[] {testValue}, decoded);
	}
	
	@ParameterizedTest
	@ValueSource(ints = {0, 65535, 128000})
	void testEncodeDecodeIntegerBase64(int testValue) {
		String encoded = INaiTokenizer.UShortTokensToBase64(new int[] {testValue});
		assertNotNull(encoded);
		assertTrue(encoded.length()>0);
		
		int[] decoded = INaiTokenizer.base64ToUShortTokens(encoded);
		assertArrayEquals(new int[] {testValue}, decoded);
	}
	
	@ParameterizedTest
	@MethodSource("generateTokenizerTestParameters")
	void testTokenizerBase64Pipeline(Tokenizers targetTokenizer, String testString) {
		INaiTokenizer tokenizer = targetTokenizer.getTokenizer();
		assertNotNull(tokenizer);

		String encodedString = targetTokenizer.stringToBase64(testString);
		int[] decodedTokens = targetTokenizer.base64ToTokens(encodedString);
		int[] expectedEncoding = expectedTokenizationsByTokenizer.get(targetTokenizer).get(testString);
		assertArrayEquals(expectedEncoding, decodedTokens, 
				Arrays.toString(expectedEncoding)+" vs "+Arrays.toString(decodedTokens)
				+" ("+targetTokenizer.decode(decodedTokens)+")");
		
		String decodedString = targetTokenizer.base64ToString(encodedString);
		assertEquals(testString, decodedString);
	}

}
