package aparmar.nai.utils.tokenization;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.GptBytePairEncodingParams;
import com.knuddels.jtokkit.api.IntArrayList;

public class JTokkitTokenizerWrapper extends AbstractSingleBitDepthNaiTokenizer {
	protected static final Gson gson = new Gson();
	protected static final EncodingRegistry encodingRegistry = Encodings.newDefaultEncodingRegistry();
	protected static final HashBiMap<Integer, Integer> codePointRemapping = calculateCodePointRemapping();
	
	protected final Encoding jTokkitTokenizer;
	
	public JTokkitTokenizerWrapper (InputStream modelInputStream, TokenBitDepth tokenBitDepth, String modelName) throws IOException {
		super(tokenBitDepth);
		registerEncoding(modelInputStream, modelName);
		jTokkitTokenizer = encodingRegistry.getEncoding(modelName).get();
	}

	protected void registerEncoding(InputStream modelInputStream, String modelName)
			throws JsonSyntaxException, JsonIOException {
		JsonObject tokenizerConfig = gson.fromJson(
				new InputStreamReader(modelInputStream, StandardCharsets.UTF_8),
				JsonObject.class);
		String splitRegex = tokenizerConfig.getAsJsonObject("config").get("splitRegex").getAsString();
		Type vocabMapType = new TypeToken<Map<String, Integer>>() {}.getType();
		Map<String, Integer> vocabMap = gson.fromJson(tokenizerConfig.get("vocab"), vocabMapType);
		String[] specialTokenStrings = tokenizerConfig.getAsJsonArray("specialTokens")
				.asList()
				.stream()
				.map(JsonElement::getAsString)
				.filter(s->s.contains("<|") && s.contains("|>"))
				.toArray(String[]::new);
		
		Map<byte[], Integer> byteVocabMap = vocabMap.entrySet().stream()
	        	.collect(Collectors.toMap(e->remapUnicodeToBytes(e.getKey()), e->e.getValue()));
		Map<String, Integer> specialTokenMap = Arrays.stream(specialTokenStrings)
	        	.collect(Collectors.toMap(Function.identity(), vocabMap::get));
		GptBytePairEncodingParams encodingParams = new GptBytePairEncodingParams(
				modelName,
		        Pattern.compile(splitRegex),
		        byteVocabMap,
		        specialTokenMap
		);
		
		encodingRegistry.registerGptBytePairEncoding(encodingParams);
	}
	
	protected static HashBiMap<Integer, Integer> calculateCodePointRemapping() {
		IntStream segment1 = IntStream.rangeClosed("!".codePointAt(0),"~".codePointAt(0));
		IntStream segment2 = IntStream.rangeClosed("¡".codePointAt(0),"¬".codePointAt(0));
		IntStream segment3 = IntStream.rangeClosed("®".codePointAt(0),"ÿ".codePointAt(0));
		
		IntStream mergedStream = IntStream.concat(segment1, segment2);
		mergedStream = IntStream.concat(mergedStream, segment3);
		Set<Integer> unchangedCodePoints = mergedStream.boxed().collect(Collectors.toSet());
		
		HashBiMap<Integer, Integer> calculatedCodePointRemapping = HashBiMap.create(255);
		int n=0;
		for (int i=0;i<256;i++) {
			if (unchangedCodePoints.contains(i)) {
				calculatedCodePointRemapping.put(i, i);
			} else {
				calculatedCodePointRemapping.put(i, 256+n);
				n++;
			}
		}
		
		return calculatedCodePointRemapping;
	}
	
	protected static byte[] remapUnicodeToBytes(String in) {
	
		return in.codePoints()
				.map(c->codePointRemapping.inverse().getOrDefault(c,c))
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString()
				.getBytes(StandardCharsets.UTF_8);
	}
//	
//	private static byte[] remapUnicodeToBytes(String in) {
//		
//		return in.codePoints()
//				.map(c->changedCodePoints.contains(c)?c-256:c)
//				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
//				.toString()
//				.getBytes(StandardCharsets.UTF_8);
//	}

	@Override
	public int[] encode(String text) {
		return jTokkitTokenizer.encode(text).toArray();
	}

	@Override
	public String decode(int[] tokens) {
		IntArrayList tokenList = new IntArrayList(tokens.length);
		for (int token : tokens) { tokenList.add(token); }
		return jTokkitTokenizer.decode(tokenList);
	}

	@Override
	public int countTokens(String text) {
		return jTokkitTokenizer.countTokens(text);
	}

}
