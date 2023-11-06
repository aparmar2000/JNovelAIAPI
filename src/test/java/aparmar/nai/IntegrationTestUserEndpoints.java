package aparmar.nai;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class IntegrationTestUserEndpoints extends AbstractFeatureIntegrationTest {

	@Test
	public void testFetchUserInformation() throws AssertionError, Exception {
		TestHelpers.runTestToleratingTimeouts(2, 1000, ()->{
			assertNotNull(apiInstance.fetchUserInformation());
		});
	}

	@Test
	public void testFetchUserPriority() throws AssertionError, Exception {
		TestHelpers.runTestToleratingTimeouts(2, 1000, ()->{
			assertNotNull(apiInstance.fetchUserPriority());
		});
	}

	@Test
	public void testFetchUserSubscription() throws AssertionError, Exception {
		TestHelpers.runTestToleratingTimeouts(2, 1000, ()->{
			assertNotNull(apiInstance.fetchUserSubscription());
		});
	}

	@Test
	public void testFetchUserData() throws AssertionError, Exception {
		TestHelpers.runTestToleratingTimeouts(2, 1000, ()->{
			assertNotNull(apiInstance.fetchUserData());
		});
	}

}
