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
		NOT_DEPRECIATED(AnnotationUtils.DepreciationLevel.NONE),
		@Deprecated
		DEPRECIATED_ONLY(AnnotationUtils.DepreciationLevel.NORMAL),
		@HardDepreciated
		HARD_DEPRECIATED_ONLY(AnnotationUtils.DepreciationLevel.HARD),
		@Deprecated
		@HardDepreciated
		BOTH_DEPRECIATION(AnnotationUtils.DepreciationLevel.HARD);
		
		private final AnnotationUtils.DepreciationLevel expectedDepreciationLevel;
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
		void testGetEnumValueDepreciationLevel(DepreciationTestEnum testValue) {
			assertEquals(testValue.getExpectedDepreciationLevel(), AnnotationUtils.getEnumValueDepreciationLevel(testValue));
		}

		@ParameterizedTest
		@EnumSource
		void testIsEnumValueDepreciated(DepreciationTestEnum testValue) {
			assertEquals(testValue.getExpectedDepreciationLevel().isDepreciation(), AnnotationUtils.isEnumValueDepreciated(testValue));
		}

		@ParameterizedTest
		@EnumSource
		void testIsEnumValueHardDepreciated(DepreciationTestEnum testValue) {
			assertEquals(testValue.getExpectedDepreciationLevel() == AnnotationUtils.DepreciationLevel.HARD, AnnotationUtils.isEnumValueHardDepreciated(testValue));
		}

		@ParameterizedTest
		@EnumSource
		void testEnumThrowOrWarnAboutDepreciation(DepreciationTestEnum testValue) {
			if (testValue.getExpectedDepreciationLevel() == AnnotationUtils.DepreciationLevel.HARD) {
				assertThrows(HardDepreciationException.class, ()->AnnotationUtils.throwOrWarnAboutDepreciation(testValue, mockLogger));
			} else {
				assertDoesNotThrow(()->AnnotationUtils.throwOrWarnAboutDepreciation(testValue, mockLogger));
			}
			if (testValue.getExpectedDepreciationLevel() == AnnotationUtils.DepreciationLevel.NORMAL) {
				verify(mockLogger, times(1)).warn(anyString(), eq(testValue));
			}
			verifyNoMoreInteractions(mockLogger);
		}
		
    }

	@ParameterizedTest
	@EnumSource
	void testGenericThrowOrWarnAboutDepreciation(AnnotationUtils.DepreciationLevel testValue) {
		if (testValue == AnnotationUtils.DepreciationLevel.HARD) {
			assertThrows(HardDepreciationException.class, ()->AnnotationUtils.throwOrWarnAboutDepreciation(testValue, Function.identity(), mockLogger));
		} else {
			assertDoesNotThrow(()->AnnotationUtils.throwOrWarnAboutDepreciation(testValue, Function.identity(), mockLogger));
		}
		if (testValue == AnnotationUtils.DepreciationLevel.NORMAL) {
			verify(mockLogger, times(1)).warn(anyString(), eq(testValue));
		}
		verifyNoMoreInteractions(mockLogger);
	}

}
