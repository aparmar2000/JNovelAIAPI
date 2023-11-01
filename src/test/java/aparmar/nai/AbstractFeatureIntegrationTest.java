package aparmar.nai;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

@Tag("integration")
abstract class AbstractFeatureIntegrationTest {
	protected NAIAPI apiInstance;

	@BeforeEach
	void setUp() throws Exception {
		Assumptions.assumeTrue(TestConstants.getTestAPIKey() != null);
		apiInstance = new NAIAPI(TestConstants.getTestAPIKey());
	}

}
