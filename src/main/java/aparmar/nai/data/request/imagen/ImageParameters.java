package aparmar.nai.data.request.imagen;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class ImageParameters {
	public enum ImageGenSampler {
		@SerializedName("k_euler_ancestral")
		K_EULER_ANCESTRAL,
		@SerializedName("k_dpmpp_2s_ancestral")
		DPM_PLUS_PLUS_2S_ANCESTRAL,
		@SerializedName("ddim")
		DDIM,
		
		@SerializedName("k_dpmpp_2m")
		DPM_PLUS_PLUS_2M,
		@SerializedName("k_dpm_2")
		DPM2,
		@SerializedName("k_dpmpp_sde")
		DPM_PLUS_PLUS_SDE,
		@SerializedName("k_dpm_fast")
		DPM_FAST,
		@SerializedName("k_euler")
		K_EULER;
		
//		@SerializedName("k_lms")
//		K_LMS,
//		@SerializedName("plms")
//		PLMS;
	}
	
	protected long seed;
	protected int height, width;
	protected int steps;
	protected double scale;
	
	protected ImageParameters.ImageGenSampler sampler;
	@SerializedName("sm")
	@Builder.Default
	protected boolean smeaEnabled = false;
	/** The UI doesn't let you enable this without also enabling SMEA, but the API will accept it fine. */
	@SerializedName("sm_dyn")
	@Builder.Default
	protected boolean dynSmeaEnabled = false;
	@SerializedName("dynamic_thresholding")
	@Builder.Default
	protected boolean decrisperEnabled = false;

	@Builder.Default
	protected boolean qualityToggle = false;
	@Builder.Default
	protected int ucPreset = 0;
	@SerializedName("negative_prompt")
	protected String undesiredContent;
	@SerializedName("uncond_scale")
	@Builder.Default
	protected double ucScale = 1.0;

	@SerializedName("n_samples")
	@Builder.Default
	protected int imgCount = 1;
	
	protected final boolean legacy = false;
}