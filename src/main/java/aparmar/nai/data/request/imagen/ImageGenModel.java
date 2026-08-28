package aparmar.nai.data.request.imagen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.annotations.SerializedName;

import aparmar.nai.data.request.V4VibeData.VibeEncodingType;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ModeTag;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.QualityTagsPreset;
import aparmar.nai.data.request.imagen.ImageParameters.ImageGenSampler;
import aparmar.nai.data.response.UserSubscription;
import aparmar.nai.utils.HardDeprecated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Getter
@RequiredArgsConstructor
public enum ImageGenModel {
	/**
	 * @deprecated This model doesn't exist in the NovelAI API anymore. Use a newer model.</br>
	 * This field will be removed in the future.
	 */
	@Deprecated
	@HardDeprecated
	@SerializedName("safe-diffusion")
	ANIME_CURATED(new QualityTagsPreset[]{QualityTagsPreset.V1_MODELS}, false, false, ImmutableSet.of(Image2ImageParameters.class, ImageControlNetParameters.class), EnumSet.noneOf(VibeEncodingType.class), EnumSet.noneOf(ModeTag.class), ImageGenModel::estimateAnlasCostSD, null),
	/**
	 * @deprecated This model doesn't exist in the NovelAI API anymore. Use a newer model.</br>
	 * This field will be removed in the future.
	 */
	@Deprecated
	@HardDeprecated
	@SerializedName("nai-diffusion")
	ANIME_FULL(new QualityTagsPreset[]{QualityTagsPreset.V1_MODELS}, false, false, ImmutableSet.of(Image2ImageParameters.class, ImageControlNetParameters.class), EnumSet.noneOf(VibeEncodingType.class), EnumSet.noneOf(ModeTag.class), ImageGenModel::estimateAnlasCostSD, null),
	/**
	 * @deprecated This model doesn't exist in the NovelAI API anymore. Use a newer model.</br>
	 * This field will be removed in the future.
	 */
	@Deprecated
	@HardDeprecated
	@SerializedName("nai-diffusion-furry")
	FURRY(new QualityTagsPreset[]{QualityTagsPreset.V1_MODELS}, false, false, ImmutableSet.of(Image2ImageParameters.class, ImageControlNetParameters.class), EnumSet.noneOf(VibeEncodingType.class), EnumSet.noneOf(ModeTag.class), ImageGenModel::estimateAnlasCostSD, null),
	/**
	 * @deprecated This model doesn't exist in the NovelAI API anymore. Use a newer model.</br>
	 * This field will be removed in the future.
	 */
	@Deprecated
	@HardDeprecated
	@SerializedName("nai-diffusion-2")
	ANIME_V2(new QualityTagsPreset[]{QualityTagsPreset.ANIME_V2}, false, false, ImmutableSet.of(Image2ImageParameters.class, ImageControlNetParameters.class), EnumSet.noneOf(VibeEncodingType.class), EnumSet.noneOf(ModeTag.class), ImageGenModel::estimateAnlasCostSD, null),
	@SerializedName("nai-diffusion-3")
	ANIME_V3(new QualityTagsPreset[]{QualityTagsPreset.ANIME_V3}, false, false, ImmutableSet.of(Image2ImageParameters.class, ImageVibeTransferParameters.class), EnumSet.noneOf(VibeEncodingType.class), EnumSet.noneOf(ModeTag.class), ImageGenModel.estimaterAnlasCostSDXL(), null),
	@SerializedName("nai-diffusion-furry-3")
	FURRY_V3(new QualityTagsPreset[]{QualityTagsPreset.FURRY_V3}, false, false, ImmutableSet.of(Image2ImageParameters.class, ImageVibeTransferParameters.class), EnumSet.noneOf(VibeEncodingType.class), EnumSet.noneOf(ModeTag.class), ImageGenModel.estimaterAnlasCostSDXL(), null),
	@SerializedName("nai-diffusion-4-curated-preview")
	ANIME_V4_CURATED(new QualityTagsPreset[]{QualityTagsPreset.ANIME_V4_CURATED}, false, false, ImmutableSet.of(Image2ImageParameters.class, V4MultiCharacterParameters.class, V4ImageVibeTransferParameters.class), EnumSet.of(VibeEncodingType.V4_CURATED), EnumSet.of(ModeTag.ANIME, ModeTag.FURRY), ImageGenModel.estimaterAnlasCostSDXL(), ImageGenModel::adaptForV4),
	@SerializedName("nai-diffusion-4-full")
	ANIME_V4_FULL(new QualityTagsPreset[]{QualityTagsPreset.ANIME_V4_FULL}, false, false, ImmutableSet.of(Image2ImageParameters.class, V4MultiCharacterParameters.class, V4ImageVibeTransferParameters.class), EnumSet.of(VibeEncodingType.V4_FULL), EnumSet.of(ModeTag.ANIME, ModeTag.FURRY), ImageGenModel.estimaterAnlasCostSDXL(), ImageGenModel::adaptForV4),
	@SerializedName("nai-diffusion-4-5-curated")
	ANIME_V4_5_CURATED(new QualityTagsPreset[]{QualityTagsPreset.ANIME_V4_5_CURATED}, false, false, ImmutableSet.of(Image2ImageParameters.class, V4MultiCharacterParameters.class, V4ImageVibeTransferParameters.class, DirectorReferenceParameters.class), EnumSet.of(VibeEncodingType.V4_5_CURATED), EnumSet.of(ModeTag.ANIME, ModeTag.FURRY), ImageGenModel.estimaterAnlasCostSDXL(), ImageGenModel::adaptForV4),
	@SerializedName("nai-diffusion-4-5-full")
	V4_5_FULL(new QualityTagsPreset[]{QualityTagsPreset.V4_5_FULL}, false, false, ImmutableSet.of(Image2ImageParameters.class, V4MultiCharacterParameters.class, V4ImageVibeTransferParameters.class, DirectorReferenceParameters.class), EnumSet.of(VibeEncodingType.V4_5_FULL), EnumSet.of(ModeTag.ANIME, ModeTag.FURRY, ModeTag.BACKGROUNDS), ImageGenModel.estimaterAnlasCostSDXL(), ImageGenModel::adaptForV4),
	@SerializedName("nai-diffusion-5-curated")
	V5_CURATED(new QualityTagsPreset[]{QualityTagsPreset.V5_STANDARD, QualityTagsPreset.V5_LIGHT}, false, true, ImmutableSet.of(Image2ImageParameters.class, V4MultiCharacterParameters.class), EnumSet.noneOf(VibeEncodingType.class), EnumSet.of(ModeTag.ANIME, ModeTag.FURRY), ImageGenModel.estimaterAnlasCostSDXL(1.5), ImageGenModel::adaptForV4),
	@SerializedName("nai-diffusion-5-full")
	V5_FULL(new QualityTagsPreset[]{QualityTagsPreset.V5_STANDARD, QualityTagsPreset.V5_LIGHT}, false, true, ImmutableSet.of(Image2ImageParameters.class, V4MultiCharacterParameters.class), EnumSet.noneOf(VibeEncodingType.class), EnumSet.of(ModeTag.ANIME, ModeTag.FURRY, ModeTag.BACKGROUNDS), ImageGenModel.estimaterAnlasCostSDXL(1.5), ImageGenModel::adaptForV4),

