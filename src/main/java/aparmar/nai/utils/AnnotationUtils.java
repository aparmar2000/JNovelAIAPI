package aparmar.nai.utils;

import java.util.function.Function;

import org.slf4j.Logger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

public class AnnotationUtils {

	@RequiredArgsConstructor
	@Getter
	public static enum DeprecationLevel {
		NONE(false),
		NORMAL(true),
		HARD(true);

		private final boolean isDeprecation;
	}

	/**
	 * Checks the <code>DeprecationLevel</code> of an <code>Enum</code> value
	 * @see DeprecationLevel
	 * @see HardDeprecated &#64;HardDepreciated
	 */
	public static DeprecationLevel getEnumValueDeprecationLevel(Enum<?> value) {
		if (value==null) { return DeprecationLevel.NONE; }
		try {
			val enumValueField = value.getClass().getField(value.name());
			if (enumValueField.isAnnotationPresent(HardDeprecated.class)) { return DeprecationLevel.HARD; }
			if (enumValueField.isAnnotationPresent(Deprecated.class)) { return DeprecationLevel.NORMAL; }
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		return DeprecationLevel.NONE;
	}
	/**
	 * Checks if an <code>Enum</code> value is depreciated
	 * @see getEnumValueDepreciationLevel
	 */
	public static boolean isEnumValueDeprecated(Enum<?> value) {
		return getEnumValueDeprecationLevel(value).isDeprecation();
	}
	/**
	 * Checks if an <code>Enum</code> value is hard depreciated
	 * @see getEnumValueDepreciationLevel
	 * @see HardDeprecated &#64;HardDepreciated
	 */
	public static boolean isEnumValueHardDeprecated(Enum<?> value) {
		return getEnumValueDeprecationLevel(value) == DeprecationLevel.HARD;
	}

	/**
	 * Takes a generic <code>value</code> and a <code>Function</code> that provides the <code>DeprecationLevel</code> for that <code>value</code>.</br>
	 * <ul>
	 * <li>If the value is deprecated normally, a <code>WARN</code>-level message will be logged to the provided logger.</li>
	 * <li>If the value is hard deprecated, a <code>HardDeprecationException</code> is thrown with a detail message containing the <code>value</code></li>
	 * <li>If the value is not deprecated, nothing else happens.</li>
	 * </ul>
	 * @param <T> the type of the <code>value</code> to be checked
	 * @param value the value to be tested
	 * @param depreciationLevelChecker a <code>Function</code> that takes an instance of type <code>T</code> and returns the <code>DepreciationLevel</code> of the instance
	 * @param logger the logger to which messages will be logged
	 * @throws HardDeprecationException if the <code>depreciationLevelChecker</code> returns a <code>DepreciationLevel</code> of <code>HARD</code>
	 * @see DeprecationLevel
	 * @see HardDeprecationException
	 * @see HardDeprecated &#64;HardDepreciated
	 */
	public static <T> void throwOrWarnAboutDeprecation(T value, Function<T, DeprecationLevel> deprecationLevelChecker, Logger logger) throws HardDeprecationException {
		val deprecationLevel = deprecationLevelChecker.apply(value);
		switch (deprecationLevel) {
		case NORMAL:
			logger.warn("%s is deprecated. Consider migrating.", value);
			break;
		case HARD:
			throw new HardDeprecationException(String.format("%s is hard deprecated, and cannot be used.", value));
		default:
			break;
		}
	}

	/**
	 * Takes an <code>Enum</code> value and checks its <code>DeprecationLevel</code> with {@link getEnumValueDeprecationLevel}.</br>
	 * <ul>
	 * <li>If the value is deprecated normally, a <code>WARN</code>-level message will be logged to the provided logger.</li>
	 * <li>If the value is hard deprecated, a <code>HardDeprecationException</code> is thrown with a detail message containing the <code>value</code></li>
	 * <li>If the value is not deprecated, nothing else happens.</li>
	 * </ul>
	 * @param value the <code>Enum</code> value to be tested
	 * @param logger the logger to which messages will be logged
	 * @throws HardDeprecationException if the value is hard deprecated
	 * @see DeprecationLevel
	 * @see HardDeprecationException
	 * @see HardDeprecated &#64;HardDepreciated
	 */
	public static void throwOrWarnAboutDepreciation(Enum<?> value, Logger logger) throws HardDeprecationException {
		throwOrWarnAboutDeprecation(value, AnnotationUtils::getEnumValueDeprecationLevel, logger);
	}
}
