package aparmar.nai;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;

@Tag("integration")
abstract class AbstractFeatureIntegrationTest {
	protected static NAIAPI apiInstance;

	@BeforeAll
	static void setUp() throws Exception {
		Assumptions.assumeTrue(TestConstants.getTestAPIKey() != null);
		apiInstance = new NAIAPI(TestConstants.getTestAPIKey());
	}

}
