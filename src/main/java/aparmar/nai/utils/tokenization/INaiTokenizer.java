package aparmar.nai.utils.tokenization;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Base64;

import com.google.common.primitives.Shorts;

public abstract interface INaiTokenizer {
	public abstract int[] encode(String text);
	public abstract String decode(int[] tokens);
	public abstract int countTokens(String text);
	
	public static int[] base64ToTokens(String base64) {
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
	
	public default String base64ToString(String base64) {
		return this.decode(base64ToTokens(base64));
	}
	
	public static String tokensToBase64(int[] tokens) {
		ByteBuffer tokenByteBuffer = ByteBuffer.allocate(tokens.length*2);
		tokenByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int token : tokens) {
			tokenByteBuffer.putShort((short)token);
		}
		
		return Base64.getEncoder().encodeToString(tokenByteBuffer.array());
	}
	
	public default String stringToBase64(String input) {
		return tokensToBase64(this.encode(input));
	}
}
