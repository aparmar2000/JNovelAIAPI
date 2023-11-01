package aparmar.nai.data.request;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ImageUpscaleRequest implements JsonSerializer<ImageUpscaleRequest> {
	@RequiredArgsConstructor
	public enum UpscaleFactor implements JsonSerializer<UpscaleFactor>, JsonDeserializer<UpscaleFactor> {
		TWO(2),
		FOUR(4);

		private final int value;
		
		@Override
		public JsonElement serialize(UpscaleFactor src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.value);
		}

		@Override
		public UpscaleFactor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			int val = json.getAsInt();
			
			if (val == 2) {	return TWO;	}
			if (val == 4) { return FOUR; }
			
			throw new JsonParseException("Scale was "+val+" but should only be 2 or 4!");
		}
	}
	
	private Base64Image image;
	@SerializedName("scale")
	private UpscaleFactor upscaleFactor;
	
	@Override
	public JsonElement serialize(ImageUpscaleRequest src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject wrapper = new JsonObject();
		wrapper.add("image", context.serialize(src.getImage(), Base64Image.class));
		wrapper.add("scale", context.serialize(src.getUpscaleFactor(), UpscaleFactor.class));
		wrapper.add("width", new JsonPrimitive(src.getImage().getTargetWidth()));
		wrapper.add("height", new JsonPrimitive(src.getImage().getTargetHeight()));
		
		return wrapper;
	}
}
