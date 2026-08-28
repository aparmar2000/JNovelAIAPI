package aparmar.nai.data.response;

import java.util.Optional;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import aparmar.nai.utils.GsonExclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class UserSubscription {
	@Getter
	@AllArgsConstructor
	@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
	public enum SubscriptionTier {
		@SerializedName("0")
		NONE(false),
		@SerializedName("1")
		TABLET(true),
		@SerializedName("2")
		SCROLL(true),
		@SerializedName("3")
		OPUS(new ImageGenerationLimit(1, 1024*1024));
		
		private SubscriptionTier(boolean generationEnabled) {
			voiceGeneration = generationEnabled;
			imageGeneration = generationEnabled;
			unlimitedImageGeneration = false;
			unlimitedImageGenerationLimits = new ImageGenerationLimit[0];
		}
		private SubscriptionTier(ImageGenerationLimit...imageGenerationLimits) {
			voiceGeneration = true;
			imageGeneration = true;
			unlimitedImageGeneration = true;
			unlimitedImageGenerationLimits = imageGenerationLimits;
		}
		
		boolean voiceGeneration;
		boolean imageGeneration;
		boolean unlimitedImageGeneration;
		ImageGenerationLimit[] unlimitedImageGenerationLimits;
	}
	
	private SubscriptionTier tier;
	private boolean active;
	private long expiresAt;
	@SerializedName("perks")
	private InternalSubscriptionPerks perksInternal;
	private String paymentProcessor;
	/** Not returned from the endpoint when using a persistent key, for privacy reasons. */
	@EqualsAndHashCode.Exclude private JsonElement paymentProcessorData;
	private boolean isPaypal;
	private SubscriptionTrainingSteps trainingStepsLeft;
	private int accountType;
	
	private boolean isGracePeriod;
	private UsageLimitStatus usage;
	

	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	@Getter
	@ToString
	public static class InternalSubscriptionPerks {
		private int maxPriorityActions, startPriority;
		private int moduleTrainingSteps;
		private boolean unlimitedMaxPriority;
		private int contextTokens;
	}
	@AllArgsConstructor(access = AccessLevel.PACKAGE)
	@EqualsAndHashCode
	@Getter
	@ToString
	public static class SubscriptionPerks {
		@Getter(value = AccessLevel.PROTECTED)
		@Nullable
		private SubscriptionTier subscriptionTier;
		
		private int maxPriorityActions, startPriority;
		private int moduleTrainingSteps;
		private boolean unlimitedMaxPriority;
		@Getter(value = AccessLevel.PROTECTED) private Optional<Boolean> voiceGeneration = Optional.empty();
		@Getter(value = AccessLevel.PROTECTED) private Optional<Boolean> imageGeneration = Optional.empty();
		@Getter(value = AccessLevel.PROTECTED) private Optional<Boolean> unlimitedImageGeneration = Optional.empty();
		private Optional<ImageGenerationLimit[]> unlimitedImageGenerationLimits = Optional.empty();
		private int contextTokens;
		
		/**
		 * @deprecated since 5.7.0<br/>
		 * Legacy constructor - you probably shouldn't have been constructing instances of this class, but if you were this constructor should maintain expected behavior.
		 * <br/>
		 * If you're writing new code, carefully consider why you're constructing an instance of this class.
		 */
		@Deprecated
		public SubscriptionPerks(int maxPriorityActions, int startPriority, int moduleTrainingSteps, boolean unlimitedMaxPriority, 
				boolean voiceGeneration, boolean imageGeneration, boolean unlimitedImageGeneration, 
				ImageGenerationLimit[] unlimitedImageGenerationLimits, int contextTokens) {
			this.maxPriorityActions = maxPriorityActions;
			this.startPriority = startPriority;
			this.moduleTrainingSteps = moduleTrainingSteps;
			this.unlimitedMaxPriority = unlimitedMaxPriority;
			this.voiceGeneration = Optional.of(voiceGeneration);
			this.imageGeneration = Optional.of(imageGeneration);
			this.unlimitedImageGeneration = Optional.of(unlimitedImageGeneration);
			this.unlimitedImageGenerationLimits = Optional.of(unlimitedImageGenerationLimits);
			this.contextTokens = contextTokens;
		}
		
		SubscriptionPerks(InternalSubscriptionPerks internalSubscriptionPerks, SubscriptionTier subscriptionTier) {
			this.subscriptionTier = subscriptionTier;
			maxPriorityActions = internalSubscriptionPerks != null ? internalSubscriptionPerks.getMaxPriorityActions() : 0;
			startPriority = internalSubscriptionPerks != null ? internalSubscriptionPerks.getStartPriority() : 0;
			moduleTrainingSteps = internalSubscriptionPerks != null ? internalSubscriptionPerks.getModuleTrainingSteps() : 0;
			unlimitedMaxPriority = internalSubscriptionPerks != null ? internalSubscriptionPerks.isUnlimitedMaxPriority() : false;
			contextTokens = internalSubscriptionPerks != null ? internalSubscriptionPerks.getContextTokens() : 0;
		}
		
		/**
		 * Underlying field no longer exists - this is guessed from the subscription tier.
		 */
		@EqualsAndHashCode.Include(replaces = "voiceGeneration")
		public boolean isVoiceGeneration() {
			return voiceGeneration.orElse(subscriptionTier != null ? subscriptionTier.isVoiceGeneration() : false);
		}
		
		/**
		 * Underlying field no longer exists - this is guessed from the subscription tier.
		 */
		@EqualsAndHashCode.Include(replaces = "imageGeneration")
		public boolean isImageGeneration() {
			return imageGeneration.orElse(subscriptionTier != null ? subscriptionTier.isImageGeneration() : false);
		}
		
		/**
		 * Underlying field no longer exists - this is guessed from the subscription tier.
		 */
		@EqualsAndHashCode.Include(replaces = "unlimitedImageGeneration")
		public boolean isUnlimitedImageGeneration() {
			return unlimitedImageGeneration.orElse(subscriptionTier != null ? subscriptionTier.isUnlimitedImageGeneration() : false);
		}
		
		/**
		 * Underlying field no longer exists - this is guessed from the subscription tier.
		 */
		@EqualsAndHashCode.Include(replaces = "unlimitedImageGenerationLimits")
		public ImageGenerationLimit[] getUnlimitedImageGenerationLimits() {
			return unlimitedImageGenerationLimits.orElse(subscriptionTier != null ? subscriptionTier.getUnlimitedImageGenerationLimits() : new ImageGenerationLimit[0]);
		}
	}
	
	@GsonExclude
	@EqualsAndHashCode.Exclude @Getter(lazy=true) private final SubscriptionPerks perks = generateSubscriptionPerks();
	
	private SubscriptionPerks generateSubscriptionPerks() {
		return new SubscriptionPerks(perksInternal, tier);
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
	
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	@Getter
	@ToString
	public static class UsageLimitStatus {
		private boolean isNegative;
		private int percent;
		/**
		 * Time in seconds until +1% usage limit recharge.
		 */
		private long timeUntilNextPercent;
	}
}
