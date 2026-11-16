package aparmar.nai.data.request.imagen;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * In order to actually generate transparent images, `transparent_background` or a similar element must be added to the prompt.`
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ImageTransparentParameters extends AbstractExtraImageParameters implements JsonSerializer<ImageTransparentParameters> {

	@Builder.Default
	protected boolean transparentBackground = true;
	@Builder.Default
	protected boolean straightAlpha = true;

	@Override
	public JsonElement serialize(ImageTransparentParameters src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject wrapper = new JsonObject();
		wrapper.add("tag_hint_transparent_background", context.serialize(src.isTransparentBackground(), boolean.class));
		wrapper.add("straight_alpha", context.serialize(src.isStraightAlpha(), boolean.class));
		
		return wrapper;
	}
}
