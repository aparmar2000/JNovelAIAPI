package aparmar.nai.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.Getter;

class UnitTestGsonExcludeExclusionStrategy {
	private Gson gson;

	@BeforeEach
	void setUp() throws Exception {
		GsonBuilder builder = new GsonBuilder();
		builder.setExclusionStrategies(new GsonExcludeExclusionStrategy());
		gson = builder.create();
	}
	
	@Getter
	private static class ExcludeFieldTestClass {
		private boolean include = true;
		@GsonExclude
		private boolean exclude = true;
	}

	@Test
	void testThatFieldsAreCorrectlyExcludedWhenSerializing() {
		ExcludeFieldTestClass testInstance = new ExcludeFieldTestClass();
		JsonElement jsonTree = gson.toJsonTree(testInstance);
		
		assertNotNull(jsonTree);
		assertTrue(jsonTree.isJsonObject());
		JsonObject jsonTreeObj = jsonTree.getAsJsonObject();
		assertTrue(jsonTreeObj.has("include"));
		assertFalse(jsonTreeObj.has("exclude"));
	}

	@Test
	void testThatFieldsAreCorrectlyExcludedWhenDeserializing() {
		String testJson = "{ include:false, exclude:false }";
		ExcludeFieldTestClass testInstance = gson.fromJson(testJson, ExcludeFieldTestClass.class);
		
		assertNotNull(testInstance);
		assertFalse(testInstance.isInclude());
		assertTrue(testInstance.isExclude());
	}

}
