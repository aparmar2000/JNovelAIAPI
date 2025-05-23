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
public class ImageAugmentLineArtRequest extends ImageAugmentRequestSingleResult {

	@Builder(toBuilder = true)
	public ImageAugmentLineArtRequest(@NonNull Base64Image image) {
		super(null, null, image.getTargetHeight(), image.getTargetWidth(), image, RequestType.LINE_ART);
	}

}
