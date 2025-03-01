package aparmar.nai.data.request.imagen;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import aparmar.nai.data.request.imagen.ImageParameters.ImageGenSampler;
import aparmar.nai.data.response.UserSubscription;
import aparmar.nai.utils.AnnotationUtils;
import aparmar.nai.utils.HardDeprecated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	public static final String FURRY_V3_LIGHT_UC = "nsfw, {worst quality}, guide lines, unfinished, bad, url, tall image, widescreen, compression artifacts, unknown text";
	public static final String FURRY_V3_HEAVY_UC = "nsfw, {{worst quality}}, [displeasing], {unusual pupils}, guide lines, {{unfinished}}, {bad}, url, artist name, {{tall image}}, mosaic, {sketch page}, comic panel, impact (font), [dated], {logo}, ych, {what}, {where is your god now}, {distorted text}, repeated text, {floating head}, {1994}, {widescreen}, absolutely everyone, sequence, {compression artifacts}, hard translated, {cropped}, {commissioner name}, unknown text, high contrast";
	/**
	 * @deprecated Use {@link ANIME_V4_CURATED_LIGHT_UC} instead. May be removed in future.
	 */
	@Deprecated
	public static final String ANIME_V4_LIGHT_UC = "blurry, lowres, error, worst quality, bad quality, jpeg artifacts, very displeasing, logo, dated, signature";
	/**
	 * @deprecated Use {@link ANIME_V4_CURATED_HEAVY_UC} instead. May be removed in future.
	 */
	@Deprecated
	public static final String ANIME_V4_HEAVY_UC = "blurry, lowres, error, film grain, scan artifacts, worst quality, bad quality, jpeg artifacts, very displeasing, chromatic aberration, logo, dated, signature, multiple views, gigantic breasts";
	public static final String ANIME_V4_CURATED_LIGHT_UC = ANIME_V4_LIGHT_UC;
	public static final String ANIME_V4_CURATED_HEAVY_UC = ANIME_V4_HEAVY_UC;
	public static final String ANIME_V4_FULL_LIGHT_UC = "blurry, lowres, error, worst quality, bad quality, jpeg artifacts, very displeasing";
	public static final String ANIME_V4_FULL_HEAVY_UC = "blurry, lowres, error, film grain, scan artifacts, worst quality, bad quality, jpeg artifacts, very displeasing, chromatic aberration, multiple views, logo, too many watermarks";
	
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
		ANIME_V3("aesthetic, best quality, absurdres", QualityTagsLocation.APPEND),
		FURRY_V3("{best quality}, {amazing quality}", QualityTagsLocation.APPEND),
		ANIME_V4_CURATED("rating:general, best quality, very aesthetic, absurdres", QualityTagsLocation.APPEND),
		ANIME_V4_FULL("no text, best quality, very aesthetic, absurdres", QualityTagsLocation.APPEND),
		/**
		 * Use {@link ANIME_V4_CURATED} instead. May be removed in future.
		 */
		@Deprecated
		ANIME_V4("rating:general, best quality, very aesthetic, absurdres", QualityTagsLocation.APPEND);
		
		private final String tags;
		private final QualityTagsLocation defaultLocation;
	}
	
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
		ANIME_CURATED(QualityTagsPreset.V1_MODELS, false, ImmutableSet.of(Image2ImageParameters.class, ImageControlNetParameters.class), ImageGenModel::estimateAnlasCostSD, null),
		/**
		 * @deprecated This model doesn't exist in the NovelAI API anymore. Use a newer model.</br>
		 * This field will be removed in the future.
		 */
		@Deprecated
		@HardDeprecated
		@SerializedName("nai-diffusion")
		ANIME_FULL(QualityTagsPreset.V1_MODELS, false, ImmutableSet.of(Image2ImageParameters.class, ImageControlNetParameters.class), ImageGenModel::estimateAnlasCostSD, null),
		/**
		 * @deprecated This model doesn't exist in the NovelAI API anymore. Use a newer model.</br>
		 * This field will be removed in the future.
		 */
		@Deprecated
		@HardDeprecated
		@SerializedName("nai-diffusion-furry")
		FURRY(QualityTagsPreset.V1_MODELS, false, ImmutableSet.of(Image2ImageParameters.class, ImageControlNetParameters.class), ImageGenModel::estimateAnlasCostSD, null),
		@SerializedName("nai-diffusion-2")
		ANIME_V2(QualityTagsPreset.ANIME_V2, false, ImmutableSet.of(Image2ImageParameters.class, ImageControlNetParameters.class), ImageGenModel::estimateAnlasCostSD, null),
		@SerializedName("nai-diffusion-3")
		ANIME_V3(QualityTagsPreset.ANIME_V3, false, ImmutableSet.of(Image2ImageParameters.class, ImageVibeTransferParameters.class), ImageGenModel::estimateAnlasCostSDXL, null),
		@SerializedName("nai-diffusion-furry-3")
		FURRY_V3(QualityTagsPreset.FURRY_V3, false, ImmutableSet.of(Image2ImageParameters.class, ImageVibeTransferParameters.class), ImageGenModel::estimateAnlasCostSDXL, null),
		@SerializedName("nai-diffusion-4-curated-preview")
		ANIME_V4_CURATED(QualityTagsPreset.ANIME_V4_CURATED, false, ImmutableSet.of(Image2ImageParameters.class, V4MultiCharacterParameters.class), ImageGenModel::estimateAnlasCostSDXL, ImageGenModel::adaptForV4),
		@SerializedName("nai-diffusion-4-full")
		ANIME_V4_FULL(QualityTagsPreset.ANIME_V4_FULL, false, ImmutableSet.of(Image2ImageParameters.class, V4MultiCharacterParameters.class), ImageGenModel::estimateAnlasCostSDXL, ImageGenModel::adaptForV4),

		/**
		 * @deprecated This model doesn't exist in the NovelAI API anymore. Use a newer model.</br>
		 * This field will be removed in the future.
		 */
		@Deprecated
		@HardDeprecated
		@SerializedName("safe-diffusion-inpainting")
		ANIME_CURATED_INPAINT(QualityTagsPreset.V1_MODELS, true, ImmutableSet.of(Image2ImageParameters.class, ImageControlNetParameters.class), ImageGenModel::estimateAnlasCostSD, null),
		/**
		 * @deprecated This model doesn't exist in the NovelAI API anymore. Use a newer model.</br>
		 * This field will be removed in the future.
		 */
		@Deprecated
		@HardDeprecated
		@SerializedName("nai-diffusion-inpainting")
		ANIME_FULL_INPAINT(QualityTagsPreset.V1_MODELS, true, ImmutableSet.of(Image2ImageParameters.class, ImageControlNetParameters.class), ImageGenModel::estimateAnlasCostSD, null),
		/**
		 * @deprecated This model doesn't exist in the NovelAI API anymore. Use a newer model.</br>
		 * This field will be removed in the future.
		 */
		@Deprecated
		@HardDeprecated
		@SerializedName("furry-diffusion-inpainting")
		FURRY_INPAINT(QualityTagsPreset.V1_MODELS, true, ImmutableSet.of(Image2ImageParameters.class, ImageControlNetParameters.class), ImageGenModel::estimateAnlasCostSD, null),
		@SerializedName("nai-diffusion-3-inpainting")
		ANIME_V3_INPAINT(QualityTagsPreset.ANIME_V3, true, ImmutableSet.of(Image2ImageParameters.class, ImageVibeTransferParameters.class), ImageGenModel::estimateAnlasCostSDXL, null),
		@SerializedName("nai-diffusion-furry-3-inpainting")
		FURRY_V3_INPAINT(QualityTagsPreset.FURRY_V3, true, ImmutableSet.of(Image2ImageParameters.class, ImageVibeTransferParameters.class), ImageGenModel::estimateAnlasCostSDXL, null),
		@SerializedName("nai-diffusion-4-curated-inpainting")
		ANIME_V4_CURATED_INPAINT(QualityTagsPreset.ANIME_V4_CURATED, true, ImmutableSet.of(Image2ImageParameters.class, V4MultiCharacterParameters.class), ImageGenModel::estimateAnlasCostSDXL, ImageGenModel::adaptForV4),
		@SerializedName("nai-diffusion-4-full-inpainting")
		ANIME_V4_FULL_INPAINT(QualityTagsPreset.ANIME_V4_FULL, true, ImmutableSet.of(Image2ImageParameters.class, V4MultiCharacterParameters.class), ImageGenModel::estimateAnlasCostSDXL, ImageGenModel::adaptForV4);
		
		private final QualityTagsPreset qualityTagsPreset;
		private final boolean inpaintingModel;
		private final Set<Class<? extends AbstractExtraImageParameters>> supportedExtraParameterTypes;
		private final BiFunction<ImageParameters, List<AbstractExtraImageParameters>, Integer> anlasCostEstimator;
		@Getter(AccessLevel.PROTECTED)
		private final ImageRequestJsonAdapterFunc jsonAdapterFunc;

		public boolean doesModelSupportExtraParameter(AbstractExtraImageParameters extraImageParameter) {
			return doesModelSupportExtraParameterType(extraImageParameter.getClass());
		}
		public boolean doesModelSupportExtraParameterType(Class<? extends AbstractExtraImageParameters> extraParameterType) {
			return supportedExtraParameterTypes.contains(extraParameterType);
		}
		
		public int estimateAnlasCost(ImageParameters parameters) {
			return anlasCostEstimator.apply(parameters, new ArrayList<>(0));
		}
		public int estimateAnlasCost(ImageParameters parameters, AbstractExtraImageParameters... extraParameters) {
			return estimateAnlasCost(parameters, Arrays.asList(extraParameters));
		}
		public int estimateAnlasCost(ImageParameters parameters, List<AbstractExtraImageParameters> extraParameters) {
			if (parameters.getImgCount() == 0) { return 0; }
			
			return anlasCostEstimator.apply(parameters, extraParameters);
		}
		public int estimateAnlasCostIncludingSubscription(ImageParameters parameters, UserSubscription subscription) {
			if (isFreeGeneration(subscription, parameters.toBuilder().imgCount(1).build())) {
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
			
			return estimateAnlasCostFinalStep(perSample, parameters, extraParameters);
		}
		
		private static int estimateAnlasCostSDXL(ImageParameters parameters, List<AbstractExtraImageParameters> extraParameters) {
			double sizeComponent = parameters.getWidth() * parameters.getHeight();
			double smeaFactor = (parameters.isSmeaEnabled()&&parameters.isDynSmeaEnabled())?1.4: parameters.isSmeaEnabled()?1.2:1.0;
			if (extraParameters.stream().anyMatch(p->p instanceof Image2ImageParameters)) { smeaFactor = 1; }
	
			double perSample = Math.ceil(2951823174884865e-21 * sizeComponent + 5.753298233447344e-7 * sizeComponent * parameters.getSteps()) * smeaFactor;
			
			return estimateAnlasCostFinalStep(perSample, parameters, extraParameters);
		}
		
		private static int estimateAnlasCostFinalStep(double baseSampleFactor, ImageParameters parameters, List<AbstractExtraImageParameters> extraParameters) {
			double img2imgStrengthFactor = extraParameters.stream()
					.filter(p->p instanceof Image2ImageParameters)
					.findAny()
					.map(p->((Image2ImageParameters)p).getStrength())
					.orElse(1.0);
			double sampleFactor = Math.max(Math.ceil(baseSampleFactor * img2imgStrengthFactor), 2);
			if (parameters.getUcScale()!=1) { sampleFactor = Math.ceil(sampleFactor * 1.3); }
			
			return (int) (sampleFactor * parameters.getImgCount());
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
	@Builder.Default
 	private Map<Class<? extends AbstractExtraImageParameters>, AbstractExtraImageParameters> extraParameters = new HashMap<>();
	
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
		JsonObject mergedParameters = context.serialize(src.getParameters(), src.getParameters().getClass()).getAsJsonObject();
		src.getExtraParameters().entrySet().stream()
			.map(e->context.serialize(e.getValue(), e.getKey()))
			.map(o->o.getAsJsonObject().entrySet())
			.flatMap(Set::stream)
			.forEach(m->mergedParameters.add(m.getKey(), m.getValue()));
		wrapper.add("parameters", mergedParameters);
		
		if (src.getModel().hasJsonAdapterFunc()) {
			src.getModel().adaptJson(src, wrapper, context);
		}
		return wrapper;
	}

	public static boolean isFreeGeneration(UserSubscription subscriptionData, ImageParameters parameters) {
		if (!subscriptionData.getPerks().isUnlimitedImageGeneration()) { return false; }
		if (parameters.getSteps() > 28) { return false; }
		return Arrays.stream(subscriptionData.getPerks().getUnlimitedImageGenerationLimits())
			.filter(limit->limit.getMaxImages()>=parameters.getImgCount())
			.anyMatch(limit->limit.getResolution()>=parameters.getWidth()*parameters.getHeight());
	}
	
	public boolean isFreeGeneration(UserSubscription subscriptionData) {
		return isFreeGeneration(subscriptionData, parameters);
	}
	
	public static class ImageGenerationRequestBuilder {
		public ImageGenerationRequestBuilder model(ImageGenModel model) {
			AnnotationUtils.throwOrWarnAboutDepreciation(model, log);
			
			if (this.parameters != null 
					&& (this.parameters instanceof ImageInpaintParameters) != model.isInpaintingModel()) {
				if (this.parameters instanceof ImageInpaintParameters) {
					throw new IllegalArgumentException(String.format("model %s does not support inpainting", model));
				} else {
					throw new IllegalArgumentException(String.format("model %s is an inpainting model", model));
				}
			}
        	if (this.extraParameters$value != null) {
	        	Optional<AbstractExtraImageParameters> incompatibleExistingParameter = this.extraParameters$value.values().stream()
	        		.filter(p->!model.doesModelSupportExtraParameter(p))
	        		.findAny();
	        	if (incompatibleExistingParameter.isPresent()) {
	        		throw new IllegalArgumentException(String.format("Model type %s is not compatible with extraParameter type %s", model, incompatibleExistingParameter.getClass()));
	        	}
        	}
			
			this.model = model;
			return this;
		}
		
		public ImageGenerationRequestBuilder parameters(ImageParameters imageParameters) {
			if (imageParameters != null && imageParameters.getSeed() < 0) { throw new IllegalArgumentException("Negative seeds are not supported!"); }
			if (this.model != null 
					&& (imageParameters instanceof ImageInpaintParameters) != model.isInpaintingModel()) {
				if (imageParameters instanceof ImageInpaintParameters) {
					throw new IllegalArgumentException(String.format("model %s does not support inpainting", model));
				} else {
					throw new IllegalArgumentException(String.format("model %s is an inpainting model", model));
				}
			}
        	if (this.extraParameters$value != null) {
	        	Optional<AbstractExtraImageParameters> incompatibleExistingParameter = this.extraParameters$value.values().stream()
	        		.filter(p->!imageParameters.compatibleWith(p))
	        		.findAny();
	        	if (incompatibleExistingParameter.isPresent()) {
	        		throw new IllegalArgumentException(String.format("ImageParameter type %s is not compatible with extraParameter type %s", imageParameters.getClass(), incompatibleExistingParameter.getClass()));
	        	}
        	}
			
			this.parameters = imageParameters;
			return this;
		}
		
        private ImageGenerationRequestBuilder extraParameters(Map<Class<? extends AbstractExtraImageParameters>, AbstractExtraImageParameters> extraParameters) {
        	this.extraParameters$value = extraParameters;
        	this.extraParameters$set = true;
        	return this;
        }
        public ImageGenerationRequestBuilder extraParameter(AbstractExtraImageParameters extraParameter) {
        	if (model != null && !model.doesModelSupportExtraParameter(extraParameter)) {
        		throw new IllegalArgumentException(String.format("Model type %s is not compatible with extraParameter type %s", model, extraParameter.getClass()));
        	}
        	if (parameters != null && !parameters.compatibleWith(extraParameter)) {
        		throw new IllegalArgumentException(String.format("ImageParameter type %s is not compatible with extraParameter type %s", parameters.getClass(), extraParameter.getClass()));
        	}
        	if (this.extraParameters$value != null) {
	        	Optional<AbstractExtraImageParameters> incompatibleExistingParameter = this.extraParameters$value.values().stream()
	        		.filter(p->!p.compatibleWith(extraParameter))
	        		.findAny();
	        	if (incompatibleExistingParameter.isPresent()) {
	        		throw new IllegalArgumentException(String.format("AbstractExtraImageParameters type %s is not compatible with AbstractExtraImageParameters type %s", incompatibleExistingParameter.get().getClass(), extraParameter.getClass()));
	        	}
        	}
        	
        	if (!this.extraParameters$set) {
        		this.extraParameters$value = new HashMap<>();
        		this.extraParameters$set = true;
        	}
            this.extraParameters$value.put(extraParameter.getClass(), extraParameter);
            return this;
        }
	}
}
