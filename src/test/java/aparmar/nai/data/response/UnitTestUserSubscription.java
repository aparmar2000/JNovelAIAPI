package aparmar.nai.data.response;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonNull;

import aparmar.nai.TestHelpers;
import aparmar.nai.data.response.UserSubscription.ImageGenerationLimit;
import aparmar.nai.data.response.UserSubscription.InternalSubscriptionPerks;
import aparmar.nai.data.response.UserSubscription.SubscriptionPerks;
import aparmar.nai.data.response.UserSubscription.SubscriptionTier;
import aparmar.nai.data.response.UserSubscription.SubscriptionTrainingSteps;
import aparmar.nai.data.response.UserSubscription.UsageLimitStatus;

class UnitTestUserSubscription {

	@Test
	void testUserSubscriptionDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		UserSubscription testInstance1 = new UserSubscription();
		UserSubscription testInstance2 = new UserSubscription(
				SubscriptionTier.TABLET,
				true,
				1000000,
				new InternalSubscriptionPerks(),
				"Dummy",
				JsonNull.INSTANCE,
				true,
				new SubscriptionTrainingSteps(),
				1, 
				true,
				new UsageLimitStatus()
				);
		
		TestHelpers.autoTestDataAndToBuilderAnnotation(UserSubscription.class, testInstance1, testInstance2);
	}

	@Test
	void testInternalSubscriptionPerksDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		InternalSubscriptionPerks testInstance1 = new InternalSubscriptionPerks();
		InternalSubscriptionPerks testInstance2 = new InternalSubscriptionPerks(
				-1,
				99,
				8192,
				true,
				100000
				);
		
		TestHelpers.autoTestDataAndToBuilderAnnotation(InternalSubscriptionPerks.class, testInstance1, testInstance2);
	}

	@Test
	void testSubscriptionPerksDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SubscriptionPerks testInstance1 = new SubscriptionPerks(
				null,
				0,
				0,
				0,
				false,
				Optional.of(false),
				Optional.of(false),
				Optional.of(false),
				Optional.of(new ImageGenerationLimit[0]),
				0
				);
		SubscriptionPerks testInstance2 = new SubscriptionPerks(
				SubscriptionTier.OPUS,
				-1,
				99,
				8192,
				true,
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(), 
				100000
				);
		
		TestHelpers.autoTestDataAndToBuilderAnnotation(SubscriptionPerks.class, testInstance1, testInstance2);
	}

	@Test
	void testImageGenerationLimitDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ImageGenerationLimit testInstance1 = new ImageGenerationLimit();
		ImageGenerationLimit testInstance2 = new ImageGenerationLimit(
				1,
				1024*1024
				);
		
		TestHelpers.autoTestDataAndToBuilderAnnotation(ImageGenerationLimit.class, testInstance1, testInstance2);
	}

	@Test
	void testSubscriptionTrainingStepsDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SubscriptionTrainingSteps testInstance1 = new SubscriptionTrainingSteps();
		SubscriptionTrainingSteps testInstance2 = new SubscriptionTrainingSteps(
				100,
				4224
				);
		
		TestHelpers.autoTestDataAndToBuilderAnnotation(SubscriptionTrainingSteps.class, testInstance1, testInstance2);
	}

	@Test
	void testUsageLimitStatusDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		UsageLimitStatus testInstance1 = new UsageLimitStatus();
		UsageLimitStatus testInstance2 = new UsageLimitStatus(
				true,
				-1,
				14
				);
		
		TestHelpers.autoTestDataAndToBuilderAnnotation(UsageLimitStatus.class, testInstance1, testInstance2);
	}

}
