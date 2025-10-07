package aparmar.nai;

import java.util.Arrays;
import java.util.stream.Stream;

import aparmar.nai.data.request.imagen.Image2ImageParameters;
import aparmar.nai.data.request.imagen.ImageControlNetParameters;
import aparmar.nai.data.request.imagen.ImageVibeTransferParameters;
import aparmar.nai.data.request.imagen.V4MultiCharacterParameters;
import aparmar.nai.data.request.imagen.ImageGenerationRequest.ImageGenModel;
import aparmar.nai.utils.AnnotationUtils;

public class ImageGenTestHelpers {
	static Stream<ImageGenModel> getDeprecatedModels() {
		return Arrays.stream(ImageGenModel.values()).filter(m -> AnnotationUtils.isEnumValueDeprecated(m));
	}
	static Stream<ImageGenModel> getHardDeprecatedModels() {
		return Arrays.stream(ImageGenModel.values()).filter(m -> AnnotationUtils.isEnumValueHardDeprecated(m));
	}
	static Stream<ImageGenModel> getNonDeprecatedModels() {
		return Arrays.stream(ImageGenModel.values()).filter(m -> !AnnotationUtils.isEnumValueDeprecated(m));
	}
	
	static Stream<ImageGenModel> getNonInpaintingModels() {
		return getNonDeprecatedModels().filter(m->!m.isInpaintingModel());
	}
	static Stream<ImageGenModel> getInpaintingModels() {
		return getNonDeprecatedModels().filter(m->m.isInpaintingModel());
	}
	
	static Stream<ImageGenModel> getImg2ImgModels() {
		return getNonInpaintingModels().filter(m->m.doesModelSupportExtraParameterType(Image2ImageParameters.class));
	}
	static Stream<ImageGenModel> getControlNetModels() {
		return getNonInpaintingModels().filter(m->m.doesModelSupportExtraParameterType(ImageControlNetParameters.class));
	}
	static Stream<ImageGenModel> getVibeTransferModels() {
		return getNonInpaintingModels().filter(m->m.doesModelSupportExtraParameterType(ImageVibeTransferParameters.class));
	}
	static Stream<ImageGenModel> getVibeTransferInpaintingModels() {
		return getInpaintingModels().filter(m->m.doesModelSupportExtraParameterType(ImageVibeTransferParameters.class));
	}
	static Stream<ImageGenModel> getMultiCharacterModels() {
		return getNonInpaintingModels().filter(m->m.doesModelSupportExtraParameterType(V4MultiCharacterParameters.class));
	}
	
	static Stream<ImageGenModel> getNonImg2ImgModels() {
		return getNonInpaintingModels().filter(m->!m.doesModelSupportExtraParameterType(Image2ImageParameters.class));
	}
	static Stream<ImageGenModel> getNonControlNetModels() {
		return getNonInpaintingModels().filter(m->!m.doesModelSupportExtraParameterType(ImageControlNetParameters.class));
	}
	static Stream<ImageGenModel> getNonVibeTransferModels() {
		return getNonInpaintingModels().filter(m->!m.doesModelSupportExtraParameterType(ImageVibeTransferParameters.class));
	}
	static Stream<ImageGenModel> getNonMultiCharacterModels() {
		return getNonInpaintingModels().filter(m->!m.doesModelSupportExtraParameterType(V4MultiCharacterParameters.class));
	}
	
	
	static boolean hasControlNetModels() {
		return getControlNetModels().count() > 0;
	}
}
