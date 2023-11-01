package aparmar.nai;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class IntegrationTestUserEndpoints extends AbstractFeatureIntegrationTest {

	@Test
	public void testFetchUserInformation() throws IOException {
		assertNotNull(apiInstance.fetchUserInformation());
	}

	@Test
	public void testFetchUserPriority() throws IOException {
		assertNotNull(apiInstance.fetchUserPriority());
	}

	@Test
	public void testFetchUserSubscription() throws IOException {
		assertNotNull(apiInstance.fetchUserSubscription());
	}

	@Test
	public void testFetchUserData() throws IOException {
		assertNotNull(apiInstance.fetchUserData());
	}

}
