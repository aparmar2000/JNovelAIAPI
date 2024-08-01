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
public class ImageAugmentRemoveBackgroundRequest extends ImageAugmentRequest {

	@Builder(toBuilder = true)
	public ImageAugmentRemoveBackgroundRequest(@NonNull Base64Image image) {
		super(null, null, image.getTargetHeight(), image.getTargetWidth(), image, RequestType.REMOVE_BACKGROUND);
	}
	
	@Override
	public int getReturnImgCount() { return 3; }

}
