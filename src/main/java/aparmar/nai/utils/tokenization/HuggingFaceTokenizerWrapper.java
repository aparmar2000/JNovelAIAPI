package main.java.aparmar.nai.utils.tokenization;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;

public class HuggingFaceTokenizerWrapper implements INaiTokenizer {
	private static final Gson gson = new Gson();
	
	private final HuggingFaceTokenizer huggingFaceTokenizer;
	
	public HuggingFaceTokenizerWrapper (Path modelPath) throws IOException {		
		JsonObject tokenizerConfig = gson.fromJson(new FileReader(modelPath.toFile()), JsonObject.class);
		Type vocabMapType = new TypeToken<Map<String, Integer>>() {}.getType();
		Map<String, Integer> vocabMap = gson.fromJson(tokenizerConfig.get("vocab"), vocabMapType);
		String[][] merges = gson.fromJson(tokenizerConfig.get("merges"), String[][].class);
		
		Path tempVocabPath = modelPath.getParent().resolve("temp-vocab.json");
		try (FileWriter fOut = new FileWriter(tempVocabPath.toFile())) {
			gson.toJson(vocabMap, vocabMapType, fOut);
		}
		Path tempMergesPath = modelPath.getParent().resolve("temp-merges.txt");
		try (FileWriter fOut = new FileWriter(tempMergesPath.toFile())) {
			String mergesString = Arrays.stream(merges)
				.map(merge->merge[0]+" "+merge[1])
				.collect(Collectors.joining("\n"));
			
			fOut.write(mergesString);
		}
		
		huggingFaceTokenizer = HuggingFaceTokenizer.newInstance(tempVocabPath, tempMergesPath, null);
		// huggingFaceTokenizer = HuggingFaceTokenizer.newInstance(modelPath, modelPath, null);
	}

	@Override
	public int[] encode(String text) {
		return LongStream.of(huggingFaceTokenizer.encode(text).getIds()).mapToInt(i->(int)i).toArray();
	}

	@Override
	public String decode(int[] tokens) {
		return huggingFaceTokenizer.decode(IntStream.of(tokens).mapToLong(i->i).toArray());
	}

	@Override
	public int countTokens(String text) {
		return huggingFaceTokenizer.encode(text).getIds().length;
	}

}
