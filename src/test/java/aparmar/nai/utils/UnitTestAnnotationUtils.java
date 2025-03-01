package aparmar.nai.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

class UnitTestAnnotationUtils {
	
	@Getter
	@RequiredArgsConstructor
	private static enum DepreciationTestEnum {
		NOT_DEPRECIATED(AnnotationUtils.DeprecationLevel.NONE),
		@Deprecated
		DEPRECIATED_ONLY(AnnotationUtils.DeprecationLevel.NORMAL),
		@HardDeprecated
		HARD_DEPRECIATED_ONLY(AnnotationUtils.DeprecationLevel.HARD),
		@Deprecated
		@HardDeprecated
		BOTH_DEPRECIATION(AnnotationUtils.DeprecationLevel.HARD);
		
		private final AnnotationUtils.DeprecationLevel expectedDepreciationLevel;
	}
	
	AutoCloseable mocksAutoCloseable;
	
	@Mock
	Logger mockLogger;
	
	@BeforeEach
	void setUp() {
		mocksAutoCloseable = MockitoAnnotations.openMocks(this);
	}
	
	@AfterEach
	void tearDown() throws Exception {
		mocksAutoCloseable.close();
	}

    @Nested
    @DisplayName("Enum-specific helper methods")
    class EnumHelperTests {
    	
		@ParameterizedTest
		@EnumSource
		void testGetEnumValueDeprecationLevel(DepreciationTestEnum testValue) {
			assertEquals(testValue.getExpectedDepreciationLevel(), AnnotationUtils.getEnumValueDeprecationLevel(testValue));
		}

		@ParameterizedTest
		@EnumSource
		void testIsEnumValueDeprecated(DepreciationTestEnum testValue) {
			assertEquals(testValue.getExpectedDepreciationLevel().isDeprecation(), AnnotationUtils.isEnumValueDeprecated(testValue));
		}

		@ParameterizedTest
		@EnumSource
		void testIsEnumValueHardDeprecated(DepreciationTestEnum testValue) {
			assertEquals(testValue.getExpectedDepreciationLevel() == AnnotationUtils.DeprecationLevel.HARD, AnnotationUtils.isEnumValueHardDeprecated(testValue));
		}

		@ParameterizedTest
		@EnumSource
		void testEnumThrowOrWarnAboutDeprecation(DepreciationTestEnum testValue) {
			if (testValue.getExpectedDepreciationLevel() == AnnotationUtils.DeprecationLevel.HARD) {
				assertThrows(HardDeprecationException.class, ()->AnnotationUtils.throwOrWarnAboutDepreciation(testValue, mockLogger));
			} else {
				assertDoesNotThrow(()->AnnotationUtils.throwOrWarnAboutDepreciation(testValue, mockLogger));
			}
			if (testValue.getExpectedDepreciationLevel() == AnnotationUtils.DeprecationLevel.NORMAL) {
				verify(mockLogger, times(1)).warn(anyString(), eq(testValue));
			}
			verifyNoMoreInteractions(mockLogger);
		}
		
    }

	@ParameterizedTest
	@EnumSource
	void testGenericThrowOrWarnAboutDeprecation(AnnotationUtils.DeprecationLevel testValue) {
		if (testValue == AnnotationUtils.DeprecationLevel.HARD) {
			assertThrows(HardDeprecationException.class, ()->AnnotationUtils.throwOrWarnAboutDeprecation(testValue, Function.identity(), mockLogger));
		} else {
			assertDoesNotThrow(()->AnnotationUtils.throwOrWarnAboutDeprecation(testValue, Function.identity(), mockLogger));
		}
		if (testValue == AnnotationUtils.DeprecationLevel.NORMAL) {
			verify(mockLogger, times(1)).warn(anyString(), eq(testValue));
		}
		verifyNoMoreInteractions(mockLogger);
	}

}