	/**
	 * @deprecated This model doesn't exist in the NovelAI API anymore. Use a newer model.</br>
	 * This field will be removed in the future.
	 */
	@Deprecated
	@HardDeprecated
	@SerializedName("safe-diffusion-inpainting")
	ANIME_CURATED_INPAINT(new QualityTagsPreset[]{QualityTagsPreset.V1_MODELS}, true, false, ImmutableSet.of(Image2ImageParameters.class, ImageControlNetParameters.class), EnumSet.noneOf(VibeEncodingType.class), EnumSet.noneOf(ModeTag.class), ImageGenModel::estimateAnlasCostSD, null),
	/**
	 * @deprecated This model doesn't exist in the NovelAI API anymore. Use a newer model.</br>
	 * This field will be removed in the future.
	 */
	@Deprecated
	@HardDeprecated
	@SerializedName("nai-diffusion-inpainting")
	ANIME_FULL_INPAINT(new QualityTagsPreset[]{QualityTagsPreset.V1_MODELS}, true, false, ImmutableSet.of(Image2ImageParameters.class, ImageControlNetParameters.class), EnumSet.noneOf(VibeEncodingType.class), EnumSet.noneOf(ModeTag.class), ImageGenModel::estimateAnlasCostSD, null),
	/**
	 * @deprecated This model doesn't exist in the NovelAI API anymore. Use a newer model.</br>
	 * This field will be removed in the future.
	 */
	@Deprecated
	@HardDeprecated
	@SerializedName("furry-diffusion-inpainting")
	FURRY_INPAINT(new QualityTagsPreset[]{QualityTagsPreset.V1_MODELS}, true, false, ImmutableSet.of(Image2ImageParameters.class, ImageControlNetParameters.class), EnumSet.noneOf(VibeEncodingType.class), EnumSet.noneOf(ModeTag.class), ImageGenModel::estimateAnlasCostSD, null),
	@SerializedName("nai-diffusion-3-inpainting")
	ANIME_V3_INPAINT(new QualityTagsPreset[]{QualityTagsPreset.ANIME_V3}, true, false, ImmutableSet.of(Image2ImageParameters.class, ImageVibeTransferParameters.class), EnumSet.noneOf(VibeEncodingType.class), EnumSet.noneOf(ModeTag.class), ImageGenModel.estimaterAnlasCostSDXL(), null),
	@SerializedName("nai-diffusion-furry-3-inpainting")
	FURRY_V3_INPAINT(new QualityTagsPreset[]{QualityTagsPreset.FURRY_V3}, true, false, ImmutableSet.of(Image2ImageParameters.class, ImageVibeTransferParameters.class), EnumSet.noneOf(VibeEncodingType.class), EnumSet.noneOf(ModeTag.class), ImageGenModel.estimaterAnlasCostSDXL(), null),
	@SerializedName("nai-diffusion-4-curated-inpainting")
	ANIME_V4_CURATED_INPAINT(new QualityTagsPreset[]{QualityTagsPreset.ANIME_V4_CURATED}, true, false, ImmutableSet.of(Image2ImageParameters.class, V4MultiCharacterParameters.class), EnumSet.noneOf(VibeEncodingType.class), EnumSet.of(ModeTag.ANIME, ModeTag.FURRY), ImageGenModel.estimaterAnlasCostSDXL(), ImageGenModel::adaptForV4),
	@SerializedName("nai-diffusion-4-full-inpainting")
	ANIME_V4_FULL_INPAINT(new QualityTagsPreset[]{QualityTagsPreset.ANIME_V4_FULL}, true, false, ImmutableSet.of(Image2ImageParameters.class, V4MultiCharacterParameters.class), EnumSet.noneOf(VibeEncodingType.class), EnumSet.of(ModeTag.ANIME, ModeTag.FURRY), ImageGenModel.estimaterAnlasCostSDXL(), ImageGenModel::adaptForV4),
	@SerializedName("nai-diffusion-4-5-curated-inpainting")
	ANIME_V4_5_CURATED_INPAINT(new QualityTagsPreset[]{QualityTagsPreset.ANIME_V4_5_CURATED}, true, false, ImmutableSet.of(Image2ImageParameters.class, V4MultiCharacterParameters.class, DirectorReferenceParameters.class), EnumSet.of(VibeEncodingType.V4_5_CURATED), EnumSet.of(ModeTag.ANIME, ModeTag.FURRY), ImageGenModel.estimaterAnlasCostSDXL(), ImageGenModel::adaptForV4),
	@SerializedName("nai-diffusion-4-5-full-inpainting")
	V4_5_FULL_INPAINT(new QualityTagsPreset[]{QualityTagsPreset.V4_5_FULL}, true, false, ImmutableSet.of(Image2ImageParameters.class, V4MultiCharacterParameters.class, DirectorReferenceParameters.class), EnumSet.of(VibeEncodingType.V4_5_FULL), EnumSet.of(ModeTag.ANIME, ModeTag.FURRY), ImageGenModel.estimaterAnlasCostSDXL(), ImageGenModel::adaptForV4),
	@SerializedName("nai-diffusion-5-full-inpainting")
	V5_FULL_INPAINT(new QualityTagsPreset[]{QualityTagsPreset.V5_STANDARD, QualityTagsPreset.V5_LIGHT}, true, false, ImmutableSet.of(Image2ImageParameters.class, V4MultiCharacterParameters.class), EnumSet.noneOf(VibeEncodingType.class), EnumSet.of(ModeTag.ANIME, ModeTag.FURRY, ModeTag.BACKGROUNDS), ImageGenModel.estimaterAnlasCostSDXL(1.5), ImageGenModel::adaptForV4);
	
