package aparmar.nai.data.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import aparmar.nai.TestHelpers;
import aparmar.nai.data.request.textgen.TextGenerationParameters;
import aparmar.nai.data.request.textgen.TextGenerationParameters.LogitBias;
import aparmar.nai.data.request.textgen.TextGenerationParameters.PhraseRepPenSetting;

class UnitTestTextGenerationParameters {

	@Test
	void testTextGenerationParametersDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		TextGenerationParameters testInstance1 = TextGenerationParameters.builder()
				.minLength(1)
				.maxLength(160)
				.temperature(0.2)
				.build();
		TextGenerationParameters testInstance2 = TextGenerationParameters.builder()
				.minLength(5)
				.maxLength(200)
				.temperature(0.3)
				.stopSequences(Stream.of(new int[] {1}).collect(Collectors.toList()))
				.badWordIds(Stream.of(new int[] {2}).collect(Collectors.toList()))
				.logitBiases(Stream.of(new LogitBias[] {LogitBias.builder().build()}).collect(Collectors.toList()))
				.numLogprobs(10)
				.order(new int[] {1,2,3})
				.repetitionPenaltyWhitelist(new int[] {5})
				.useString(true)
				.useCache(true)
				.earlyStopping(true)
				.nextWord(true)
				.getHiddenStates(true)
				.outputNonzeroProbs(true)
				.generateUntilSentence(true)
				.beamNumber(2)
				.beamGroupNumber(3)
				.cfgAlpha(2.5)
				.topK(3)
				.topA(4.5)
				.topP(5.5)
				.topG(6.5)
				.typicalP(7.5)
				.minP(0.7)
				.tailFreeSampling(8.5)
				.unifiedLinear(1.5)
				.unifiedQuad(0.2)
				.unifiedConf(0.15)
				.repetitionPenalty(9.5)
				.repetitionPenaltyRange(10)
				.repetitionPenaltySlope(1.25)
				.repetitionPenaltyFrequency(2.25)
				.repetitionPenaltyPresence(3.25)
				.phraseRepetitionPenalty(PhraseRepPenSetting.VERY_AGGRESSIVE)
				.padTokenId(111)
				.bosTokenId(222)
				.eosTokenId(333)
				.lengthPenalty(1.1)
				.diversityPenalty(2.1)
				.noRepeatNgramSize(19)
				.encoderNoRepeatNgramSize(61)
				.numReturnSequences(14)
				.maxTime(3.1)
				.mirostatTau(4.1)
				.mirostatLr(5.1)
				.modulePrefix(PresetModulePrefixes.AUTHOR_H_P_LOVECRAFT.getPrefix())
				.build();
		TestHelpers.autoTestDataAndToBuilderAnnotation(TextGenerationParameters.class, testInstance1, testInstance2);
	}

	@Test
	void testLogitBiasDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		LogitBias testInstance1 = LogitBias.builder()
				.build();
		LogitBias testInstance2 = LogitBias.builder()
				.sequence(new int[] {1,2,3})
				.bias(1.5)
				.ensureSequenceFinishes(true)
				.unbiasOnceGenerated(true)
				.build();
		TestHelpers.autoTestDataAndToBuilderAnnotation(LogitBias.class, testInstance1, testInstance2);
	}

	@Test
	void testTextGenerationParametersFailsValidationWhenEmpty() {
		assertThrows(IllegalArgumentException.class, ()->TextGenerationParameters.builder().build());
	}
	
	@ParameterizedTest
	@ValueSource(ints = {-1,2049})
	void testTextGenerationParametersFailsValidationWhenIllegalMinLength(int testLength) {
		assertThrows(IllegalArgumentException.class, ()->TextGenerationParameters.builder()
				.minLength(testLength)
				.build());
	}
	
	@ParameterizedTest
	@ValueSource(ints = {-1, 24,2049})
	void testTextGenerationParametersFailsValidationWhenIllegalMaxLength(int testLength) {
		assertThrows(IllegalArgumentException.class, ()->TextGenerationParameters.builder()
				.minLength(25)
				.maxLength(testLength)
				.build());
	}
	
	@ParameterizedTest
	@ValueSource(doubles = {0, 999})
	void testTextGenerationParametersFailsValidationWhenIllegalTemperature(double testTemperature) {
		assertThrows(IllegalArgumentException.class, ()->TextGenerationParameters.builder()
				.minLength(1)
				.maxLength(160)
				.temperature(testTemperature)
				.build());
	}
	
	@Test
	void testTextGenerationParametersSucceedsValidationWhenValid() {
		assertNotNull(TextGenerationParameters.builder()
				.minLength(1)
				.maxLength(160)
				.temperature(0.2)
				.modulePrefix(PresetModulePrefixes.NO_MODULE.getPrefix())
				.build());
	}
	
	@ParameterizedTest
	@NullAndEmptySource
	void testTextGenerationParametersSetsDefaultModule(String testModule) {
		TextGenerationParameters actualParameters = TextGenerationParameters.builder()
			.minLength(1)
			.maxLength(160)
			.temperature(0.2)
			.modulePrefix(testModule)
			.build();
		
		assertNotNull(actualParameters);
		assertEquals(PresetModulePrefixes.NO_MODULE.getPrefix(), actualParameters.getModulePrefix());
	}

}
