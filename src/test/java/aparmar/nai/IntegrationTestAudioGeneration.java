package test.java.aparmar.nai;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import main.java.aparmar.nai.NAIAPI;
import main.java.aparmar.nai.data.request.VoiceGenerationRequest;
import main.java.aparmar.nai.data.request.VoiceGenerationRequest.PresetV1Voice;
import main.java.aparmar.nai.data.request.VoiceGenerationRequest.PresetV2Voice;
import main.java.aparmar.nai.data.request.VoiceGenerationRequest.VoiceVersion;
import main.java.aparmar.nai.data.response.AudioWrapper;

public class IntegrationTestAudioGeneration {
	private NAIAPI apiInstance;

	@BeforeEach
	public void setUp() throws Exception {
		assumeNotNull(TestConstants.getTestAPIKey());
		apiInstance = new NAIAPI(TestConstants.getTestAPIKey());
	}

	@ParameterizedTest
	@EnumSource(PresetV1Voice.class)
	public void testGenerateVoiceV1NonOpus(PresetV1Voice testVoice) throws IOException {
		VoiceGenerationRequest testRequest = VoiceGenerationRequest.builder()
				.text("test")
				.voice(testVoice.getVoiceId())
				.opus(false)
				.version(VoiceVersion.VERSION_1)
				.build();
		AudioWrapper actualResult = apiInstance.generateVoice(testRequest);
		assertNotNull(actualResult);
		assertTrue(actualResult.getBytes().length > 100);
	}

	@ParameterizedTest
	@EnumSource(PresetV1Voice.class)
	public void testGenerateVoiceV1Opus(PresetV1Voice testVoice) throws IOException {
		VoiceGenerationRequest testRequest = VoiceGenerationRequest.builder()
				.text("test")
				.voice(testVoice.getVoiceId())
				.opus(true)
				.version(VoiceVersion.VERSION_1)
				.build();
		AudioWrapper actualResult = apiInstance.generateVoice(testRequest);
		assertNotNull(actualResult);
		assertTrue(actualResult.getBytes().length > 100);
	}

	@ParameterizedTest
	@EnumSource(PresetV2Voice.class)
	public void testGenerateVoiceV2NonOpus(PresetV2Voice testVoice) throws IOException {
		VoiceGenerationRequest testRequest = VoiceGenerationRequest.builder()
				.text("test")
				.seed(testVoice.getSeedString())
				.opus(false)
				.version(VoiceVersion.VERSION_2)
				.build();
		AudioWrapper actualResult = apiInstance.generateVoice(testRequest);
		assertNotNull(actualResult);
		assertTrue(actualResult.getBytes().length > 100);
	}

	@ParameterizedTest
	@EnumSource(PresetV2Voice.class)
	public void testGenerateVoiceV2Opus(PresetV2Voice testVoice) throws IOException {
		VoiceGenerationRequest testRequest = VoiceGenerationRequest.builder()
				.text("test")
				.seed(testVoice.getSeedString())
				.opus(true)
				.version(VoiceVersion.VERSION_2)
				.build();
		AudioWrapper actualResult = apiInstance.generateVoice(testRequest);
		assertNotNull(actualResult);
		assertTrue(actualResult.getBytes().length > 100);
	}

	// TODO: Maybe write this later? Have to deal with ID3.
//	@ParameterizedTest
//	@EnumSource(VoiceVersion.class)
//	public void testGeneratedVoiceFiletype(VoiceVersion textVoiceVersion) throws IOException {
//		VoiceGenerationRequest testRequest = VoiceGenerationRequest.builder()
//				.text("test")
//				.opus(true)
//				.version(textVoiceVersion)
//				.build();
//		AudioWrapper actualGeneratedData = apiInstance.generateVoice(testRequest);
//		assertNotNull(actualGeneratedData);
//		Function<ByteBuffer, Boolean> dataValidator;
//		switch(textVoiceVersion.getReturnAudioFormat()) {
//		case MP3:
//			dataValidator = this::validateMP3Data;
//			break;
//		case WEBA:
//			break;
//		default:
//			dataValidator = (t) -> false;
//			break;
//		}
//		assertTrue(
//				dataValidator.apply(ByteBuffer.wrap(actualGeneratedData.getBytes())),
//				"Invalid "+textVoiceVersion.getReturnAudioFormat()+" file.");
//	}
//	
//	private boolean validateMP3Data(ByteBuffer data) {
//		byte[] headerData = new byte[4];
//		data.get(headerData);
//		if ((headerData[0] & 0xff) != 0xff) { return false; }
//		
//		return true;
//	}

}