	private final QualityTagsPreset[] qualityTagsPresets;
	private final boolean inpaintingModel;
	private final boolean standaloneUpscalingModel;
	private final Set<Class<? extends AbstractExtraImageParameters>> supportedExtraParameterTypes;
	private final EnumSet<VibeEncodingType> supportedVibeEncodingTypes;
	private final EnumSet<ModeTag> supportedModeTags;
	private final AnlasCostEstimator anlasCostEstimator;
	@Getter(AccessLevel.PROTECTED)
	private final ImageRequestJsonAdapterFunc jsonAdapterFunc;
	
	@FunctionalInterface
	private static interface AnlasCostEstimator {
		public int apply(ImageParameters parameters, List<AbstractExtraImageParameters> extraParameters);
	}
	
	// --- Backwards Compatibility
	
	public QualityTagsPreset getQualityTagsPreset() {
		return qualityTagsPresets[0];
	}
	
	// ---

	/**
	 * Tests if a particular {@link AbstractExtraImageParameters} instance is compatible with this model.
	 * @param extraImageParameter the {@code AbstractExtraImageParameters} instance to check.
	 * @return An {@code Optional<String>} containing the incompatibility reason, if there is one.
	 */
	public Optional<String> doesModelSupportExtraParameter(AbstractExtraImageParameters extraImageParameter) {
		if (!doesModelSupportExtraParameterType(extraImageParameter.getClass())) {
			return Optional.of(String.format("Model type %s is not compatible with extraParameter type %s", this, extraImageParameter.getClass()));
		}
		if (extraImageParameter instanceof V4ImageVibeTransferParameters) {
			val encodingType = ((V4ImageVibeTransferParameters)extraImageParameter).getEncodingType();
			if (encodingType != null && !supportedVibeEncodingTypes.contains(encodingType)) {
				return Optional.of(String.format("Model type %s is not compatible with vibes encoded in type %s", this, encodingType));
			}
		}
		return Optional.empty();
	}
	public boolean doesModelSupportExtraParameterType(Class<? extends AbstractExtraImageParameters> extraParameterType) {
		return supportedExtraParameterTypes.contains(extraParameterType);
	}
	
