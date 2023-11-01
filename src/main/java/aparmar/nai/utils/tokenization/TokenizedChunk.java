package aparmar.nai.utils.tokenization;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A chunk of text that is always correctly tokenized.
 */
@EqualsAndHashCode
public class TokenizedChunk implements Cloneable {
	@EqualsAndHashCode.Exclude
	private final ReentrantLock lock = new ReentrantLock();
	   
	@Getter
	private Tokenizers tokenizer;
	@EqualsAndHashCode.Exclude
	@Getter
	private String textChunk;
	@Getter
	private int[] tokens;
	
	public TokenizedChunk(Tokenizers tokenizer, String textChunk) {
		this.tokenizer = tokenizer;
		this.textChunk = textChunk;
		if (textChunk.isEmpty()) {
			tokens = new int[0];
		} else {
			tokens = tokenizer.encode(textChunk);
		}
	}
	
	public TokenizedChunk(Tokenizers tokenizer, int[] tokens) {
		this.tokenizer = tokenizer;
		this.tokens = tokens;
		if (tokens.length==0) {
			textChunk = "";
		} else {
			textChunk = tokenizer.decode(tokens);
		}
	}
	
	private TokenizedChunk(TokenizedChunk other) {
		this.tokenizer = other.getTokenizer();
		this.tokens = other.getTokens();
		this.textChunk = other.getTextChunk();
	}
	
	/**
	 * Updates the tokenizer used for this chunk. Re-encodes tokens from the text.
	 */
	public void setTokenizer(Tokenizers newTokenizer) {
		lock.lock();
		try {
			if (tokenizer == newTokenizer) { return; }
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
	 * Updates the tokens used for this chunk. Updates the text based on the decoded tokens.
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
	 * Appends tokens to the end of this chunk. Updates the text based on the decoded tokens.
	 */
	public void appendTokens(int[] newTokens) {
		lock.lock();
		try {
			if (newTokens.length==0) { return; }
			tokens = IntStream.concat(Arrays.stream(tokens), Arrays.stream(newTokens)).toArray();
			
			textChunk = tokenizer.decode(tokens);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Appends another chunk to the end of this chunk. Updates the text & tokens appropriately.
	 */
	public void appendTokenizedChunk(TokenizedChunk other) {
		if (getTokenizer()==other.getTokenizer()) {
			appendTokens(other.getTokens());
		} else {
			appendString(other.getTextChunk());
		}
	}
	
	@Override
	public TokenizedChunk clone() {
		return new TokenizedChunk(this);
	}
	
	public int tokenLength() {
		return tokens.length;
	}
	
	public boolean hasContent() {
		return !textChunk.isEmpty();
	}
	
	public String getBase64EncodedTokens() {
		return INaiTokenizer.tokensToBase64(tokens);
	}
}
