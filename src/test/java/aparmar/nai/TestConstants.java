package aparmar.nai;

public class TestConstants {
	public static String TEST_IMAGE_FOLDER = "test-images/";
	
	private static String TestAPIKey = null;
	private static synchronized void loadTestAPIKey() {
		try {
			TestAPIKey = System.getenv("TEST_API_KEY");
			if (TestAPIKey==null || TestAPIKey.isEmpty()) { TestAPIKey = null; }
		} catch (Exception e) {
			TestAPIKey = null;
			e.printStackTrace();
		}
	}
	public static synchronized String getTestAPIKey() {
		if (TestAPIKey == null) { loadTestAPIKey(); }
		
		return TestAPIKey;
	}
}
