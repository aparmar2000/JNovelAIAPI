package aparmar.nai.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import aparmar.nai.data.request.textgen.TextGenerationParameters;
import aparmar.nai.data.request.textgen.TextGenerationParameters.LogitBias;
import aparmar.nai.data.request.textgen.TextGenerationRequest;
import aparmar.nai.utils.tokenization.Tokenizers;

public class OaiApiAdapters {
	public static JsonObject convertTextGenerationRequest(TextGenerationRequest textGenerationRequest, Gson gson) {
		JsonObject wrapper = new JsonObject();
		Tokenizers tokenizer = textGenerationRequest.getModel().getTokenizerForModel();
		
		wrapper.add("model", gson.toJsonTree( textGenerationRequest.getModel() ));
		if (textGenerationRequest.getParameters().isUseString()) {
			wrapper.add("prompt", gson.toJsonTree( tokenizer.encode(textGenerationRequest.getInput()) ));
		} else {
			wrapper.add("prompt", gson.toJsonTree( tokenizer.base64ToTokens(textGenerationRequest.getInput()) ));
		}
		
		TextGenerationParameters parameters = textGenerationRequest.getParameters();
		wrapper.addProperty("stream", false);
		// Note: logprobs with a value of 0 causes 500 errors with GLM 4.6
		wrapper.add("stop", gson.toJsonTree( parameters.getStopSequences().stream().map(tokenizer::decode).toArray(String[]::new) ));
		JsonObject logitBiasMap = new JsonObject();
		for (int[] badId : parameters.getBadWordIds()) {
			logitBiasMap.addProperty(Integer.toString(badId[0]), -100);
		}
		for (LogitBias logitBias : parameters.getLogitBiases()) {
			logitBiasMap.addProperty(Integer.toString(logitBias.getSequence()[0]), Math.round(logitBias.getBias()));
		}
		wrapper.add("logit_bias", logitBiasMap);
		wrapper.addProperty("temperature", parameters.getTemperature());
		wrapper.addProperty("max_tokens", parameters.getMaxLength());
		wrapper.addProperty("top_k", parameters.getTopK());
		wrapper.addProperty("top_p", parameters.getTopP());
		wrapper.addProperty("min_p", parameters.getMinP());
		wrapper.addProperty("presence_penalty", parameters.getRepetitionPenaltyPresence());
		wrapper.addProperty("frequency_penalty", parameters.getRepetitionPenaltyFrequency());
		wrapper.addProperty("unified_linear", parameters.getUnifiedLinear());
		wrapper.addProperty("unified_increase_linear_with_entropy", 0);
		wrapper.addProperty("unified_cubic", 0);
		wrapper.addProperty("unified_quadratic", parameters.getUnifiedQuad());
		
		return wrapper;
	}
}
