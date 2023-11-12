package aparmar.nai.utils.tokenization;

import java.io.IOException;
import java.nio.file.Path;

import lombok.Getter;
import aparmar.nai.data.request.TextGenModel;
import aparmar.nai.utils.InternalResourceLoader;

public enum Tokenizers implements INaiTokenizer {
	GPT2("gpt2_tokenizer.json"),
	GPT2_GENJI("genji_tokenizer.json"), // Genji tokenizer doesn't consistently tokenize the same as NAI for some reason
	PILE("pile_tokenizer.json"),
	NERDSTASH_V1("novelai.model"),
	NERDSTASH_V2("novelai_v2.model");
	
	@Getter
	private final INaiTokenizer tokenizer;
	
	private Tokenizers(String modelName) {
		INaiTokenizer newTokenizer = null;
		try {
			Path modelPath = InternalResourceLoader.getInternalFilePath(modelName);
			
			if (modelName.endsWith(".model")) {
				newTokenizer = new SpTokenizerWrapper(modelPath);
			} else if (modelName.endsWith("tokenizer.json")) {
				newTokenizer = new JTokkitTokenizerWrapper(modelPath);
			}
		} catch (IOException | NoSuchMethodException | SecurityException e) {
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
		
		default:
			return null;
		}
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
}