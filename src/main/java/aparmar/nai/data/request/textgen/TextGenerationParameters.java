package aparmar.nai.data.request.textgen;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.annotations.SerializedName;

import aparmar.nai.data.request.PresetModulePrefixes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class TextGenerationParameters { // TODO: Add more validation
	
	public enum PhraseRepPenSetting {
		@SerializedName("off")
		OFF,
		@SerializedName("very_light")
		VERY_LIGHT,
		@SerializedName("light")
		LIGHT,
		@SerializedName("medium")
		MEDIUM,
		@SerializedName("aggressive")
		AGGRESSIVE,
		@SerializedName("very_aggressive")
		VERY_AGGRESSIVE;
	}
	
	@EqualsAndHashCode.Include(replaces="stopSequences")
	private List<List<Integer>> stopSequenceGetterForEquals() {
		return stopSequences.stream()
				.map(arr->Arrays.stream(arr).boxed().collect(Collectors.toList()))
				.collect(Collectors.toList());
	}
	@EqualsAndHashCode.Include(replaces="badWordIds")
	private List<List<Integer>> badWordIdsGetterForEquals() {
		return badWordIds.stream()
				.map(arr->Arrays.stream(arr).boxed().collect(Collectors.toList()))
				.collect(Collectors.toList());
	}
	
	@SerializedName("stop_sequences")
	private List<int[]> stopSequences = new LinkedList<int[]>(); // TODO: Maybe make token ids an object?
	@SerializedName("bad_words_ids")
	private List<int[]> badWordIds = new LinkedList<int[]>();
	
	@SerializedName("logit_bias_exp")
	private List<LogitBias> logitBiases = new LinkedList<LogitBias>();
	private int[] order; // TODO: Make this an object with custom serialization & validation?
	@SerializedName("repetition_penalty_whitelist")
	private int[] repetitionPenaltyWhitelist;

	private double temperature;
	@SerializedName("max_length")
	private int maxLength;
	@SerializedName("min_length")
	private int minLength;
	@SerializedName("num_logprobs")
	private int numLogprobs;

	@SerializedName("use_string")
	private boolean useString;
	@SerializedName("use_cache")
	private boolean useCache;
	@SerializedName("early_stopping")
	private boolean earlyStopping; // TODO: What is this?
	@SerializedName("next_word")
	private boolean nextWord; // TODO: What is this?
	@SerializedName("get_hidden_states")
	private boolean getHiddenStates = false;
	@SerializedName("output_nonzero_probs")
	private boolean outputNonzeroProbs; // TODO: What is this?
	@SerializedName("generate_until_sentence")
	private boolean generateUntilSentence;
	
	@SerializedName("num_beams")
	private int beamNumber; // TODO: What is this?
	@SerializedName("num_beam_groups")
	private int beamGroupNumber; // TODO: What is this?
	@SerializedName("cfg_alpha")
	private double cfgAlpha;
	
	@SerializedName("top_k")
	private int topK;
	@SerializedName("top_a")
	private double topA;
	@SerializedName("top_p")
	private double topP;
	@SerializedName("top_g")
	private double topG;
	@SerializedName("typical_p")
	private double typicalP;
	@SerializedName("min_p")
	private double minP;
	@SerializedName("tail_free_sampling")
	private double tailFreeSampling;

	@SerializedName("math1_temp")
	private double unifiedLinear;
	@SerializedName("math1_quad")
	private double unifiedQuad;
	@SerializedName("math1_quad_entropy_scale")
	private double unifiedConf;
	
	@SerializedName("repetition_penalty")
	private double repetitionPenalty;
	@SerializedName("repetition_penalty_range")
	private int repetitionPenaltyRange;
	@SerializedName("repetition_penalty_slope")
	private double repetitionPenaltySlope;
	@SerializedName("repetition_penalty_frequency")
	private double repetitionPenaltyFrequency;
	@SerializedName("repetition_penalty_presence")
	private double repetitionPenaltyPresence;
	@SerializedName("phrase_rep_pen")
	private TextGenerationParameters.PhraseRepPenSetting phraseRepetitionPenalty;
	
	@SerializedName("pad_token_id")
	private int padTokenId;
	@SerializedName("bos_token_id")
	private int bosTokenId;
	@SerializedName("eos_token_id")
	private int eosTokenId;
	@SerializedName("length_penalty")
	private double lengthPenalty; // TODO: What is this?
	@SerializedName("diversity_penalty")
	private double diversityPenalty; // TODO: What is this?
	@SerializedName("no_repeat_ngram_size")
	private int noRepeatNgramSize; // TODO: What is this?
	@SerializedName("encoder_no_repeat_ngram_size")
	private int encoderNoRepeatNgramSize; // TODO: What is this?
	@SerializedName("num_return_sequences")
	private int numReturnSequences; // TODO: What is this?
	@SerializedName("max_time")
	private double maxTime;
	@SerializedName("mirostat_tau")
	private double mirostatTau;
	@SerializedName("mirostat_lr")
	private double mirostatLr;
	
	@SerializedName("prefix")
	private String modulePrefix;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder(toBuilder = true)
	public static class LogitBias {
		private int[] sequence;
		private double bias;
		@SerializedName("ensure_sequence_finish")
		private boolean ensureSequenceFinishes;
		@SerializedName("generate_once")
		private boolean unbiasOnceGenerated;
	}
	
	public void setMaxLength(int newMaxLength) {
		if (newMaxLength<=0 || newMaxLength>2048) {
			throw new IllegalArgumentException("Max length must be within 1-2048, was "+this.maxLength);
		}
		if (newMaxLength<this.minLength) {
			throw new IllegalArgumentException("Max length must be >= min length ("+this.minLength+"), was "+this.maxLength);
		}
		this.maxLength = newMaxLength;
	}
	
	public void setMinLength(int newMinLength) {
		if (newMinLength<=0 || newMinLength>2048) {
			throw new IllegalArgumentException("Min length must be within 1-2048, was "+newMinLength);
		}
		if (newMinLength>this.maxLength) {
			throw new IllegalArgumentException("Min length must be <= max length ("+this.maxLength+"), was "+this.minLength);
		}
		this.minLength = newMinLength;
	}
	
	public void setTemperature(double newTemperature) {
		if (newTemperature<0.1 || newTemperature>100) {
			throw new IllegalArgumentException("Temperature must be within 0.1-100, was "+newTemperature);
		}
		this.temperature = newTemperature;
	}
	
	public void setModulePrefix(String newPrefix) {
		if (newPrefix == null || newPrefix.isEmpty()) {
			this.modulePrefix = PresetModulePrefixes.NO_MODULE.getPrefix();
		} else {
			this.modulePrefix = newPrefix;
		}
	}

	@Builder(toBuilder = true)
	public TextGenerationParameters(List<int[]> stopSequences, List<int[]> badWordIds, List<LogitBias> logitBiases, int[] order,
			int[] repetitionPenaltyWhitelist, double temperature, int maxLength, int minLength, int numLogprobs, boolean useString,
			boolean useCache, boolean earlyStopping, boolean nextWord, boolean getHiddenStates,
			boolean outputNonzeroProbs, boolean generateUntilSentence, int beamNumber, int beamGroupNumber,
			double cfgAlpha, int topK, double topA, double topP, double topG, double typicalP, double minP,
			double tailFreeSampling, double unifiedLinear, double unifiedQuad, double unifiedConf,
			double repetitionPenalty, int repetitionPenaltyRange, double repetitionPenaltySlope, 
			double repetitionPenaltyFrequency, double repetitionPenaltyPresence,
			TextGenerationParameters.PhraseRepPenSetting phraseRepetitionPenalty, int padTokenId, int bosTokenId, int eosTokenId,
			double lengthPenalty, double diversityPenalty, int noRepeatNgramSize, int encoderNoRepeatNgramSize,
			int numReturnSequences, double maxTime, double mirostatTau, double mirostatLr, String modulePrefix) {
		this.stopSequences = stopSequences==null?new LinkedList<int[]>():stopSequences;
		this.badWordIds = badWordIds==null?new LinkedList<int[]>():badWordIds;
		this.logitBiases = logitBiases==null?new LinkedList<LogitBias>():logitBiases;
		this.order = order;
		this.repetitionPenaltyWhitelist = repetitionPenaltyWhitelist;
		setTemperature(temperature);
		setMaxLength(maxLength);
		setMinLength(minLength);
		this.numLogprobs = numLogprobs;
		this.useString = useString;
		this.useCache = useCache;
		this.earlyStopping = earlyStopping;
		this.nextWord = nextWord;
		this.getHiddenStates = getHiddenStates;
		this.outputNonzeroProbs = outputNonzeroProbs;
		this.generateUntilSentence = generateUntilSentence;
		this.beamNumber = beamNumber;
		this.beamGroupNumber = beamGroupNumber;
		this.cfgAlpha = cfgAlpha;
		this.topK = topK;
		this.topA = topA;
		this.topP = topP;
		this.topG = topG;
		this.typicalP = typicalP;
		this.minP = minP;
		this.tailFreeSampling = tailFreeSampling;
		this.unifiedLinear = unifiedLinear;
		this.unifiedQuad = unifiedQuad;
		this.unifiedConf = unifiedConf;
		this.repetitionPenalty = repetitionPenalty;
		this.repetitionPenaltyRange = repetitionPenaltyRange;
		this.repetitionPenaltySlope = repetitionPenaltySlope;
		this.repetitionPenaltyFrequency = repetitionPenaltyFrequency;
		this.repetitionPenaltyPresence = repetitionPenaltyPresence;
		this.phraseRepetitionPenalty = phraseRepetitionPenalty;
		this.padTokenId = padTokenId;
		this.bosTokenId = bosTokenId;
		this.eosTokenId = eosTokenId;
		this.lengthPenalty = lengthPenalty;
		this.diversityPenalty = diversityPenalty;
		this.noRepeatNgramSize = noRepeatNgramSize;
		this.encoderNoRepeatNgramSize = encoderNoRepeatNgramSize;
		this.numReturnSequences = numReturnSequences;
		this.maxTime = maxTime;
		this.mirostatTau = mirostatTau;
		this.mirostatLr = mirostatLr;
		setModulePrefix(modulePrefix);
	}
}