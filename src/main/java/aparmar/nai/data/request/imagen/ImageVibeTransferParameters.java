package aparmar.nai.data.request.imagen;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import aparmar.nai.data.request.Base64Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ImageVibeTransferParameters extends AbstractExtraImageParameters implements JsonSerializer<ImageVibeTransferParameters> {
	@Singular
	protected List<VibeTransferImage> vibeImages;
	
	protected Base64Image[] getVibeImageImages() {
		return vibeImages.stream().map(VibeTransferImage::getReferenceImage).toArray(Base64Image[]::new);
	}
	protected double[] getVibeImageInformationExtractedPerVibe() {
		return vibeImages.stream().mapToDouble(VibeTransferImage::getReferenceInformationExtracted).toArray();
	}
	protected double[] getVibeImageReferenceStrengthPerVibe() {
		return vibeImages.stream().mapToDouble(VibeTransferImage::getReferenceStrength).toArray();
	}
	
	@Data
	@Builder
	@AllArgsConstructor
	public static class VibeTransferImage {
		@Builder.Default
		protected double referenceInformationExtracted = 1.0;
		@Builder.Default
		protected double referenceStrength = 0.6;
		protected Base64Image referenceImage;
	}
	
	//===== Serialization

	@Override
	public JsonElement serialize(ImageVibeTransferParameters src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject wrapper = new JsonObject();
		wrapper.add("reference_image_multiple", context.serialize(src.getVibeImageImages(), Base64Image[].class));
		wrapper.add("reference_information_extracted_multiple", context.serialize(src.getVibeImageInformationExtractedPerVibe(), double[].class));
		wrapper.add("reference_strength_multiple", context.serialize(src.getVibeImageReferenceStrengthPerVibe(), double[].class));
		
		return wrapper;
	}
}
