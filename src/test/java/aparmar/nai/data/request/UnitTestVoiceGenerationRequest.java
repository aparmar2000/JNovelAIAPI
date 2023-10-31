package test.java.aparmar.nai.data.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.google.common.base.Strings;

import main.java.aparmar.nai.data.request.VoiceGenerationRequest;
import main.java.aparmar.nai.data.request.VoiceGenerationRequest.PresetV1Voice;
import main.java.aparmar.nai.data.request.VoiceGenerationRequest.PresetV2Voice;
import main.java.aparmar.nai.data.request.VoiceGenerationRequest.VoiceVersion;
import test.java.aparmar.nai.TestHelpers;

class UnitTestVoiceGenerationRequest {

	@Test
	void testVoiceGenerationRequestDataAnnotation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		VoiceGenerationRequest testInstance1 = VoiceGenerationRequest.builder()
				.text("blue")
				.version(VoiceVersion.VERSION_1)
				.build();
		VoiceGenerationRequest testInstance2 = VoiceGenerationRequest.builder()
				.text("test")
				.voice(15)
				.seed("seed")
				.opus(true)
				.version(VoiceVersion.VERSION_2)
				.build();
		TestHelpers.autoTestDataAndToBuilderAnnotation(VoiceGenerationRequest.class, testInstance1, testInstance2);
	}

	@ParameterizedTest
	@ValueSource(ints = {0,1001})
	void testCharacterLimit(int testLength) {
		assertThrows(
				IllegalArgumentException.class, 
				VoiceGenerationRequest.builder()
					.text(Strings.repeat("H", testLength))
					.version(VoiceVersion.VERSION_1)
					::build);
	}

	@Test
	void testNullString() {
		assertThrows(
				IllegalArgumentException.class, 
				VoiceGenerationRequest.builder()
					.version(VoiceVersion.VERSION_1)
					::build);
	}
	
	@Test
	void testSeededVoiceRequest() {
		VoiceGenerationRequest expectedRequest = VoiceGenerationRequest.builder()
				.text("test")
				.voice(-1)
				.seed("seed")
				.opus(false)
				.version(VoiceVersion.VERSION_2)
				.build();
		VoiceGenerationRequest actualRequest = 
				VoiceGenerationRequest.seededVoiceRequest("test", "seed", VoiceVersion.VERSION_2);
		
		assertEquals(expectedRequest, actualRequest);
	}
	
	@Test
	void testPresetV2VoiceRequest() {
		VoiceGenerationRequest expectedRequest = VoiceGenerationRequest.builder()
				.text("test")
				.voice(-1)
				.seed(PresetV2Voice.AINI.getSeedString())
				.opus(false)
				.version(VoiceVersion.VERSION_2)
				.build();
		VoiceGenerationRequest actualRequest = 
				VoiceGenerationRequest.presetV2VoiceRequest("test", PresetV2Voice.AINI);
		
		assertEquals(expectedRequest, actualRequest);
	}
	
	@Test
	void testPresetV1VoiceRequest() {
		VoiceGenerationRequest expectedRequest = VoiceGenerationRequest.builder()
				.text("test")
				.voice(PresetV1Voice.ALSEID.getVoiceId())
				.seed("")
				.opus(false)
				.version(VoiceVersion.VERSION_1)
				.build();
		VoiceGenerationRequest actualRequest = 
				VoiceGenerationRequest.presetV1VoiceRequest("test", PresetV1Voice.ALSEID);
		
		assertEquals(expectedRequest, actualRequest);
	}
}
