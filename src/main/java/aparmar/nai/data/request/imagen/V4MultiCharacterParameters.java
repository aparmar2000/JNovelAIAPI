package aparmar.nai.data.request.imagen;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class V4MultiCharacterParameters extends MultiCharacterParameters implements JsonSerializer<V4MultiCharacterParameters> {
	@Builder.Default
	@SerializedName("use_order")
	protected boolean useOrder = true;
	
	@Override
	public JsonElement serialize(V4MultiCharacterParameters src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject wrapper = new JsonObject();
		
		JsonObject negWrapper = new JsonObject();
		JsonObject negCaptionsObj = new JsonObject();
		List<JsonObject> negCaptionsList = new LinkedList<>();
		for (CharacterPrompt characterPrompt : src.getCharacterPrompts()) {
			JsonObject serializedPrompt = new JsonObject();
			
			JsonObject centerObj = new JsonObject();
			centerObj.addProperty("x", characterPrompt.getCenterX());
			centerObj.addProperty("y", characterPrompt.getCenterY());
			serializedPrompt.add("centers", context.serialize(Collections.singletonList(centerObj)));
			serializedPrompt.addProperty("char_caption", characterPrompt.getUndesiredContent());
			
			negCaptionsList.add(serializedPrompt);
		}
		negCaptionsObj.add("char_captions", context.serialize(negCaptionsList));
		negWrapper.add("caption", negCaptionsObj);
		
		JsonObject posWrapper = new JsonObject();
		JsonObject posCaptionsObj = new JsonObject();
		List<JsonObject> posCaptionsList = new LinkedList<>();
		for (CharacterPrompt characterPrompt : src.getCharacterPrompts()) {
			JsonObject serializedPrompt = new JsonObject();
			
			JsonObject centerObj = new JsonObject();
			centerObj.addProperty("x", characterPrompt.getCenterX());
			centerObj.addProperty("y", characterPrompt.getCenterY());
			serializedPrompt.add("centers", context.serialize(Collections.singletonList(centerObj)));
			serializedPrompt.addProperty("char_caption", characterPrompt.getPrompt());
			
			posCaptionsList.add(serializedPrompt);
		}
		posCaptionsObj.add("char_captions", context.serialize(posCaptionsList));
		posWrapper.add("caption", posCaptionsObj);
		posWrapper.addProperty("use_coords", src.isUseCoords());
		posWrapper.addProperty("use_order", src.isUseOrder());
		
		wrapper.add("characterPrompts", context.serialize(src.getCharacterPrompts()));
		wrapper.addProperty("use_coords", src.isUseCoords());
		wrapper.add("v4_negative_prompt", negWrapper);
		wrapper.add("v4_prompt", posWrapper);
		
		return wrapper;
	}
}
