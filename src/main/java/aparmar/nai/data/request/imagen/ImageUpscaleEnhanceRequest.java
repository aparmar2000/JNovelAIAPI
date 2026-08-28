package aparmar.nai.data.request.imagen;

import java.lang.reflect.Type;

import com.google.common.primitives.ImmutableDoubleArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import aparmar.nai.data.request.Base64Image;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
public class ImageUpscaleEnhanceRequest implements JsonSerializer<ImageUpscaleEnhanceRequest> {
	public static final ImmutableDoubleArray VALID_BLUR_SIGMA_VALUES = ImmutableDoubleArray.of(0.0, 0.30, 0.35, 0.40, 0.45, 0.50);
	
	private Base64Image image;
	/**
	 * Rounded to one of [0.0, 0.30, 0.35, 0.40, 0.45, 0.50]
	 */
	@SerializedName("declared_blur_sigma")
	@Builder.Default private float declaredBlurSigma = 0;
	@Builder.Default private ImageGenModel model = ImageGenModel.V5_CURATED;
	
	public ImageUpscaleEnhanceRequest(Base64Image image, float declaredBlurSigma, ImageGenModel model) {
		validateModel(model);
		
		this.image = image;
		this.declaredBlurSigma = declaredBlurSigma;
		this.model = model;
	}
	
	@Override
	public JsonElement serialize(ImageUpscaleEnhanceRequest src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject wrapper = new JsonObject();
		wrapper.add("image", context.serialize(src.getImage(), Base64Image.class));
		wrapper.add("model", context.serialize(src.getModel(), ImageGenModel.class));
		
		double rawBlurSigma = src.getDeclaredBlurSigma();
		double roundedBlurSigma = VALID_BLUR_SIGMA_VALUES.get(0);
		for (int i=1; i<VALID_BLUR_SIGMA_VALUES.length(); i++) {
			double nextRoundedBlurSigma = VALID_BLUR_SIGMA_VALUES.get(i);
			if (Math.abs(rawBlurSigma-nextRoundedBlurSigma) < Math.abs(rawBlurSigma-roundedBlurSigma)) {
				roundedBlurSigma = nextRoundedBlurSigma;
			} else {
				break;
			}
		}
		wrapper.add("declared_blur_sigma", new JsonPrimitive(roundedBlurSigma));
		
		return wrapper;
	}
	
	public static class ImageUpscaleEnhanceRequestBuilder {
		public ImageUpscaleEnhanceRequestBuilder model(ImageGenModel model) {
			validateModel(model);
			this.model$set = true;
			this.model$value = model;
			return this;
		}
	}
	
	private static void validateModel(ImageGenModel model) throws IllegalArgumentException {
		if (!model.isStandaloneUpscalingModel()) {
			throw new IllegalArgumentException(String.format("An ImageUpscaleEnhanceRequest can only use a model that supports standalone upscaling - %s does not!", model.getClass()));
		}
	}

	// Values experimentally derived, may not be entirely accurate
	public int estimateAnlasCost() {
		long imagePixels = image.getTargetWidth() * image.getTargetHeight();
		if (imagePixels <= 1024*1024) {
			return 1;
		}
		if (imagePixels <= 1024*1664) {
			return 2;
		}
		if (imagePixels <= 1024*1664) {
			return 2;
		}
		if (imagePixels <= 1024*2368) {
			return 3;
		}
		return 4;
	}
}
