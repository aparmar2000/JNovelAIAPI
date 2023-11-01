package aparmar.nai.data.response;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonNull;

import aparmar.nai.TestHelpers;
import aparmar.nai.data.response.UserSubscription.ImageGenerationLimit;
import aparmar.nai.data.response.UserSubscription.SubscriptionPerks;
import aparmar.nai.data.response.UserSubscription.SubscriptionTier;
import aparmar.nai.data.response.UserSubscription.SubscriptionTrainingSteps;

class UnitTestUserSubscription {

	@Test
	void testUserSubscriptionDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		UserSubscription testInstance1 = new UserSubscription();
		UserSubscription testInstance2 = new UserSubscription(
				SubscriptionTier.TABLET,
				true,
				1000000,
				new SubscriptionPerks(),
				JsonNull.INSTANCE,
				new SubscriptionTrainingSteps(),
				1
				);
		
		TestHelpers.autoTestDataAndToBuilderAnnotation(UserSubscription.class, testInstance1, testInstance2);
	}

	@Test
	void testSubscriptionPerksDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SubscriptionPerks testInstance1 = new SubscriptionPerks();
		SubscriptionPerks testInstance2 = new SubscriptionPerks(
				-1,
				99,
				8192,
				true,
				true,
				true,
				true,
				new ImageGenerationLimit[0], 
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

}
