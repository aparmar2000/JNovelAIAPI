package aparmar.nai.utils.tokenization;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.knuddels.jtokkit.api.GptBytePairEncodingParams;

public class JTokkitLlama3TokenizerWrapper extends JTokkitTokenizerWrapper {

	public JTokkitLlama3TokenizerWrapper(InputStream modelInputStream, String modelName) throws IOException {
		super(modelInputStream, modelName);
	}

	@Override
	protected void registerEncoding(InputStream modelInputStream, String modelName)
			throws JsonSyntaxException, JsonIOException {
		JsonObject tokenizerConfig = gson.fromJson(
				new InputStreamReader(modelInputStream, StandardCharsets.UTF_8),
				JsonObject.class);
		String splitRegex = tokenizerConfig.getAsJsonObject("pre_tokenizer")
				.getAsJsonArray("pretokenizers")
				.asList()
				.stream()
				.filter(JsonElement::isJsonObject)
				.map(JsonElement::getAsJsonObject)
				.filter(e->e.has("type") && e.get("type").getAsString().equals("Split"))
				.findAny()
				.get()
				.getAsJsonObject("pattern").get("Regex").getAsString();
		Type vocabMapType = new TypeToken<Map<String, Integer>>() {}.getType();
		Map<String, Integer> vocabMap = gson.fromJson(tokenizerConfig.getAsJsonObject("model").get("vocab"), vocabMapType);
		Map<String, Integer> specialTokenMap = tokenizerConfig.getAsJsonArray("added_tokens")
				.asList()
				.stream()
				.map(JsonElement::getAsJsonObject)
				.collect(Collectors.toMap(e->e.get("content").getAsString(), e->e.get("id").getAsInt()));
		
		Map<byte[], Integer> byteVocabMap = vocabMap.entrySet().stream()
	        	.collect(Collectors.toMap(e->remapUnicodeToBytes(e.getKey()), e->e.getValue()));
		GptBytePairEncodingParams encodingParams = new GptBytePairEncodingParams(
				modelName,
		        Pattern.compile(splitRegex),
		        byteVocabMap,
		        specialTokenMap
		);
		
		encodingRegistry.registerGptBytePairEncoding(encodingParams);
	}

}
