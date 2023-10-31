package main.java.aparmar.nai.utils.tokenization;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.platform.commons.util.ReflectionUtils;

import ai.djl.sentencepiece.SpTokenizer;

public class SpTokenizerWrapper implements INaiTokenizer {
	private final SpTokenizer spTokenizer;
	private final Object spProcessor;
	
	private final Method encoderMethod;
	private final Method decoderMethod;
	
	public SpTokenizerWrapper(Path modelPath) throws IOException {
		spTokenizer = new SpTokenizer(modelPath);
		spProcessor = spTokenizer.getProcessor();
		
		Optional<Method> getEncodeMethodOptional = 
				ReflectionUtils.findMethod(spProcessor.getClass(), "encode", String.class);
		if (!getEncodeMethodOptional.isPresent()) {
			throw new AssertionError("SpProcessor has no encode method!");
		}
		encoderMethod = getEncodeMethodOptional.get();
		encoderMethod.setAccessible(true);
		
		Optional<Method> getDecodeMethodOptional = 
				ReflectionUtils.findMethod(spProcessor.getClass(), "decode", int[].class);
		if (!getDecodeMethodOptional.isPresent()) {
			throw new AssertionError("SpProcessor has no decode method!");
		}
		decoderMethod = getDecodeMethodOptional.get();
		decoderMethod.setAccessible(true);
	}

	@Override
	public int[] encode(String text) {
		try {
			return (int[]) encoderMethod.invoke(spProcessor, text);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public String decode(int[] tokens) {
		try {
			return (String) decoderMethod.invoke(spProcessor, tokens);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public int countTokens(String text) {
		return spTokenizer.tokenize(text).size();
	}

}
