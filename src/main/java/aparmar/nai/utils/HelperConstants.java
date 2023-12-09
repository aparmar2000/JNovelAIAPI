package aparmar.nai.utils;

import java.util.regex.Pattern;

import okhttp3.MediaType;

public class HelperConstants {
	public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
	public static final MediaType MEDIA_TYPE_AUDIO = MediaType.parse("audio/mpeg");
	
	public static final String API_ROOT = "api.novelai.net";
	public static final String AUTH_HEADER = "Authorization";
	public static final String PERSISTENT_KEY_REGEX = "^(?:Bearer )?pst-[A-Za-z0-9]{64}$";
	public static final Pattern PERSISTENT_KEY_PATTERN = Pattern.compile(PERSISTENT_KEY_REGEX);

	public static final String PRESET_FILENAME = "presetGenerationParameters.txt";
	
	public static final String DINKUS = "***";
}
