package aparmar.nai.utils.tokenization;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import com.google.common.collect.ImmutableMap;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;

public class HuggingFaceTokenizerWrapper extends AbstractSingleBitDepthNaiTokenizer {
	private final HuggingFaceTokenizer huggingFaceTokenizer;
	
	public HuggingFaceTokenizerWrapper(HuggingFaceTokenizer huggingFaceTokenizer, TokenBitDepth tokenBitDepth) {
		super(tokenBitDepth);
		this.huggingFaceTokenizer = huggingFaceTokenizer;
	}
	
	public HuggingFaceTokenizerWrapper(InputStream modelInputStream, TokenBitDepth tokenBitDepth) throws IOException {
		super(tokenBitDepth);
		huggingFaceTokenizer = HuggingFaceTokenizer.newInstance(
				modelInputStream, 
				ImmutableMap.of(
						"addSpecialTokens", "false",
						"maxLength", "8192",
						"truncation", "false",
						"padding", "false")
				);
	}

	@Override
	public int[] encode(String text) {
		return LongStream.of(huggingFaceTokenizer.encode(text, false, false).getIds()).skip(1).mapToInt(i->(int)i).toArray();
	}

	@Override
	public String decode(int[] tokens) {
		return huggingFaceTokenizer.decode(IntStream.of(tokens).mapToLong(i->i).toArray());
	}

	@Override
	public int countTokens(String text) {
		return huggingFaceTokenizer.encode(text).getIds().length-1;
	}

}
