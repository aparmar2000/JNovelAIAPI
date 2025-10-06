package aparmar.nai.data.request.imagen;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import aparmar.nai.data.request.Base64Image;
import aparmar.nai.data.request.imagen.DirectorReferenceParameter.DirectorReferenceDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class DirectorReferenceParameters extends AbstractExtraImageParameters implements JsonSerializer<DirectorReferenceParameters> {
	
	@Singular
	protected List<DirectorReferenceParameter> directorReferences;
	
	public boolean compatibleWith(AbstractExtraImageParameters otherParameters) {
		if (otherParameters instanceof V4ImageVibeTransferParameters) {
			return false;
		}
		if (otherParameters instanceof ImageVibeTransferParameters) {
			return false;
		}
		return super.compatibleWith(otherParameters);
	}
	
	public int getExtraCost() {
		return super.getExtraCost() + directorReferences.stream()
			.mapToInt(DirectorReferenceParameter::getExtraCost)
			.sum();
	}

	@Override
	public JsonElement serialize(DirectorReferenceParameters src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject wrapper = new JsonObject();

		List<DirectorReferenceDescription> refDescriptionList = new LinkedList<>();
		List<Base64Image> refImageList = new LinkedList<>();
		List<Float> refInfoExtractList = new LinkedList<>();
		List<Float> refStrengthList = new LinkedList<>();
		
		for (DirectorReferenceParameter directorReference : src.getDirectorReferences()) {
			refDescriptionList.add(directorReference.getDescription());
			refImageList.add(directorReference.getPreprocessedReferenceImage());
			refInfoExtractList.add(directorReference.getInformationExtracted());
			refStrengthList.add(directorReference.getStrength());
		}
		
		wrapper.add("director_reference_descriptions", context.serialize(refDescriptionList));
		wrapper.add("director_reference_images", context.serialize(refImageList));
		wrapper.add("director_reference_information_extracted", context.serialize(refInfoExtractList));
		wrapper.add("director_reference_strength_values", context.serialize(refStrengthList));
		
		return wrapper;
	}
}
