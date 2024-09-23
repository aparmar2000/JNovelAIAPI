package aparmar.nai.utils.tokenization;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import aparmar.nai.data.request.TextGenModel;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * A chunk of text that is always correctly tokenized. (Should be) thread-safe.
 */
@EqualsAndHashCode
public class TokenizedChunk implements Cloneable {
	@EqualsAndHashCode.Exclude
	private final ReentrantLock lock = new ReentrantLock();
	
	private INaiTokenizer tokenizer;
	@EqualsAndHashCode.Exclude
	private String textChunk;
	private int[] tokens;
	
	/**
	 * Holds a snapshot of the values of a <code>TokenizedChunk</code> at a particular moment in time.
	 */
	@Data
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class TokenizedChunkSnapshot {
		private final INaiTokenizer tokenizer;
		private final String text;
		private final int[] tokens;
	}
	
	public TokenizedChunk(INaiTokenizer tokenizer, String textChunk) {
		this.tokenizer = tokenizer;
		this.textChunk = textChunk;
		if (textChunk.isEmpty()) {
			tokens = new int[0];
		} else {
			tokens = tokenizer.encode(textChunk);
		}
	}
	public TokenizedChunk(TextGenModel model, String textChunk) {
		this.tokenizer = model.getTokenizerForModel();
		this.textChunk = textChunk;
		if (textChunk.isEmpty()) {
			tokens = new int[0];
		} else {
			tokens = tokenizer.encode(textChunk);
		}
	}
	
	public TokenizedChunk(INaiTokenizer tokenizer, int[] tokens) {
		this.tokenizer = tokenizer;
		this.tokens = tokens;
		if (tokens.length==0) {
			textChunk = "";
		} else {
			textChunk = tokenizer.decode(tokens);
		}
	}
	public TokenizedChunk(TextGenModel model, int[] tokens) {
		this.tokenizer = model.getTokenizerForModel();
		this.tokens = tokens;
		if (tokens.length==0) {
			textChunk = "";
		} else {
			textChunk = tokenizer.decode(tokens);
		}
	}
	
	private TokenizedChunk(TokenizedChunk other) {
		this.tokenizer = other.getTokenizer();
		this.tokens = other.getTokens().clone();
		this.textChunk = other.getTextChunk();
	}

