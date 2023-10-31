package test.java.aparmar.nai;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeNotNull;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.aparmar.nai.NAIAPI;

public class IntegrationTestUserEndpoints {
	private NAIAPI apiInstance;

	@BeforeEach
	public void setUp() throws Exception {
		assumeNotNull(TestConstants.getTestAPIKey());
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
