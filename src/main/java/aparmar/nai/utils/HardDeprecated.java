package aparmar.nai.utils;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A program element annotated &#64;HardDeprecated is one that programmers
 * are strongly discouraged from using. Usage may cause {@link HardDeprecationException} to be thrown.
 */
@Documented
@Retention(RUNTIME)
@Target({ CONSTRUCTOR, FIELD, LOCAL_VARIABLE, METHOD, PACKAGE, PARAMETER, TYPE })
public @interface HardDeprecated {

}
