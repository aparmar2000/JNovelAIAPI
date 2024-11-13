package aparmar.nai.utils.tokenization;

import java.io.IOException;
import java.io.InputStream;

import aparmar.nai.data.request.TextGenModel;
import aparmar.nai.utils.InternalResourceLoader;
import lombok.Getter;

public enum Tokenizers implements INaiTokenizer {
	GPT2("gpt2_tokenizer.json", TokenBitDepth.USHORT),
	GPT2_GENJI("genji_tokenizer.json", TokenBitDepth.USHORT), // Genji tokenizer doesn't consistently tokenize the same as NAI for some reason
	PILE("pile_tokenizer.json", TokenBitDepth.USHORT),
	NERDSTASH_V1("novelai.model", TokenBitDepth.USHORT),
	NERDSTASH_V2("novelai_v2.model", TokenBitDepth.USHORT),
	LLAMA_3("llama_3_tokenizer.json", TokenBitDepth.INTEGER);
	
	@Getter
	private final INaiTokenizer tokenizer;
	
	private Tokenizers(String modelFilename, TokenBitDepth tokenBitDepth) {
		INaiTokenizer newTokenizer = null;
		try {
			InputStream modelInputStream = InternalResourceLoader.getInternalResourceAsStream(modelFilename);
			
			if (modelFilename.contains("llama_3")) {
				newTokenizer = new HuggingFaceTokenizerWrapper(modelInputStream, tokenBitDepth);
			} else if (modelFilename.endsWith(".model")) {
				newTokenizer = new SpTokenizerWrapper(modelInputStream, tokenBitDepth);
			} else if (modelFilename.endsWith("tokenizer.json")) {
				newTokenizer = new JTokkitTokenizerWrapper(modelInputStream, tokenBitDepth, modelFilename.replaceAll("\\.[^.]+$", ""));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tokenizer = newTokenizer;
	}
	
	public static Tokenizers getTokenizerForModel(TextGenModel model) {
		switch (model) {
		case CALLIOPE:
		case SIGURD_OLD:
		case SIGURD:
		case EUTERPE:
		case SNEK:
		case HYPEBOT:
		case INFILL:
			return GPT2;
		case GENJI_OLD:
		case GENJI:
			return GPT2_GENJI;
		case KRAKE:
			return PILE;
			
		case CLIO:
			return NERDSTASH_V1;
		case KAYRA:
			return NERDSTASH_V2;
		case ERATO:
			return LLAMA_3;
		
		default:
			return null;
		}
	}


	@Override
	public TokenBitDepth getTokenBitDepth() {
		return getTokenizer().getTokenBitDepth();
	}

	@Override
	public int[] encode(String text) {
		return getTokenizer().encode(text);
	}

	@Override
	public String decode(int[] tokens) {
		return getTokenizer().decode(tokens);
	}

	@Override
	public int countTokens(String text) {
		return getTokenizer().countTokens(text);
	}

	@Override
	public int[] base64ToTokens(String base64) {
		return getTokenizer().base64ToTokens(base64);
	}

	@Override
	public String tokensToBase64(int[] tokens) {
		return getTokenizer().tokensToBase64(tokens);
	}
}