package aparmar.nai.utils.tokenization;

import java.util.function.Function;

public abstract class AbstractSingleBitDepthNaiTokenizer implements INaiTokenizer {
	protected final TokenBitDepth tokenBitDepth;
	
	private final Function<String, int[]> base64Encoder;
	private final Function<int[], String> base64Decoder;
	
	protected AbstractSingleBitDepthNaiTokenizer(TokenBitDepth tokenBitDepth) {
		this.tokenBitDepth = tokenBitDepth;
		
		switch (tokenBitDepth) {
		default:
		case USHORT:
			base64Encoder = INaiTokenizer::base64ToUShortTokens;
			base64Decoder = INaiTokenizer::UShortTokensToBase64;
			break;
		case INTEGER:
			base64Encoder = INaiTokenizer::base64ToIntegerTokens;
			base64Decoder = INaiTokenizer::IntegerTokensToBase64;
			break;
		}
	}

	@Override
	public TokenBitDepth getTokenBitDepth() {
		return tokenBitDepth;
	}
	
	public int[] base64ToTokens(String base64) { return base64Encoder.apply(base64); }
	
	public String tokensToBase64(int[] tokens) { return base64Decoder.apply(tokens); }

}
