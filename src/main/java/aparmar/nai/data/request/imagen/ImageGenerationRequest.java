package aparmar.nai.data.request.imagen;

import java.lang.reflect.Type;
import java.util.Arrays;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import aparmar.nai.data.response.UserSubscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ImageGenerationRequest implements JsonSerializer<ImageGenerationRequest> {
	public static final String ANIME_V2_HEAVY_UC = "nsfw, lowres, bad, text, error, missing, extra, fewer, cropped, jpeg artifacts, worst quality, bad quality, watermark, displeasing, unfinished, chromatic aberration, scan, scan artifacts";
	public static final String ANIME_V2_LIGHT_UC = "nsfw, lowres, jpeg artifacts, worst quality, watermark, blurry, very displeasing";
	public static final String ANIME_V3_HEAVY_UC = ANIME_V2_HEAVY_UC;
	public static final String ANIME_V3_LIGHT_UC = ANIME_V2_LIGHT_UC;
	public static final String FURRY_LOW_QUALITY_UC = "nsfw, {worst quality}, {bad quality}, text, signature, watermark";
	public static final String ANIME_LOW_QUALITY_AND_BAD_ANATOMY_UC = "nsfw, lowres, bad anatomy, bad hands, text, error, missing fingers, extra digit, fewer digits, cropped, worst quality, low quality, normal quality, jpeg artifacts, signature, watermark, username, blurry, blurry, blurry background, blur, blurred, long limbs, extra limbs, mutated, mutated limbs";
	public static final String ANIME_LOW_QUALITY_UC = "nsfw, lowres, text, cropped, worst quality, low quality, normal quality, jpeg artifacts, signature, watermark, username, blurry";
	
	public enum QualityTagsLocation {
		DEFAULT,
		PREPEND,
		APPEND;
	}
	
	@Getter
	@RequiredArgsConstructor
	public enum QualityTagsPreset {
		V1_MODELS("masterpiece, best quality", QualityTagsLocation.PREPEND),
		ANIME_V2("very aesthetic, best quality, absurdres", QualityTagsLocation.APPEND),
		ANIME_V3("aesthetic, best quality, absurdres", QualityTagsLocation.APPEND);
		
		private final String tags;
		private final QualityTagsLocation defaultLocation;
	}
	
	@Getter
	@RequiredArgsConstructor
	public enum ImageGenModel {
		@SerializedName("safe-diffusion")
		ANIME_CURATED(QualityTagsPreset.V1_MODELS, false, true),
		@SerializedName("nai-diffusion")
		ANIME_FULL(QualityTagsPreset.V1_MODELS, false, true),
		@SerializedName("nai-diffusion-furry")
		FURRY(QualityTagsPreset.V1_MODELS, false, true),
		@SerializedName("nai-diffusion-2")
		ANIME_V2(QualityTagsPreset.ANIME_V2, false, true),
		@SerializedName("nai-diffusion-3")
		ANIME_V3(QualityTagsPreset.ANIME_V3, false, false),
		
		@SerializedName("safe-diffusion-inpainting")
		ANIME_CURATED_INPAINT(QualityTagsPreset.V1_MODELS, true, true),
		@SerializedName("nai-diffusion-inpainting")
		ANIME_FULL_INPAINT(QualityTagsPreset.V1_MODELS, true, true),
		@SerializedName("furry-diffusion-inpainting")
		FURRY_INPAINT(QualityTagsPreset.V1_MODELS, true, true),
		@SerializedName("nai-diffusion-3-inpainting")
		ANIME_V3_INPAINT(QualityTagsPreset.ANIME_V3, true, false);
		
		private final QualityTagsPreset qualityTagsPreset;
		private final boolean inpaintingModel, supportsControlNet;
	}
	
	public enum ImageGenAction {
		@SerializedName("generate")
		GENERATE,
		@SerializedName("img2img")
		IMG2IMG,
		@SerializedName("infill")
		INFILL;
	}
	
	private String input;
	private ImageGenModel model;
	private ImageGenAction action;
	private ImageParameters parameters;
	
	@Override
	public JsonElement serialize(ImageGenerationRequest src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject wrapper = new JsonObject();
		
		String alteredInput = src.getInput();
		if (src.getParameters().isQualityToggle()) {
			String qualityTagString = src.getModel().getQualityTagsPreset().getTags();
			QualityTagsLocation qualityInsertLocation = src.getParameters().getQualityInsertLocation();
			if (qualityInsertLocation == QualityTagsLocation.DEFAULT) {
				qualityInsertLocation = src.getModel().getQualityTagsPreset().getDefaultLocation();
			}
			
			switch (qualityInsertLocation) {
			case DEFAULT:
			case PREPEND:
				alteredInput = qualityTagString+", "+alteredInput;
				break;
			case APPEND:
				alteredInput = alteredInput+", "+qualityTagString;
				break;
			}
		}
		
		wrapper.addProperty("input", alteredInput);
		wrapper.add("model", context.serialize(src.getModel(), ImageGenModel.class));
		wrapper.add("action", context.serialize(src.getAction(), ImageGenAction.class));
		wrapper.add("parameters", context.serialize(src.getParameters(), src.getParameters().getClass()));
		
		return wrapper;
	}
	
	public boolean isFreeGeneration(UserSubscription subscriptionData) {
		if (!subscriptionData.getPerks().isUnlimitedImageGeneration()) { return false; }
		return Arrays.stream(subscriptionData.getPerks().getUnlimitedImageGenerationLimits())
			.filter(limit->limit.getMaxImages()>=parameters.getImgCount())
			.anyMatch(limit->limit.getResolution()>=parameters.getWidth()*parameters.getHeight());
	}
}