	boolean doesModelSupportModeTag(ModeTag modeTag) {
		return modeTag==null || supportedModeTags.contains(modeTag);
	}
	
	public int estimateAnlasCost(ImageParameters parameters) {
		return anlasCostEstimator.apply(parameters, new ArrayList<>(0));
	}
	public int estimateAnlasCost(ImageParameters parameters, AbstractExtraImageParameters... extraParameters) {
		return estimateAnlasCost(parameters, Arrays.asList(extraParameters));
	}
	public int estimateAnlasCost(ImageParameters parameters, List<AbstractExtraImageParameters> extraParameters) {
		int extraParameterCost = extraParameters.stream().mapToInt(AbstractExtraImageParameters::getExtraCost).sum();
		if (parameters.getImgCount() == 0) { return extraParameterCost; }
		
		return anlasCostEstimator.apply(parameters, extraParameters) + extraParameterCost;
	}
	public int estimateAnlasCostIncludingSubscription(ImageParameters parameters, UserSubscription subscription) {
		if (ImageGenerationRequest.isFreeGeneration(subscription, parameters.toBuilder().imgCount(1).build())) {
			parameters = parameters.toBuilder()
					.imgCount(parameters.getImgCount()-1)
					.build();
		}
		
		return estimateAnlasCost(parameters);
	}
	
	public boolean hasJsonAdapterFunc() {
		return jsonAdapterFunc != null;
	}
	public void adaptJson(ImageGenerationRequest request, JsonElement currentJson, JsonSerializationContext context) {
		if (jsonAdapterFunc != null) { jsonAdapterFunc.adapt(request, currentJson, context); }
	}
	
	// Anlas cost estimation
	private static final EnumSet<ImageGenSampler> CHEAP_SAMPLER_SET = EnumSet.of(ImageGenSampler.DDIM,ImageGenSampler.K_EULER,ImageGenSampler.K_EULER_ANCESTRAL);
	private static final int PIXELS_1024_SQUARE = (1024 * 1024);
	private static int estimateAnlasCostSD(ImageParameters parameters, List<AbstractExtraImageParameters> extraParameters) {
		double imgPixels = parameters.getWidth() * parameters.getHeight();
		
		double perSample;
		if (imgPixels <= PIXELS_1024_SQUARE && CHEAP_SAMPLER_SET.contains(parameters.getSampler())) {
			perSample = ((15.266497014243718 * Math.exp((imgPixels / PIXELS_1024_SQUARE) * 0.6326248927474729) - 15.225164493059737) / 28) * parameters.getSteps();
		} else {
			int cost64Mult = (int) (Math.floor(parameters.getWidth()/64) * Math.floor(parameters.getHeight()/64));
			int costIndex = ImageGenConstants.CALCULATED_COST_INDEX_BY_SIZE_ARRAY[cost64Mult-1];
			
			double[] costMultLookupArray;
			if (parameters.isSmeaEnabled()) {
				if (parameters.isDynSmeaEnabled()) {
					costMultLookupArray = ImageGenConstants.DYN_SMEA_COST_FACTOR_PAIR_ARRAY;
				} else {
					costMultLookupArray = ImageGenConstants.SMEA_COST_FACTOR_PAIR_ARRAY;
				}
			} else if (parameters.getSampler() == ImageGenSampler.DDIM) {
				costMultLookupArray = ImageGenConstants.DDIM_COST_FACTOR_PAIR_ARRAY;
			} else {
				costMultLookupArray = ImageGenConstants.K_EULER_ANCESTRAL_COST_FACTOR_PAIR_ARRAY;
			}
			
			perSample = costMultLookupArray[costIndex] * parameters.steps + costMultLookupArray[costIndex+1];
		}
		
		return estimateAnlasCostFinalStep(1.0, perSample, parameters, extraParameters);
	}

