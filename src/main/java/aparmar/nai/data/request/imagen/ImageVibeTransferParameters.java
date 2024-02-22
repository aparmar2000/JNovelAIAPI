package aparmar.nai.data.request.imagen;

import com.google.gson.annotations.SerializedName;

import aparmar.nai.data.request.Base64Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ImageVibeTransferParameters extends AbstractExtraImageParameters {	
	@SerializedName("reference_information_extracted")
	@Builder.Default
	protected double referenceInformationExtracted = 1.0;
	@SerializedName("reference_strength")
	@Builder.Default
	protected double referenceStrength = 0.6;
	@SerializedName("reference_image")
	protected Base64Image referenceImage;
}
