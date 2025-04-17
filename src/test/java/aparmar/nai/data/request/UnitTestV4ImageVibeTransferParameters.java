package aparmar.nai.data.request;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import aparmar.nai.TestHelpers;
import aparmar.nai.data.request.V4VibeData.VibeEncodingType;
import aparmar.nai.data.request.imagen.V4ImageVibeTransferParameters;

class UnitTestV4ImageVibeTransferParameters {

	@Test
	void testV4ImageVibeTransferParametersDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		V4ImageVibeTransferParameters testInstance1 = V4ImageVibeTransferParameters.builder()
				.build();
		V4ImageVibeTransferParameters testInstance2 = V4ImageVibeTransferParameters.builder()
				.vibeData(V4ImageVibeTransferParameters.VibeTransferData.builder()
						.vibeData(new V4VibeData(1, "a", VibeEncodingType.V4_CURATED, new byte[0]))
						.build())
				.build();
		TestHelpers.autoTestDataAndToBuilderAnnotation(V4ImageVibeTransferParameters.class, testInstance1, testInstance2);
	}
	
	@Test
	void testVibeDataWithMismatchedEncodingModelsAreRejected() {
		assertThrows(IllegalArgumentException.class, ()->V4ImageVibeTransferParameters.builder()
				.vibeData(V4ImageVibeTransferParameters.VibeTransferData.builder()
						.vibeData(new V4VibeData(1, "a", VibeEncodingType.V4_FULL, new byte[0]))
						.build())
				.vibeData(V4ImageVibeTransferParameters.VibeTransferData.builder()
						.vibeData(new V4VibeData(1, "a", VibeEncodingType.V4_CURATED, new byte[0]))
						.build()));
		assertThrows(IllegalArgumentException.class, ()->V4ImageVibeTransferParameters.builder()
				.vibeDatas(ImmutableList.of(
						V4ImageVibeTransferParameters.VibeTransferData.builder()
							.vibeData(new V4VibeData(1, "a", VibeEncodingType.V4_FULL, new byte[0]))
							.build(),
						V4ImageVibeTransferParameters.VibeTransferData.builder()
							.vibeData(new V4VibeData(1, "a", VibeEncodingType.V4_CURATED, new byte[0]))
							.build()
						)));
	}
}
