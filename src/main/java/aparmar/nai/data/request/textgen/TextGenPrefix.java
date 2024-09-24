package aparmar.nai.data.request.textgen;

import lombok.Getter;

@Getter
public enum TextGenPrefix {
	NONE(""),
	KRAKE_DEFAULT("<|ENDOFTEXT|>\n", 0, 187),
	ERATO_DEFAULT("<|reserved_special_token_81|>[ S: 4 ]\n", 128081, 58, 328, 25, 220, 19, 5243),
	ERATO_GREETING("<|reserved_special_token_81|>[ S: 4 ]\n[ Style: prose, opening ]\n", 128081, 58, 328, 25, 220, 19, 5243, 58, 12179, 25, 61801, 11, 8736, 5243);
	
	private final String textPrefix;
	private final int[] tokenPrefix;
	
	private TextGenPrefix(String textPrefix) {
		this.textPrefix = textPrefix;
		this.tokenPrefix = new int[0];
	}
	private TextGenPrefix(String textPrefix, int... tokenPrefix) {
		this.textPrefix = textPrefix;
		this.tokenPrefix = tokenPrefix;
	}
}
