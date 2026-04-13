package aparmar.nai.utils.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import aparmar.nai.data.request.textgen.TextGenerationParameters;
import aparmar.nai.data.request.textgen.TextGenerationParameters.SamplerStepId;
import aparmar.nai.data.request.textgen.TextGenerationParameters.SamplingStep;

public class TextGenerationRequestParameterSerializer implements JsonSerializer<TextGenerationParameters>, JsonDeserializer<TextGenerationParameters> {

	@Override
	public JsonElement serialize(TextGenerationParameters src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject serializedParameters = (JsonObject) context.serialize(src);
		
		serializedParameters.remove("order");
		JsonArray orderArr = new JsonArray();
		for (SamplingStep step : src.getOrderSteps()) {
			if (step.isEnabled()) {
				orderArr.add(step.getSampler().getId());
			}
		}
		serializedParameters.add("order", orderArr);

		serializedParameters.remove("bad_words_ids");
		if (src.getBadWordIds() != null && src.getBadWordIds().size() > 0) {
			JsonArray badWordsArray = new JsonArray();
			
			for (int[] badWordIds : src.getBadWordIds()) {
				if (badWordIds != null && badWordIds.length > 0) {
					badWordsArray.add(context.serialize(badWordIds));
				}
			}
			
			if (badWordsArray.size() > 0) {
				serializedParameters.add("bad_words_ids", orderArr);
			}
		}
		
		return serializedParameters;
	}

	@Override
	public TextGenerationParameters deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		if (!json.isJsonObject()) {
			throw new JsonParseException("Expected object but did not find one!");
		}
		JsonObject jsonObj = (JsonObject) json;
		JsonArray orderArr = (JsonArray) jsonObj.get("order");
		jsonObj.remove("order");
		
		TextGenerationParameters result = context.deserialize(jsonObj, typeOfT);
		if (orderArr != null && orderArr.size() > 0) {
			result.setOrderSteps(orderArr.asList()
					.stream()
					.map(JsonElement::getAsInt)
					.map(SamplerStepId::fromId)
					.map(s->new SamplingStep(s, true))
					.collect(Collectors.toCollection(ArrayList::new)));
		}
		
		return result;
	}

}
