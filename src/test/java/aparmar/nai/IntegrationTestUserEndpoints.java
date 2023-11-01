package aparmar.nai;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IntegrationTestUserEndpoints {
	private NAIAPI apiInstance;

	@BeforeEach
	public void setUp() throws Exception {
		Assumptions.assumeTrue(TestConstants.getTestAPIKey() != null);
		apiInstance = new NAIAPI(TestConstants.getTestAPIKey());
	}

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
