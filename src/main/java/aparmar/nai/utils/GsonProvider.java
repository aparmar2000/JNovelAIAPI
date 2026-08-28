package aparmar.nai.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import aparmar.nai.data.request.Base64Image;
import aparmar.nai.data.request.ImageAnnotateRequest;
import aparmar.nai.data.request.ImageUpscaleRequest;
import aparmar.nai.data.request.ImageUpscaleRequest.UpscaleFactor;
import aparmar.nai.data.request.imagen.DirectorReferenceParameters;
import aparmar.nai.data.request.imagen.ImageGenerationRequest;
import aparmar.nai.data.request.imagen.ImageUpscaleEnhanceRequest;
import aparmar.nai.data.request.imagen.ImageVibeTransferParameters;
import aparmar.nai.data.request.imagen.MultiCharacterParameters.CharacterPrompt;
import aparmar.nai.data.request.imagen.V4MultiCharacterParameters;
import aparmar.nai.data.request.imgaug.ImageAugmentRequest.DefryFactor;
import aparmar.nai.data.request.textgen.TextGenerationParameters.SamplingStep;
import aparmar.nai.data.response.TextGenerationResponse.LogProb;
import aparmar.nai.utils.serialization.SamplingStepDeserializer;

@SuppressWarnings("deprecation")
public class GsonProvider {
	public static Gson buildGsonInstance() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		
		gsonBuilder.setExclusionStrategies(new GsonExcludeExclusionStrategy());
		gsonBuilder.registerTypeAdapter(LogProb.class, new LogProb());
		gsonBuilder.registerTypeAdapter(Base64Image.class, new Base64Image());
		gsonBuilder.registerTypeAdapter(UpscaleFactor.class, UpscaleFactor.TWO);
		gsonBuilder.registerTypeAdapter(ImageUpscaleRequest.class, new ImageUpscaleRequest());
		gsonBuilder.registerTypeAdapter(ImageUpscaleEnhanceRequest.class, new ImageUpscaleEnhanceRequest());
		gsonBuilder.registerTypeAdapter(ImageAnnotateRequest.class, ImageAnnotateRequest.SERIALIZER_INSTANCE);
		gsonBuilder.registerTypeAdapter(ImageGenerationRequest.class, new ImageGenerationRequest());
		gsonBuilder.registerTypeAdapter(ImageVibeTransferParameters.class, new ImageVibeTransferParameters());
		gsonBuilder.registerTypeAdapter(DefryFactor.class, DefryFactor.ZERO);
		gsonBuilder.registerTypeAdapter(CharacterPrompt.class, new CharacterPrompt());
		gsonBuilder.registerTypeAdapter(V4MultiCharacterParameters.class, new V4MultiCharacterParameters());
		gsonBuilder.registerTypeAdapter(DirectorReferenceParameters.class, new DirectorReferenceParameters());
		gsonBuilder.registerTypeAdapter(SamplingStep.class, new SamplingStepDeserializer());
		
		return gsonBuilder.create();
	}
}
