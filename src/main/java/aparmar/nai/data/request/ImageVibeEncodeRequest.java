package aparmar.nai.data.request;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import aparmar.nai.utils.AnnotationUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ImageVibeEncodeRequest {
	public static final Set<ImageGenModel> SUPPORTED_MODELS = ImmutableSet.of(ImageGenModel.ANIME_V4_CURATED, ImageGenModel.ANIME_V4_FULL);
	
	private Base64Image image;
	@SerializedName("information_extracted")
	@Builder.Default
	private float informationExtracted = 1;
	@Builder.Default
	private Base64Image mask = null;
	private ImageGenModel model;
	
	public static class ImageVibeEncodeRequestBuilder {
		public ImageVibeEncodeRequestBuilder model(ImageGenModel model) {
			AnnotationUtils.throwOrWarnAboutDepreciation(model, log);
			if (!SUPPORTED_MODELS.contains(model)) {
				throw new IllegalArgumentException(String.format("model %s does not support vibe encoding", model));
			}
			
			this.model = model;
			return this;
		}
		
		public ImageVibeEncodeRequestBuilder informationExtracted(float informationExtracted) {
			if (informationExtracted<0 || informationExtracted>1) {
				throw new IllegalArgumentException(String.format("informationExtracted must be between 0 and 1 inclusive, but was %s", informationExtracted));
			}
			
			this.informationExtracted$value = informationExtracted;
			this.informationExtracted$set = true;
			return this;
		}
		
	}
}
