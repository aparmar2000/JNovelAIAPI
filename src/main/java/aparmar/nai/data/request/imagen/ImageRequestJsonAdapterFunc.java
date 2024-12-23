package aparmar.nai.data.request.imagen;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;

public interface ImageRequestJsonAdapterFunc {
	public JsonElement adapt(ImageGenerationRequest request, JsonElement currentJson, JsonSerializationContext context);
}
