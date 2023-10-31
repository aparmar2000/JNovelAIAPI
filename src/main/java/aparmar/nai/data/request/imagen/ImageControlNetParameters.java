package main.java.aparmar.nai.data.request.imagen;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import main.java.aparmar.nai.data.request.Base64Image;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ImageControlNetParameters extends ImageParameters {
	public enum ControlnetModel {
		@SerializedName("hed")
		PALETTE_SWAP,
		@SerializedName("depth")
		FORM_LOCK,
		@SerializedName("scribble")
		SCRIBBLER,
		@SerializedName("mlsd")
		BUILDING_CONTROL,
		@SerializedName("seg")
		LANDSCAPER;
	}
	
	@SerializedName("controlnet_model")
	protected ControlnetModel model;
	@SerializedName("controlnet_condition")
	protected Base64Image conditionImg;

}
