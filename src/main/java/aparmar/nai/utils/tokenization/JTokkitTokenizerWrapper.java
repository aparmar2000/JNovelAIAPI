package aparmar.nai.utils.tokenization;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.GptBytePairEncodingParams;

public class JTokkitTokenizerWrapper implements INaiTokenizer {
	private static final Gson gson = new Gson();
	private static final EncodingRegistry encodingRegistry = Encodings.newDefaultEncodingRegistry();
	private static final HashBiMap<Integer, Integer> codePointRemapping = calculateCodePointRemapping();
	
	private final Encoding jTokkitTokenizer;
	
	public JTokkitTokenizerWrapper (Path modelPath) throws IOException {
		String modelName = modelPath.getFileName().toString().replaceAll("\\.json$", "");
		
		JsonObject tokenizerConfig = gson.fromJson(new FileReader(modelPath.toFile()), JsonObject.class);
		String splitRegex = tokenizerConfig.getAsJsonObject("config").get("splitRegex").getAsString();
		Type vocabMapType = new TypeToken<Map<String, Integer>>() {}.getType();
		Map<String, Integer> vocabMap = gson.fromJson(tokenizerConfig.get("vocab"), vocabMapType);
		String[] specialTokenStrings = tokenizerConfig.getAsJsonArray("specialTokens")
				.asList()
				.stream()
				.map(JsonElement::getAsString)
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
		jTokkitTokenizer = encodingRegistry.getEncoding(modelName).get();
	}
	
	private static HashBiMap<Integer, Integer> calculateCodePointRemapping() {
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
	
	private static byte[] remapUnicodeToBytes(String in) {
	
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
		return jTokkitTokenizer.encode(text).stream().mapToInt(i->i).toArray();
	}

	@Override
	public String decode(int[] tokens) {
		return jTokkitTokenizer.decode(IntStream.of(tokens).boxed().collect(Collectors.toList()));
	}

	@Override
	public int countTokens(String text) {
		return jTokkitTokenizer.countTokens(text);
	}

}
