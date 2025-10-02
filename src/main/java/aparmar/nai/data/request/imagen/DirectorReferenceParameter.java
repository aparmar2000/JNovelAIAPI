package aparmar.nai.data.request.imagen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import aparmar.nai.data.request.Base64Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
public class DirectorReferenceParameter {
	public static enum DirectorReferenceCaption {
		@SerializedName("character")
		CHARACTER_NOT_STYLE,
		@SerializedName("character&style")
		CHARACTER_AND_STYLE;
	}
	
	private DirectorReferenceDescription description;
	private Base64Image referenceImage;
	@Builder.Default
	private float informationExtracted = 1;
	@Builder.Default
	private float strength = 1;
	
	public int getExtraCost() {
		if (getDescription() == null || getDescription().getCaption() == null || getDescription().getCaption().getBaseCaption() == null) { return 0; }
		
		switch (getDescription().getCaption().getBaseCaption()) {
		case CHARACTER_AND_STYLE:
		case CHARACTER_NOT_STYLE:
			return 5;
		default:
			return 0;
		}
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
	
	public static DirectorReferenceParameterBuilder characterAndStyleReferenceBuilder() {
		return DirectorReferenceParameter.builder()
				.description(new DirectorReferenceDescription(DirectorReferenceCaption.CHARACTER_AND_STYLE, false));
	}
	public static DirectorReferenceParameter characterAndStyleReference(@NonNull Base64Image referenceImage) {
		return characterAndStyleReferenceBuilder()
				.referenceImage(referenceImage)
				.build();
	}
	
	public static DirectorReferenceParameterBuilder characterNotStyleReferenceBuilder() {
		return DirectorReferenceParameter.builder()
				.description(new DirectorReferenceDescription(DirectorReferenceCaption.CHARACTER_NOT_STYLE, false));
	}
	public static DirectorReferenceParameter characterNotStyleReference(@NonNull Base64Image referenceImage) {
		return characterNotStyleReferenceBuilder()
				.referenceImage(referenceImage)
				.build();
	}
}
