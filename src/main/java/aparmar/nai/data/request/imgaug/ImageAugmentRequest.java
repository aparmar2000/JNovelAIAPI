package aparmar.nai.data.request.imgaug;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import aparmar.nai.data.request.Base64Image;
import kotlin.NotImplementedError;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
public class ImageAugmentRequest {
	@RequiredArgsConstructor
	public enum DefryFactor implements JsonSerializer<DefryFactor>, JsonDeserializer<DefryFactor> {
		ZERO(0),
		ONE(1),
		TWO(2),
		THREE(3),
		FOUR(4),
		FIVE(5);

		private final int value;
		
		@Override
		public JsonElement serialize(DefryFactor src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.value);
		}

		@Override
		public DefryFactor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			int val = json.getAsInt();
			
			switch (val) {
			case 0:
				return ZERO;
			case 1:
				return ONE;
			case 2:
				return TWO;
			case 3:
				return THREE;
			case 4:
				return FOUR;
			case 5:
				return FIVE;
			}
			
			throw new JsonParseException("Scale was "+val+" but should only be between 0 and 5!");
		}
	}
	
	public enum RequestType {
		@SerializedName("bg-removal")
		REMOVE_BACKGROUND,
		@SerializedName("lineart")
		LINE_ART,
		@SerializedName("sketch")
		SKETCH,
		@SerializedName("colorize")
		COLORIZE,
		@SerializedName("emotion")
		EMOTION,
		@SerializedName("declutter")
		DECLUTTER;
	}
	
	protected DefryFactor defry;
	protected String prompt;
	protected int height, width;
	@NonNull
	protected Base64Image image;
	@SerializedName("req_type")
	@NonNull
	protected RequestType requestType;
	
	public int getReturnImgCount() { throw new NotImplementedError("Return img count not specified"); }
}