	public INaiTokenizer getTokenizer() {
		lock.lock();
		try { return tokenizer; } finally { lock.unlock(); }
	}
	public String getTextChunk() {
		lock.lock();
		try { return textChunk; } finally { lock.unlock(); }
	}
	public int[] getTokens() {
		lock.lock();
		try { return tokens; } finally { lock.unlock(); }
	}
	/**
	 * Gets a snapshot of the current values of the <code>TokenizedChunk</code>.
	 * </br>
	 * Use this instead of the single-field <code>get</code> methods when retrieving multiple fields and need thread-safety.
	 */
	public TokenizedChunkSnapshot getSnapshot() {
		lock.lock();
		try {
			return new TokenizedChunkSnapshot(tokenizer, textChunk, tokens);
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Updates the tokenizer used for this chunk. Re-encodes tokens from the text.
	 */
	public void setTokenizer(INaiTokenizer newTokenizer) {
		lock.lock();
		try {
			if (tokenizer.equals(newTokenizer)) { return; }
			tokenizer = newTokenizer;
			if (!textChunk.isEmpty()) {
				tokens = newTokenizer.encode(textChunk);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Updates the text used for this chunk. Re-encodes tokens from the text.
	 */
	public void setTextChunk(String newTextChunk) {
		lock.lock();
		try {
			if (textChunk.equals(newTextChunk)) { return; }
			textChunk = newTextChunk;
			
			if (newTextChunk.isEmpty()) {
				tokens = new int[0];
			} else {
				tokens = tokenizer.encode(newTextChunk);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Updates the tokens used for this chunk. Updates the text based on the new tokens.
	 */
	public void setTokens(int[] newTokens) {
		lock.lock();
		try {
			if (Arrays.equals(tokens, newTokens)) { return; }
			tokens = newTokens;
			if (newTokens.length==0) {
				textChunk = "";
			} else {
				textChunk = tokenizer.decode(newTokens);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Appends a string to the end of this chunk. Re-encodes tokens from the text.
	 */
	public void appendString(String newString) {
		lock.lock();
		try {
			if (newString.isEmpty()) { return; }
			textChunk = textChunk+newString;
			
			tokens = tokenizer.encode(textChunk);
		} finally {
			lock.unlock();
		}
	}
	/**
	 * Prepends a string to the end of this chunk. Re-encodes tokens from the text.
	 */
	public void prependString(String newString) {
		lock.lock();
		try {
			if (newString.isEmpty()) { return; }
			textChunk = newString+textChunk;
			
			tokens = tokenizer.encode(textChunk);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Appends tokens to the end of this chunk. Updates the text based on the decoded tokens.
	 */
	public void appendTokens(int[] newTokens) {
		lock.lock();
		try {
			if (newTokens.length==0) { return; }
			
			appendString(tokenizer.decode(newTokens));
		} finally {
			lock.unlock();
		}
	}
	/**
	 * Appends tokens to the end of this chunk. Updates the text based on the decoded tokens.
	 */
	public void prependTokens(int[] newTokens) {
		lock.lock();
		try {
			if (newTokens.length==0) { return; }
			
			prependString(tokenizer.decode(newTokens));
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Appends another chunk to the end of this chunk. Updates the text & tokens appropriately.
	 */
	public void appendTokenizedChunk(TokenizedChunk other) {
		appendString(other.getTextChunk());
	}
	/**
	 * Prepends another chunk to the end of this chunk. Updates the text & tokens appropriately.
	 */
	public void prependTokenizedChunk(TokenizedChunk other) {
		prependString(other.getTextChunk());
	}
	/**
	 * Appends multiple other chunks to the end of this chunk. Updates the text & tokens appropriately.
	 */
	public void appendTokenizedChunks(TokenizedChunk... other) {
		appendString(Arrays.stream(other).map(TokenizedChunk::getTextChunk).collect(Collectors.joining()));
	}
	/**
	 * Prepends multiple other chunks to the end of this chunk. Updates the text & tokens appropriately.
	 */
	public void prependTokenizedChunks(TokenizedChunk... other) {
		prependString(Arrays.stream(other).map(TokenizedChunk::getTextChunk).collect(Collectors.joining()));
	}
	public static TokenizedChunk mergeTokenizedChunks(INaiTokenizer tokenizer, TokenizedChunk... chunks) {
		return new TokenizedChunk(tokenizer, Arrays.stream(chunks).map(TokenizedChunk::getTextChunk).collect(Collectors.joining()));
	}
	public static TokenizedChunk mergeTokenizedChunks(TextGenModel model, TokenizedChunk... chunks) {
		return TokenizedChunk.mergeTokenizedChunks(model.getTokenizerForModel(), chunks);
	}
	public static TokenizedChunk mergeTokenizedChunks(TokenizedChunk baseChunk, TokenizedChunk... chunks) {
		TokenizedChunk baseCopy = baseChunk.clone();
		baseCopy.appendTokenizedChunks(chunks);
		return baseCopy;
	}
	
	@Override
	public TokenizedChunk clone() {
		return new TokenizedChunk(this);
	}
	
	public int tokenLength() {
		return tokens.length;
	}
	
	public int textLength() {
		return textChunk.length();
	}
	
	public boolean hasContent() {
		return !textChunk.isEmpty();
	}
	
	public void setTokensFromBase64(String base64) {
		setTokens(tokenizer.base64ToTokens(base64));
	}
	
	public String getBase64EncodedTokens() {
		return tokenizer.tokensToBase64(tokens);
	}
	
	public static TokenizedChunk fromBase64Tokens(INaiTokenizer tokenizer, String base64) {
		return new TokenizedChunk(tokenizer, tokenizer.base64ToTokens(base64));
	}
}
