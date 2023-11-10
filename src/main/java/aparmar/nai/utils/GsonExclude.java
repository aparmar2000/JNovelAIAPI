package aparmar.nai.utils;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that indicates this member should not be serialized to JSON.
 * Requires that the {@link aparmar.nai.utils.GsonExcludeExclusionStrategy} be applied.
 * 
 * @see aparmar.nai.utils.GsonExcludeExclusionStrategy
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface GsonExclude { }
