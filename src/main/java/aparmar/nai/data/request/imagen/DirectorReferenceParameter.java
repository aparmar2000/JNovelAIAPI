package aparmar.nai.data.request.imagen;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.google.gson.annotations.SerializedName;

import aparmar.nai.data.request.Base64Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
public class DirectorReferenceParameter {
	@RequiredArgsConstructor
	public static enum DirectorReferenceCaption {
		@SerializedName("character")
		CHARACTER_NOT_STYLE(aspectMatchingScalingPreprocessor(Base64Image.ScalingMode.PAD_BLACK, new int[] {1024, 1536}, new int[] {1536, 1024}, new int[] {1472, 1472} )),
		@SerializedName("character&style")
		CHARACTER_AND_STYLE(aspectMatchingScalingPreprocessor(Base64Image.ScalingMode.PAD_BLACK, new int[] {1024, 1536}, new int[] {1536, 1024}, new int[] {1472, 1472} )),
		@SerializedName("style")
		STYLE_NOT_CHARACTER(aspectMatchingScalingPreprocessor(Base64Image.ScalingMode.PAD_BLACK, new int[] {1024, 1536}, new int[] {1536, 1024}, new int[] {1472, 1472} ));
		
		private final Function<BufferedImage, Base64Image> imagePreprocessor;
		
		public Base64Image applyPreprocessor(BufferedImage refImage) {
			return imagePreprocessor.apply(refImage);
		}
		public Base64Image applyPreprocessor(Base64Image refImage) {
			return applyPreprocessor(refImage.getProcessedImage());
		}
		
		private static Function<BufferedImage, Base64Image> aspectMatchingScalingPreprocessor(final Base64Image.ScalingMode scalingMode, final int[]... validDimensions) {
			return (refImage) -> {
				double refAspectRatio = (double) refImage.getWidth() / refImage.getHeight();
				
				int targetWidth = 0;
				int targetHeight = 0;
				double minAspectRatioDiff = Double.MAX_VALUE;
		
				for (int[] dim : validDimensions) {
					int currentWidth = dim[0];
					int currentHeight = dim[1];
					double currentAspectRatio = (double) currentWidth / currentHeight;
					
					double diff = Math.abs(refAspectRatio - currentAspectRatio);
					
					if (diff < minAspectRatioDiff) {
						minAspectRatioDiff = diff;
						targetWidth = currentWidth;
						targetHeight = currentHeight;
					}
				}
				
				return new Base64Image(refImage, targetWidth, targetHeight, Base64Image.ScalingMode.PAD_BLACK, false);
			};
		}
	}
	
	private DirectorReferenceDescription description;
	private Base64Image referenceImage;
	@Builder.Default
	private float informationExtracted = 1;
	@Builder.Default
	private float secondaryStrength = 1;
	@Builder.Default
	private float strength = 1;
	
	public int getExtraCost() {
		if (getDescription() == null || getDescription().getCaption() == null || getDescription().getCaption().getBaseCaption() == null) { return 0; }
		
		switch (getDescription().getCaption().getBaseCaption()) {
		case CHARACTER_AND_STYLE:
		case CHARACTER_NOT_STYLE:
		case STYLE_NOT_CHARACTER:
			return 5;
		default:
			return 0;
		}
	}
	
	public Base64Image getPreprocessedReferenceImage() {
		if (getDescription() == null || getDescription().getCaption() == null || getDescription().getCaption().getBaseCaption() == null) {
			return referenceImage;
		}
		DirectorReferenceCaption baseCaption = getDescription().getCaption().getBaseCaption();
		return baseCaption.applyPreprocessor(referenceImage);
	}

	@Data
	@AllArgsConstructor
	@SuperBuilder(toBuilder = true)
	@EqualsAndHashCode
	public static class DirectorReferenceDescription {
		private DirectorReferenceDescriptionCaption caption;
		@SerializedName("legacy_uc")
		private boolean legacyUC;
		
		public DirectorReferenceDescription(DirectorReferenceCaption baseCaption, boolean legacyUC) {
			this.caption = new DirectorReferenceDescriptionCaption(baseCaption);
			this.legacyUC = legacyUC;
		}
		
		public DirectorReferenceDescription(DirectorReferenceCaption baseCaption, boolean legacyUC, String... charCaptions) {
			this.caption = new DirectorReferenceDescriptionCaption(baseCaption, new ArrayList<>(Arrays.asList(charCaptions)));
			this.legacyUC = legacyUC;
		}
		
		public DirectorReferenceDescription(DirectorReferenceCaption baseCaption, boolean legacyUC, List<String> charCaptions) {
			this.caption = new DirectorReferenceDescriptionCaption(baseCaption, charCaptions);
			this.legacyUC = legacyUC;
		}
	}

	@Data
	@AllArgsConstructor
	@SuperBuilder(toBuilder = true)
	@EqualsAndHashCode
	public static class DirectorReferenceDescriptionCaption {
		@SerializedName("base_caption")
		private DirectorReferenceCaption baseCaption;
		
		@SerializedName("char_captions")
		@Singular
		private List<String> charCaptions;
		
		public DirectorReferenceDescriptionCaption(DirectorReferenceCaption baseCaption) {
			this.baseCaption = baseCaption;
			this.charCaptions = new ArrayList<>();
		}
	}
	
	public static DirectorReferenceParameterBuilder directorReferenceBuilder(@NonNull DirectorReferenceCaption referenceType) {
		return DirectorReferenceParameter.builder()
				.description(new DirectorReferenceDescription(referenceType, false));
	}
	public static DirectorReferenceParameter directorReference(@NonNull DirectorReferenceCaption referenceType, @NonNull Base64Image referenceImage) {
		return directorReferenceBuilder(referenceType)
				.referenceImage(referenceImage)
				.build();
	}
	
	public static DirectorReferenceParameterBuilder characterAndStyleReferenceBuilder() {
		return directorReferenceBuilder(DirectorReferenceCaption.CHARACTER_AND_STYLE);
	}
	public static DirectorReferenceParameter characterAndStyleReference(@NonNull Base64Image referenceImage) {
		return characterAndStyleReferenceBuilder()
				.referenceImage(referenceImage)
				.build();
	}
	
	/**
	 * @deprecated since 5.5.0, use {@link DirectorReferenceParameter#characterReferenceBuilder()} instead.
	 */
	@Deprecated
	public static DirectorReferenceParameterBuilder characterNotStyleReferenceBuilder() {
		return directorReferenceBuilder(DirectorReferenceCaption.CHARACTER_NOT_STYLE);
	}
	/**
	 * @deprecated since 5.5.0, use {@link DirectorReferenceParameter#characterReference()} instead.
	 */
	@Deprecated
	public static DirectorReferenceParameter characterNotStyleReference(@NonNull Base64Image referenceImage) {
		return characterNotStyleReferenceBuilder()
				.referenceImage(referenceImage)
				.build();
	}
	
	public static DirectorReferenceParameterBuilder characterReferenceBuilder() {
		return characterNotStyleReferenceBuilder();
	}
	public static DirectorReferenceParameter characterReference(@NonNull Base64Image referenceImage) {
		return characterNotStyleReference(referenceImage);
	}
	
	public static DirectorReferenceParameterBuilder styleReferenceBuilder() {
		return directorReferenceBuilder(DirectorReferenceCaption.STYLE_NOT_CHARACTER);
	}
	public static DirectorReferenceParameter styleReference(@NonNull Base64Image referenceImage) {
		return styleReferenceBuilder()
				.referenceImage(referenceImage)
				.build();
	}
}
