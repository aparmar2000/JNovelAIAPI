package aparmar.nai.data.response;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

import aparmar.nai.TestHelpers;

class UnitTestUserData {

	@Test
	void testUserDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		UserData testInstance1 = new UserData();
		UserData testInstance2 = new UserData(
				new UserPriority(),
				new UserSubscription(),
				new UserKeystore(),
				"Settings",
				new UserInfo()
				);
		
		TestHelpers.autoTestDataAndToBuilderAnnotation(UserData.class, testInstance1, testInstance2);
	}

}
