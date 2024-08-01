package aparmar.nai.data.request.imgaug;

import aparmar.nai.data.request.Base64Image;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class ImageAugmentRequestSingleResult extends ImageAugmentRequest {

	protected ImageAugmentRequestSingleResult(DefryFactor defry, String prompt, int height, int width,
			@NonNull Base64Image image, @NonNull RequestType requestType) {
		super(defry, prompt, height, width, image, requestType);
	}
	
	@Override
	public int getReturnImgCount() { return 1; }
}
