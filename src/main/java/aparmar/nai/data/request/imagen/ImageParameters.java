package aparmar.nai.data.request.imagen;

import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.annotations.SerializedName;

import aparmar.nai.data.request.imagen.ImageGenerationRequest.QualityTagsLocation;
import aparmar.nai.utils.GsonExclude;
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
		@SerializedName("k_euler")
		K_EULER,
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
		DPM_FAST;
		
//		@SerializedName("k_lms")
//		K_LMS,
//		@SerializedName("plms")
//		PLMS;
	}
	
	public enum SamplingSchedule {
		@SerializedName("native")
		NATIVE,
		@SerializedName("karras")
		KARRAS,
		@SerializedName("exponential")
		EXPONENTIAL,
		@SerializedName("polyexponential")
		POLYEXPONENTIAL;
	}
	
	@Builder.Default
	protected long seed = ThreadLocalRandom.current().nextLong();
	protected int height, width;
	@Builder.Default
	protected int steps = 28;
	@Builder.Default
	protected double scale = 5.0;
	/** Only functions with V3 models */
	@SerializedName("cfg_rescale")
	@Builder.Default
	protected double scaleRescaleFactor = 0;

	@Builder.Default
	protected ImageParameters.ImageGenSampler sampler = ImageGenSampler.K_EULER_ANCESTRAL;
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
	/** Only functions with V3 models */
	@SerializedName("noise_schedule")
	@Builder.Default
	protected SamplingSchedule noiseSchedule = SamplingSchedule.NATIVE;

	@Builder.Default
	protected boolean qualityToggle = false;
	@Builder.Default
	@GsonExclude
	protected QualityTagsLocation qualityInsertLocation = QualityTagsLocation.DEFAULT;
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
	
	public static int getNearestMultipleOf64(int val) {
		return (int) (64*(Math.round(val/64.0)));
	}
}