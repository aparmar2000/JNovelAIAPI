package aparmar.nai.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * An {@link com.google.gson.ExclusionStrategy} that filters out fields annotated with {@link com.google.gson.ExclusionStrategy}.
 * 
 * @see com.google.gson.ExclusionStrategy
 */
public class GsonExcludeExclusionStrategy implements ExclusionStrategy {

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		return f.getAnnotation(GsonExclude.class) != null;
	}

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}

}
