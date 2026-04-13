package aparmar.nai.utils.serialization;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import aparmar.nai.data.request.textgen.TextGenerationParameters.SamplerStepId;
import aparmar.nai.data.request.textgen.TextGenerationParameters.SamplingStep;
import lombok.val;

public class SamplingStepDeserializer implements JsonDeserializer<SamplingStep> {

	@Override
	public SamplingStep deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		if (json.isJsonPrimitive()) {
			int samplerId = json.getAsInt();
			try {
				return new SamplingStep(SamplerStepId.fromId(samplerId), true);
			} catch (IllegalArgumentException e) {
				throw new JsonParseException(e);
			}
		}
		
		if (!json.isJsonObject()) {
			throw new JsonParseException("Expected either an int or an object but was neither!");
		}
		val jsonObj = json.getAsJsonObject();
		SamplerStepId sampler = context.deserialize(jsonObj.get("id"), SamplerStepId.class);
		boolean enabled = context.deserialize(jsonObj.get("enabled"), Boolean.class);
		return new SamplingStep(sampler, enabled);
	}

}
