package main.java.aparmar.nai.data.request;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import main.java.aparmar.nai.data.request.imagen.ImageControlNetParameters.ControlnetModel;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageAnnotateRequest implements JsonSerializer<ImageAnnotateRequest> {
	public static final ImageAnnotateRequest SERIALIZER_INSTANCE = new ImageAnnotateRequest(); 
	
	@RequiredArgsConstructor
	@Getter
	public enum AnnotationModel {
		@SerializedName("hed")
		PALETTE_SWAP_ANNOTATOR(ControlnetModel.PALETTE_SWAP, 1),
		/** Provides both the depth map expected by Form Lock and a normal map. */
		@SerializedName("midas")
		FORM_LOCK_ANNOTATOR(ControlnetModel.FORM_LOCK, 2),
		@SerializedName("fake_scribble")
		SCRIBBLER_ANNOTATOR(ControlnetModel.SCRIBBLER, 1),
		@SerializedName("mlsd")
		BUILDING_CONTROL_ANNOTATOR(ControlnetModel.BUILDING_CONTROL, 1),
		@SerializedName("uniformer")
		LANDSCAPER_ANNOTATOR(ControlnetModel.LANDSCAPER, 1),
		
		/** No corresponding controlnet as far as I know, but it <i>does</i> work. */
		@SerializedName("openpose")
		POSE_ANNOTATOR(null, 1);
		
		private final ControlnetModel correspondingControlnetModel;
		private final int returnImgCount;
	}
	
	private AnnotationModel model;
	private Base64Image image;
	private Map<String, Number> extraParameters = new HashMap<>();
	
	public static ImageAnnotateRequest defaultRequestForModel(AnnotationModel model, Base64Image image) {
		return new ImageAnnotateRequest(model, image, Collections.emptyMap());
	}
	
	public static ImageAnnotateRequest paletteSwapRequest(Base64Image image, int lowThreshold, int highThreshold) {
		HashMap<String, Number> newParams = new HashMap<>();
		newParams.put("low_threshold", lowThreshold);
		newParams.put("high_threshold", highThreshold);
		
		return new ImageAnnotateRequest(AnnotationModel.PALETTE_SWAP_ANNOTATOR, image, newParams);
	}
	
	public static ImageAnnotateRequest buildingControlRequest(Base64Image image, double distThreshold, double valueThreshold) {
		HashMap<String, Number> newParams = new HashMap<>();
		newParams.put("distance_threshold", distThreshold);
		newParams.put("value_threshold", valueThreshold);
		
		return new ImageAnnotateRequest(AnnotationModel.BUILDING_CONTROL_ANNOTATOR, image, newParams);
	}
	

	@Override
	public JsonElement serialize(ImageAnnotateRequest src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject wrapper = new JsonObject();
		wrapper.add("model", context.serialize(src.getModel(), AnnotationModel.class));
		
		JsonObject parameters = new JsonObject();
		parameters.add("image", context.serialize(src.getImage(), Base64Image.class));
		for (Entry<String, Number> entry : src.getExtraParameters().entrySet()) {
			parameters.addProperty(entry.getKey(), entry.getValue());
		}
		wrapper.add("parameters", parameters);
		
		return wrapper;
	}
}
