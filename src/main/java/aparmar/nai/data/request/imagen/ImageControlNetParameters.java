package main.java.aparmar.nai.data.request.imagen;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import main.java.aparmar.nai.data.request.Base64Image;
import main.java.aparmar.nai.data.request.ImageAnnotateRequest.AnnotationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ImageControlNetParameters extends ImageParameters {
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
