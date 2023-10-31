package main.java.aparmar.nai.data.response;

import com.google.gson.JsonElement;

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
	private int tier;
	private boolean active;
	private long expiresAt;
	private SubscriptionPerks perks;
	@EqualsAndHashCode.Exclude private JsonElement paymentProcessorData;
	private SubscriptionTrainingSteps trainingStepsLeft;

	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	@Getter
	@ToString
	public static class SubscriptionPerks {
		private int maxPriorityActions, startPriority;
		private int contextTokens;
		private boolean unlimitedMaxPriority;
		private int moduleTrainingSteps;
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
