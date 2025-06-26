package aparmar.nai.data.request;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;

import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import lombok.NonNull;
import lombok.Value;

@Value
public class V4VibeData {
	protected static final ImmutableMap<ImageGenModel, VibeEncodingType> IMAGE_GEN_MODEL_TO_VIBE_ENCODING_TYPE_MAP = new ImmutableMap.Builder<ImageGenModel, VibeEncodingType>()
			.put(ImageGenModel.ANIME_V4_CURATED, VibeEncodingType.V4_CURATED)
			.put(ImageGenModel.ANIME_V4_FULL, VibeEncodingType.V4_FULL)
			.put(ImageGenModel.V4_5_FULL, VibeEncodingType.V4_5_FULL)
			.put(ImageGenModel.ANIME_V4_5_CURATED, VibeEncodingType.V4_5_CURATED)
			.build();
	
	public enum VibeEncodingType {
		@SerializedName("v4full")
		V4_FULL,
		@SerializedName("v4curated")
		V4_CURATED,
		@SerializedName("v4-5full")
		V4_5_FULL,
		@SerializedName("v4-5curated")
		V4_5_CURATED;
		
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
	
	@Nullable
	private final Float infoExtracted;
	@Nullable
	private final String sourceHash;
	@NonNull
	private final VibeEncodingType encodingType;
	@NonNull
	private final byte[] encoding;
}
