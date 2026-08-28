package aparmar.nai.utils.serialization;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import aparmar.nai.data.request.imagen.ImageGenModel;
import aparmar.nai.data.request.imagen.ImageGenerationRequest;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenAction;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.QualityTagsLocation;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.QualityTagsPreset;
import lombok.val;
import lombok.var;

public class ImageGenerationRequestSerializer implements JsonSerializer<ImageGenerationRequest> {
	protected static final Pattern TEXT_PROMPT_START_PATTERN = Pattern.compile("[.,]?\\s*text:(?!:)", Pattern.CASE_INSENSITIVE);
	
	@Override
	public JsonElement serialize(ImageGenerationRequest src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject wrapper = new JsonObject();
		
		String alteredInput = src.getInput();
		if (src.getParameters().isQualityToggle()) {
			QualityTagsPreset selectedPreset = src.getParameters().getQualityPreset();
			if (selectedPreset == QualityTagsPreset.DEFAULT || selectedPreset == null) {
				selectedPreset = src.getModel().getQualityTagsPreset();
			}
			
			String qualityTagString = selectedPreset.getTags();
			QualityTagsLocation qualityInsertLocation = src.getParameters().getQualityInsertLocation();
			if (qualityInsertLocation == QualityTagsLocation.DEFAULT) {
				qualityInsertLocation = selectedPreset.getDefaultLocation();
			}
			
			switch (qualityInsertLocation) {
			case DEFAULT:
			case PREPEND:
				alteredInput = qualityTagString+", "+alteredInput;
				break;
			case APPEND:
				alteredInput = alteredInput+", "+qualityTagString;
				break;
			case APPEND_MOVE_TEXT_PROMPT:
				val textPromptMatcher = TEXT_PROMPT_START_PATTERN.matcher(alteredInput);
				var textPrompt = "";
				if (textPromptMatcher.find()) {
					textPrompt = alteredInput.substring(textPromptMatcher.end());
					alteredInput = alteredInput.substring(0, textPromptMatcher.start());
				}
				textPrompt = textPrompt.trim();
				alteredInput = alteredInput+", "+qualityTagString;
				if (!textPrompt.isEmpty()) {
					alteredInput = alteredInput + ". Text: " + textPrompt;
				}
				break;
			}
		}
		if (src.getModeTag() != null) {
			alteredInput = src.getModeTag().addTag(alteredInput);
		}
		
		wrapper.addProperty("input", alteredInput);
		wrapper.add("model", context.serialize(src.getModel(), ImageGenModel.class));
		wrapper.add("action", context.serialize(src.getAction(), ImageGenAction.class));
		JsonObject mergedParameters = context.serialize(src.getParameters(), src.getParameters().getClass()).getAsJsonObject();
		src.getExtraParameters().entrySet().stream()
			.map(e->context.serialize(e.getValue(), e.getKey()))
			.map(o->o.getAsJsonObject().entrySet())
			.flatMap(Set::stream)
			.forEach(m->mergedParameters.add(m.getKey(), m.getValue()));
		wrapper.add("parameters", mergedParameters);
		
		if (src.getModel().hasJsonAdapterFunc()) {
			src.getModel().adaptJson(src, wrapper, context);
		}
		return wrapper;
	}
}
