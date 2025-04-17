package aparmar.nai.data.request;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;

import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import lombok.Value;

@Value
public class V4VibeData {
	public enum VibeEncodingType {
		@SerializedName("v4full")
		V4_FULL,
		@SerializedName("v4curated")
		V4_CURATED;
		
		public static final ImmutableMap<ImageGenModel, VibeEncodingType> IMAGE_GEN_MODEL_TO_VIBE_ENCODING_TYPE_MAP = new ImmutableMap.Builder<ImageGenModel, VibeEncodingType>()
				.put(ImageGenModel.ANIME_V4_CURATED, V4_CURATED)
				.put(ImageGenModel.ANIME_V4_FULL, V4_FULL)
				.build();
		
		public ImageGenModel getAModelForEncodingType() {
			return getAModelForEncodingType(this);
		}
		
		@Nullable
		public static VibeEncodingType getEncodingTypeForModel(ImageGenModel model) {
			return IMAGE_GEN_MODEL_TO_VIBE_ENCODING_TYPE_MAP.get(model);
		}
		@Nullable
		public static ImageGenModel getAModelForEncodingType(VibeEncodingType encodingType) {
			return IMAGE_GEN_MODEL_TO_VIBE_ENCODING_TYPE_MAP.entrySet()
					.stream()
					.filter(e->e.getValue()==encodingType)
					.findAny()
					.map(Map.Entry::getKey)
					.orElse(null);
		}
	}
	
	private final float infoExtracted;
	private final String sourceHash;
	private final VibeEncodingType encodingType;
	private final byte[] encoding;
}
