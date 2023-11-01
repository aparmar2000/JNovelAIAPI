package aparmar.nai.data.response;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class UserSubscription {
	public enum SubscriptionTier {
		@SerializedName("0")
		NONE,
		@SerializedName("1")
		TABLET,
		@SerializedName("2")
		SCROLL,
		@SerializedName("3")
		OPUS;
	}
	
	private SubscriptionTier tier;
	private boolean active;
	private long expiresAt;
	private SubscriptionPerks perks;
	@EqualsAndHashCode.Exclude private JsonElement paymentProcessorData;
	private SubscriptionTrainingSteps trainingStepsLeft;
	private int accountType;

	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	@Getter
	@ToString
	public static class SubscriptionPerks {
		private int maxPriorityActions, startPriority;
		private int moduleTrainingSteps;
		private boolean unlimitedMaxPriority;
		private boolean voiceGeneration;
		private boolean imageGeneration;
		private boolean unlimitedImageGeneration;
		private ImageGenerationLimit[] unlimitedImageGenerationLimits;
		private int contextTokens;
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	@Getter
	@ToString
	public static class ImageGenerationLimit {
		@SerializedName("maxPrompts")
		private int maxImages;
		private long resolution;
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	@Getter
	@ToString
	public static class SubscriptionTrainingSteps {
		private int fixedTrainingStepsLeft, purchasedTrainingSteps;
	}
}
