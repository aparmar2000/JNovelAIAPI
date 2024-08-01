package aparmar.nai.data.request.imgaug;

import aparmar.nai.data.request.Base64Image;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImageAugmentColorizeRequest extends ImageAugmentRequestSingleResult {

	@Builder
	public ImageAugmentColorizeRequest(DefryFactor defryFactor, String prompt, @NonNull Base64Image image) {
		super(
			defryFactor != null ? defryFactor : DefryFactor.ZERO, 
			prompt != null ? prompt : "", 
			image.getTargetHeight(), 
			image.getTargetWidth(), 
			image, 
			RequestType.COLORIZE);
	}

}
