package aparmar.nai.utils.tokenization;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Base64;
import java.util.stream.IntStream;

import com.google.common.primitives.Shorts;

public abstract interface INaiTokenizer {
	public static enum TokenBitDepth {
		USHORT,
		INTEGER;
	}
	
	public TokenBitDepth getTokenBitDepth();
	
	public int[] encode(String text);
	public String decode(int[] tokens);
	public default int countTokens(String text) {
		return this.encode(text).length;
	};
	
	public int[] base64ToTokens(String base64);
	
	public default String base64ToString(String base64) {
		return this.decode(base64ToTokens(base64));
	}
	
	public String tokensToBase64(int[] tokens);
	
	public default String stringToBase64(String input) {
		return tokensToBase64(this.encode(input));
	}
	

	public static int[] base64ToUShortTokens(String base64) {
		ShortBuffer tokenByteBuffer = 
				ByteBuffer.wrap(Base64.getDecoder().decode(base64))
				.order(ByteOrder.LITTLE_ENDIAN)
				.asShortBuffer();
		short[] tokenArray = new short[tokenByteBuffer.limit()];
		tokenByteBuffer.get(tokenArray);
		return Shorts.asList(tokenArray)
				.stream()
				.mapToInt(s->(s & 0xffff))
				.toArray();
	}
	
	public static String UShortTokensToBase64(int[] tokens) {
		ByteBuffer tokenByteBuffer = ByteBuffer.allocate(tokens.length*2);
		tokenByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int token : tokens) {
			tokenByteBuffer.putShort((short)token);
		}
		
		return Base64.getEncoder().encodeToString(tokenByteBuffer.array());
	}
	
	public static int[] base64ToIntegerTokens(String base64) {
		IntBuffer tokenByteBuffer = 
				ByteBuffer.wrap(Base64.getDecoder().decode(base64))
				.order(ByteOrder.LITTLE_ENDIAN)
				.asIntBuffer();
		int[] tokenArray = new int[tokenByteBuffer.limit()];
		tokenByteBuffer.get(tokenArray);
		return IntStream.of(tokenArray)
				.map(i->i)
				.toArray();
	}
	
	public static String IntegerTokensToBase64(int[] tokens) {
		ByteBuffer tokenByteBuffer = ByteBuffer.allocate(tokens.length*4);
		tokenByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int token : tokens) {
			tokenByteBuffer.putInt(token);
		}
		
		return Base64.getEncoder().encodeToString(tokenByteBuffer.array());
	}
}
