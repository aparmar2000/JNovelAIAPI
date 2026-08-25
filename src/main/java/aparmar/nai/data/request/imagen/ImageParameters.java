package aparmar.nai.data.request.imagen;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import aparmar.nai.data.request.imagen.ImageGenerationRequest.QualityTagsLocation;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.QualityTagsPreset;
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
	
	public enum ImageFormat {
		@SerializedName("png")
		PNG,
		@SerializedName("webp")
		WEBP;
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
	protected long seed = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
	protected int height, width;
	@Builder.Default
	protected int steps = 28;
	@Builder.Default
	protected double scale = 5.0;
	/** Only functions with V3 & later models */
	@SerializedName("cfg_rescale")
	@Builder.Default
	protected double scaleRescaleFactor = 0;
	@SerializedName("color_correct")
	@Builder.Default
	protected boolean colorCorrect = false;

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
	/** Only functions with V3 and later models */
	@SerializedName("noise_schedule")
	@Builder.Default
	protected SamplingSchedule noiseSchedule = SamplingSchedule.NATIVE;
	@SerializedName("prefer_brownian")
	@Builder.Default
	protected boolean preferBrownian = true;
	@SerializedName("skip_cfg_above_sigma")
	/** Variety mode control - default for 'on' is 58 */
	@Builder.Default
	@Nullable
	protected Double skipCfgAboveSigma = null;

	@Builder.Default
	protected boolean qualityToggle = false;
	@Builder.Default
	@GsonExclude
	@Nullable
	protected QualityTagsPreset qualityPreset = null;
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

	@SerializedName("image_format")
	@Builder.Default
	protected ImageFormat imageFormat = ImageFormat.PNG;
	/** Currently only supported by V5 models */
	@SerializedName("straight_alpha")
	@Builder.Default
	protected boolean straightAlpha = false;
	/** Pure pass-through hint for the model: the prompt is asking for a transparent background. Omegalaser does not interpret it. */
	@SerializedName("tag_hint_transparent_background")
	@Builder.Default
	protected boolean transparentBackgroundHint = false;
	@SerializedName("n_samples")
	@Builder.Default
	protected int imgCount = 1;
	
	@SerializedName("params_version")
	protected final int paramsVersion = 4;
	protected final boolean legacy = false;
	@SerializedName("legacy_v3_extend")
	protected final boolean legacyV3Extend = false;
	
	public boolean compatibleWith(AbstractExtraImageParameters otherParameters) { return true; }

	/**
	 * Creates a new {@code ImageParameters} instance.
	 *
	 * @param seed
	 * @param height
	 * @param width
	 * @param steps
	 * @param scale
	 * @param scaleRescaleFactor Only functions with V3 & later models
	 * @param sampler
	 * @param smeaEnabled
	 * @param dynSmeaEnabled The UI doesn't let you enable this without also enabling SMEA, but the API will accept it fine.
	 * @param decrisperEnabled
	 * @param noiseSchedule Only functions with V3 and later models
	 * @param qualityToggle
	 * @param qualityInsertLocation
	 * @param ucPreset
	 * @param undesiredContent
	 * @param ucScale
	 * @param imgCount
	 */
	public ImageParameters(int seed, int height, int width, int steps, int scale, int scaleRescaleFactor, 
			ImageGenSampler sampler, boolean smeaEnabled, boolean dynSmeaEnabled, boolean decrisperEnabled, 
			SamplingSchedule noiseSchedule, boolean qualityToggle, QualityTagsLocation qualityInsertLocation, 
			int ucPreset, String undesiredContent, int ucScale, int imgCount) {
		this.seed = seed;
		this.height = height;
		this.width = width;
		this.steps = steps;
		this.scale = scale;
		this.scaleRescaleFactor = scaleRescaleFactor;
		this.sampler = sampler;
		this.smeaEnabled = smeaEnabled;
		this.dynSmeaEnabled = dynSmeaEnabled;
		this.decrisperEnabled = decrisperEnabled;
		this.noiseSchedule = noiseSchedule;
		this.qualityToggle = qualityToggle;
		this.qualityInsertLocation = qualityInsertLocation;
		this.ucPreset = ucPreset;
		this.undesiredContent = undesiredContent;
		this.ucScale = ucScale;
		this.imgCount = imgCount;
	}
	
	public static int getNearestMultipleOf64(int val) {
		return (int) (64*(Math.round(val/64.0)));
	}

	public static abstract class ImageParametersBuilder<C extends ImageParameters, B extends ImageParameters.ImageParametersBuilder<C, B>> {
		public B qualityToggle(final boolean qualityToggle) {
			this.qualityToggle$value = qualityToggle;
			qualityToggle$set = true;
			
			if (!qualityToggle) {
				this.qualityPreset$value = null;
				qualityPreset$set = true;
			} else if (qualityPreset$value == null) {
				this.qualityPreset$value = QualityTagsPreset.DEFAULT;
				qualityPreset$set = true;
			}
			return self();
		}
		
		public B defaultQualityPreset() {
			this.qualityPreset$value = QualityTagsPreset.DEFAULT;
			qualityPreset$set = true;
			return self().qualityToggle(true);
		}
		
		public B qualityPreset(@Nullable QualityTagsPreset qualityPreset) {
			this.qualityPreset$value = qualityPreset;
			qualityPreset$set = true;
			return self().qualityToggle(qualityPreset != null);
		}
		
		public B varietyPlus(boolean enabled) {
			this.skipCfgAboveSigma$value = enabled ? 58. : null;
			skipCfgAboveSigma$set = true;
			return self();
		}
	}
}