package aparmar.nai.data.request;

import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import lombok.Value;

@Value
public class V4VibeData {
	private final float infoExtracted;
	private final String sourceHash;
	private final ImageGenModel model;
	private final byte[] encoding;
}
