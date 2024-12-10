package aparmar.nai.utils.tokenization;

import java.io.IOException;
import java.io.InputStream;

import ai.djl.sentencepiece.SpProcessor;
import ai.djl.sentencepiece.SpTokenizer;

public class SpTokenizerWrapper extends AbstractSingleBitDepthNaiTokenizer {
	private final SpTokenizer spTokenizer;
	private final SpProcessor spProcessor;
	
	public SpTokenizerWrapper(InputStream modelInputStream, TokenBitDepth tokenBitDepth) throws IOException {
		super(tokenBitDepth);
		spTokenizer = new SpTokenizer(modelInputStream);
		spProcessor = spTokenizer.getProcessor();
	}

	@Override
	public int[] encode(String text) {
		return spProcessor.encode(text);
	}

	@Override
	public String decode(int[] tokens) {
		return spProcessor.decode(tokens);
	}

	@Override
	public int countTokens(String text) {
		return encode(text).length;
	}

}
