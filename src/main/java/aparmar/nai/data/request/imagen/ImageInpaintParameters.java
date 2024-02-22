package aparmar.nai.data.request.imagen;

import com.google.gson.annotations.SerializedName;

import aparmar.nai.data.request.Base64Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ImageInpaintParameters extends ImageParameters {
	public boolean compatibleWith(AbstractExtraImageParameters otherParameters) {
		if (otherParameters instanceof ImageVibeTransferParameters) { return false; }
		return super.compatibleWith(otherParameters);
	}
	
	@SerializedName("add_original_image")
	@Builder.Default
	protected boolean overlayOriginalImage = false;
	protected Base64Image image, mask;
}
