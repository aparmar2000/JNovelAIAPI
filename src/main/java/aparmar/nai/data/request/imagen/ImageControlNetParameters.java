package aparmar.nai.data.request.imagen;

import com.google.gson.annotations.SerializedName;

import aparmar.nai.data.request.Base64Image;
import aparmar.nai.data.request.ImageAnnotateRequest.AnnotationModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ImageControlNetParameters extends AbstractExtraImageParameters {
	@RequiredArgsConstructor
	@Getter
	public enum ControlnetModel {
		@SerializedName("hed")
		PALETTE_SWAP(AnnotationModel.PALETTE_SWAP_ANNOTATOR),
		@SerializedName("depth")
		FORM_LOCK(AnnotationModel.FORM_LOCK_ANNOTATOR),
		@SerializedName("scribble")
		SCRIBBLER(AnnotationModel.SCRIBBLER_ANNOTATOR),
		@SerializedName("mlsd")
		BUILDING_CONTROL(AnnotationModel.BUILDING_CONTROL_ANNOTATOR),
		@SerializedName("seg")
		LANDSCAPER(AnnotationModel.LANDSCAPER_ANNOTATOR);
		
		private final AnnotationModel correspondingAnnotationModel;
	}
	
	@SerializedName("controlnet_model")
	protected ControlnetModel model;
	@SerializedName("controlnet_condition")
	protected Base64Image conditionImg;

}
