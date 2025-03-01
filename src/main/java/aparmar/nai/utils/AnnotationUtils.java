package aparmar.nai.utils;

import java.util.function.Function;

import org.slf4j.Logger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

public class AnnotationUtils {

	@RequiredArgsConstructor
	@Getter
	public static enum DepreciationLevel {
		NONE(false),
		NORMAL(true),
		HARD(true);

		private final boolean isDepreciation;
	}

	/**
	 * Checks the <code>DepreciationLevel</code> of an <code>Enum</code> value
	 * @see DepreciationLevel
	 * @see HardDepreciated &#64;HardDepreciated
	 */
	public static DepreciationLevel getEnumValueDepreciationLevel(Enum<?> value) {
		try {
			val enumValueField = value.getClass().getField(value.name());
			if (enumValueField.isAnnotationPresent(HardDepreciated.class)) { return DepreciationLevel.HARD; }
			if (enumValueField.isAnnotationPresent(Deprecated.class)) { return DepreciationLevel.NORMAL; }
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		return DepreciationLevel.NONE;
	}
	/**
	 * Checks if an <code>Enum</code> value is depreciated
	 * @see getEnumValueDepreciationLevel
	 */
	public static boolean isEnumValueDepreciated(Enum<?> value) {
		return getEnumValueDepreciationLevel(value).isDepreciation();
	}
	/**
	 * Checks if an <code>Enum</code> value is hard depreciated
	 * @see getEnumValueDepreciationLevel
	 * @see HardDepreciated &#64;HardDepreciated
	 */
	public static boolean isEnumValueHardDepreciated(Enum<?> value) {
		return getEnumValueDepreciationLevel(value) == DepreciationLevel.HARD;
	}

	/**
	 * Takes a generic <code>value</code> and a <code>Function</code> that provides the <code>DepreciationLevel</code> for that <code>value</code>.</br>
	 * <ul>
	 * <li>If the value is depreciated normally, a <code>WARN</code>-level message will be logged to the provided logger.</li>
	 * <li>If the value is hard depreciated, a <code>HardDepreciationException</code> is thrown with a detail message containing the <code>value</code></li>
	 * <li>If the value is not depreciated, nothing else happens.</li>
	 * </ul>
	 * @param <T> the type of the <code>value</code> to be checked
	 * @param value the value to be tested
	 * @param depreciationLevelChecker a <code>Function</code> that takes an instance of type <code>T</code> and returns the <code>DepreciationLevel</code> of the instance
	 * @param logger the logger to which messages will be logged
	 * @throws HardDepreciationException if the <code>depreciationLevelChecker</code> returns a <code>DepreciationLevel</code> of <code>HARD</code>
	 * @see DepreciationLevel
	 * @see HardDepreciationException
	 * @see HardDepreciated &#64;HardDepreciated
	 */
	public static <T> void throwOrWarnAboutDepreciation(T value, Function<T, DepreciationLevel> depreciationLevelChecker, Logger logger) throws HardDepreciationException {
		val depreciationLevel = depreciationLevelChecker.apply(value);
		switch (depreciationLevel) {
		case NORMAL:
			logger.warn("%s is depreciated. Consider migrating.", value);
			break;
		case HARD:
			throw new HardDepreciationException(String.format("%s is hard depreciated, and cannot be used.", value));
		default:
			break;
		}
	}

	/**
	 * Takes an <code>Enum</code> value and checks its <code>DepreciationLevel</code> with {@link getEnumValueDepreciationLevel}.</br>
	 * <ul>
	 * <li>If the value is depreciated normally, a <code>WARN</code>-level message will be logged to the provided logger.</li>
	 * <li>If the value is hard depreciated, a <code>HardDepreciationException</code> is thrown with a detail message containing the <code>value</code></li>
	 * <li>If the value is not depreciated, nothing else happens.</li>
	 * </ul>
	 * @param value the <code>Enum</code> value to be tested
	 * @param logger the logger to which messages will be logged
	 * @throws HardDepreciationException if the value is hard depreciated
	 * @see DepreciationLevel
	 * @see HardDepreciationException
	 * @see HardDepreciated &#64;HardDepreciated
	 */
	public static void throwOrWarnAboutDepreciation(Enum<?> value, Logger logger) throws HardDepreciationException {
		throwOrWarnAboutDepreciation(value, AnnotationUtils::getEnumValueDepreciationLevel, logger);
	}
}