	private static AnlasCostEstimator estimaterAnlasCostSDXL() {
		return estimaterAnlasCostSDXL(1.0);
	}
	private static AnlasCostEstimator estimaterAnlasCostSDXL(double extraCostFac) {
		return (p, eP) -> estimateAnlasCostSDXL(extraCostFac, p, eP);
	}
	
	private static int estimateAnlasCostSDXL(double extraCostFac, ImageParameters parameters, List<AbstractExtraImageParameters> extraParameters) {
		double sizeComponent = parameters.getWidth() * parameters.getHeight();
		double smeaFactor = (parameters.isSmeaEnabled()&&parameters.isDynSmeaEnabled())?1.4: parameters.isSmeaEnabled()?1.2:1.0;
		if (extraParameters.stream().anyMatch(p->p instanceof Image2ImageParameters)) { smeaFactor = 1; }

		double perSample = Math.ceil(2951823174884865e-21 * sizeComponent + 5.753298233447344e-7 * sizeComponent * parameters.getSteps()) * smeaFactor;
		
		return estimateAnlasCostFinalStep(extraCostFac, perSample, parameters, extraParameters);
	}
	
	private static int estimateAnlasCostFinalStep(double extraCostFac, double baseSampleFactor, ImageParameters parameters, List<AbstractExtraImageParameters> extraParameters) {
		double sampleFactor = baseSampleFactor * extraCostFac;
		
		double img2imgStrengthFactor = extraParameters.stream()
				.filter(p->p instanceof Image2ImageParameters)
				.findAny()
				.map(p->((Image2ImageParameters)p).getStrength())
				.orElse(1.0);
		sampleFactor = Math.max(Math.ceil(sampleFactor * img2imgStrengthFactor), 2);
		if (parameters.getUcScale()!=1) { sampleFactor = Math.ceil(sampleFactor * 1.3); }
		
		int extraFactor = 0;
		val optV4VibeTransferParameters = extraParameters.stream()
				.filter(p->p instanceof V4ImageVibeTransferParameters)
				.map(p->(V4ImageVibeTransferParameters)p)
				.findAny();
		if (optV4VibeTransferParameters.isPresent() && optV4VibeTransferParameters.get().getVibeDatas().size()>4) {
			extraFactor += 2*(optV4VibeTransferParameters.get().getVibeDatas().size()-4);
		}
		
		return (int) (sampleFactor * parameters.getImgCount())+extraFactor;
	}

	// Adapter Functions
	private static JsonElement adaptForV4(ImageGenerationRequest request, JsonElement currentJson, JsonSerializationContext context) {
		JsonObject curJsonObj = currentJson.getAsJsonObject();
		JsonObject parametersObj = curJsonObj.getAsJsonObject("parameters");
		
		if (!parametersObj.has("v4_prompt")) {
			parametersObj.add("v4_prompt", new JsonObject());
			parametersObj.getAsJsonObject("v4_prompt").add("caption", new JsonObject());
			parametersObj.getAsJsonObject("v4_prompt").getAsJsonObject("caption").add("char_captions", new JsonArray());
			parametersObj.getAsJsonObject("v4_prompt").addProperty("use_coords", false);
			parametersObj.getAsJsonObject("v4_prompt").addProperty("use_order", true);
		}
		JsonObject v4PromptObj = parametersObj.getAsJsonObject("v4_prompt").getAsJsonObject("caption");
		v4PromptObj.addProperty("base_caption", request.getInput());
		
		if (!parametersObj.has("v4_negative_prompt")) {
			parametersObj.add("v4_negative_prompt", new JsonObject());
			parametersObj.getAsJsonObject("v4_negative_prompt").add("caption", new JsonObject());
			parametersObj.getAsJsonObject("v4_negative_prompt").getAsJsonObject("caption").add("char_captions", new JsonArray());
		}
		JsonObject v4NegPromptObj = parametersObj.getAsJsonObject("v4_negative_prompt").getAsJsonObject("caption");
		v4NegPromptObj.addProperty("base_caption", request.getParameters().getUndesiredContent());
		
		return curJsonObj;
	}
}