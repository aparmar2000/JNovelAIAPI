package test.java.aparmar.nai;

public class TestConstants {
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
