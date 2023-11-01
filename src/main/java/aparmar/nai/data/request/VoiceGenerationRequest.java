package aparmar.nai.data.request;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import aparmar.nai.data.response.AudioWrapper.AudioWrapperFormat;

@Getter
@EqualsAndHashCode
public class VoiceGenerationRequest implements IQueryStringPayload {
	private final String text;
	private final int voice;
	private final String seed;
	private final boolean opus;
	private final VoiceVersion version;

	@Builder
	private VoiceGenerationRequest(String text, int voice, String seed, boolean opus, VoiceVersion version) {
		if (text == null || text.trim().isEmpty()) {
			throw new IllegalArgumentException("Submitted text cannot be null or empty.");
		}
		if (text.length()>1000) {
			throw new IllegalArgumentException("Submitted text cannot be more than 1000 characters.");
		}
		
		this.text = text;
		this.voice = voice;
		this.seed = seed;
		this.opus = opus;
		this.version = version;
	}

	@RequiredArgsConstructor
	@Getter
	public enum VoiceVersion {
		VERSION_1("v1", AudioWrapperFormat.MP3),
		VERSION_2("v2", AudioWrapperFormat.WEBA);
		
		private final String value;
		private final AudioWrapperFormat returnAudioFormat;
	}
	
	@RequiredArgsConstructor
	public enum PresetV2Voice {
		LIGEIA("Anananan"),// Androgynous
		
		AINI("Aini"),// Female
		OREA("Orea"),
		CLAEA("Claea"),
		LIM("Lim"),
		AURAE("Aurae"),
		NAIA("Naia"),
		
		AULON("Aulon"),// Male
		ELEI("Elei"),
		OGMA("Ogma"),
		RAID("Raid"),
		PEGA("Pega"),
		LAM("Lam");
		
		@Getter
		private final String seedString;
	}
	
	@RequiredArgsConstructor
	public enum PresetV1Voice {
		CYLLENE(17),// Female
		LEUCOSIA(95),
		CRINA(44),
		HESPE(80),
		IDA(106),
		
		ALSEID(6),// Male
		DAPHNIS(10),
		ECHO(16),
		THEL(41),
		NOMIOS(77);
		
		@Getter
		private final int voiceId;
	}

	public static VoiceGenerationRequest presetV2VoiceRequest(String text, PresetV2Voice voice) {
		return seededVoiceRequest(text, voice.getSeedString(), VoiceVersion.VERSION_2);
	}
	public static VoiceGenerationRequest presetV1VoiceRequest(String text, PresetV1Voice voice) {
		return new VoiceGenerationRequest(text, voice.getVoiceId(), "", false, VoiceVersion.VERSION_1);
	}
	public static VoiceGenerationRequest seededVoiceRequest(String text, String voiceSeed, @NonNull VoiceVersion version) {
		return new VoiceGenerationRequest(text, -1, voiceSeed, false, version);
	}
	
	@Override
	public Map<String, ? extends Object> getParameterValues() {
		Map<String, String> valueMap = new HashMap<>();
		
		valueMap.put("text", text);
		valueMap.put("voice", Integer.toString(voice));
		if (voice==-1) { valueMap.put("seed", seed); }
		valueMap.put("opus", opus?"true":"false");
		valueMap.put("version", version.getValue());
		
		return valueMap;
	}
}
