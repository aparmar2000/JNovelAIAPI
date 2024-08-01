package aparmar.nai.data.request.imgaug;

import aparmar.nai.data.request.Base64Image;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImageAugmentEmotionRequest extends ImageAugmentRequestSingleResult {
	private static final String EMOTION_DELIMITER = ";; ";
	
	@RequiredArgsConstructor
	public enum EmotionStrength {
		NORMAL(DefryFactor.ZERO),
		SLIGHTLY_WEAK(DefryFactor.ONE),
		WEAK(DefryFactor.TWO),
		EVEN_WEAKER(DefryFactor.THREE),
		VERY_WEAK(DefryFactor.FOUR),
		WEAKEST(DefryFactor.FIVE);
		
		@Getter
		private final DefryFactor defryFactor;
	}
	
	@RequiredArgsConstructor
	public enum Emotion {
		NEUTRAL("neutral"),
		HAPPY("happy"),
		SAD("sad"),
		AROUSED("aroused"),
		THINKING("thinking"),
		CONFUSED("confused"),
		WORRIED("worried"),
		ANGRY("angry"),
		SCARED("scared"),
		SURPRISED("surprised"),
		EMBARRASSED("embarrassed"),
		SHY("shy"),
		DISGUSTED("disgusted"),
		LAUGHING("laughing"),
		TIRED("tired"),
		EXCITED("excited"),
		NERVOUS("nervous"),
		LOVE("love"),
		SMUG("smug"),
		BORED("bored"),
		IRRITATED("irritated"),
		DETERMINED("determined"),
		PLAYFUL("playful"),
		HURT("hurt");
		
		@Getter
		private final String promptText;
	}

	@Builder
	public ImageAugmentEmotionRequest(EmotionStrength emotionStrength, @NonNull Emotion emotion, String prompt, @NonNull Base64Image image) {
		super(
			emotionStrength != null ? emotionStrength.getDefryFactor() : DefryFactor.ZERO, 
			emotion.getPromptText()+EMOTION_DELIMITER+(prompt!=null?prompt:""),
			image.getTargetHeight(), 
			image.getTargetWidth(), 
			image, 
			RequestType.EMOTION);
	}

}
