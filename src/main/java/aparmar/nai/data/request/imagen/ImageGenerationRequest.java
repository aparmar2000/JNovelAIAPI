package aparmar.nai.data.request.imagen;

import com.google.gson.annotations.SerializedName;

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
public class ImageGenerationRequest {
	public static final String ANIME_V2_QUALITY_TAGS = "very aesthetic, best quality, absurdres";
	
	public static final String ANIME_V2_HEAVY_UC = "nsfw, lowres, bad, text, error, missing, extra, fewer, cropped, jpeg artifacts, worst quality, bad quality, watermark, displeasing, unfinished, chromatic aberration, scan, scan artifacts";
	public static final String ANIME_V2_LIGHT_UC = "nsfw, lowres, jpeg artifacts, worst quality, watermark, blurry, very displeasing";
	public static final String FURRY_LOW_QUALITY_UC = "nsfw, {worst quality}, {bad quality}, text, signature, watermark";
	public static final String ANIME_LOW_QUALITY_AND_BAD_ANATOMY_UC = "nsfw, lowres, bad anatomy, bad hands, text, error, missing fingers, extra digit, fewer digits, cropped, worst quality, low quality, normal quality, jpeg artifacts, signature, watermark, username, blurry, blurry, blurry background, blur, blurred, long limbs, extra limbs, mutated, mutated limbs";
	public static final String ANIME_LOW_QUALITY_UC = "nsfw, lowres, text, cropped, worst quality, low quality, normal quality, jpeg artifacts, signature, watermark, username, blurry";
	
	@Getter
	@RequiredArgsConstructor
	public enum ImageGenModel {
		@SerializedName("safe-diffusion")
		ANIME_CURATED(false),
		@SerializedName("nai-diffusion")
		ANIME_FULL(false),
		@SerializedName("nai-diffusion-furry")
		FURRY(false),
		@SerializedName("nai-diffusion-2")
		ANIME_V2(false),
		
		@SerializedName("safe-diffusion-inpainting")
		ANIME_CURATED_INPAINT(true),
		@SerializedName("nai-diffusion-inpainting")
		ANIME_FULL_INPAINT(true),
		@SerializedName("furry-diffusion-inpainting")
		FURRY_INPAINT(true);
		
		private final boolean inpaintingModel;
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
}
