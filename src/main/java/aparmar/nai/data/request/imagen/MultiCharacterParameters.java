package aparmar.nai.data.request.imagen;

import java.lang.reflect.Type;
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
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class MultiCharacterParameters extends AbstractExtraImageParameters {
	@Builder.Default
	@SerializedName("use_coords")
	protected boolean useCoords = false;
	
	@Singular
	protected List<CharacterPrompt> characterPrompts;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder(toBuilder = true)
	public static class CharacterPrompt implements JsonSerializer<CharacterPrompt> {
		@Builder.Default
		protected float centerX = 0.5f;
		@Builder.Default
		protected float centerY = 0.5f;
		@Builder.Default
		protected String prompt = "";
		@SerializedName("uc")
		@Builder.Default
		protected String undesiredContent = "";
		
		@Override
		public JsonElement serialize(CharacterPrompt src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject wrapper = new JsonObject();
			
			JsonObject centerObj = new JsonObject();
			centerObj.addProperty("x", src.getCenterX());
			centerObj.addProperty("y", src.getCenterY());
			wrapper.add("center", centerObj);
			wrapper.addProperty("prompt", src.getPrompt());
			wrapper.addProperty("uc", src.getUndesiredContent());
			
			return wrapper;
		}
	}
}
