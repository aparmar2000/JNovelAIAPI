package aparmar.nai.data.request;

import com.google.common.collect.ImmutableBiMap;

import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import lombok.Value;

@Value
public class V4VibeData {
	public static final ImmutableBiMap<ImageGenModel, String> IMAGE_GEN_MODEL_NAME_MAP = new ImmutableBiMap.Builder<ImageGenModel, String>()
			.put(ImageGenModel.ANIME_V4_CURATED, "v4curated")
			.put(ImageGenModel.ANIME_V4_FULL, "v4full")
			.build();
	
	private final float infoExtracted;
	private final String sourceHash;
	private final ImageGenModel model;
	private final byte[] encoding;
}
