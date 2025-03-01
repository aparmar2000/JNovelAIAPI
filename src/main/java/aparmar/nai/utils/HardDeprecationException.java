package aparmar.nai.utils;

/**
 * Thrown to indicate that a {@link HardDeprecated &#64;HardDepreciated} program element has been used where such usage is prohibited.
 */
public class HardDeprecationException extends RuntimeException {
	private static final long serialVersionUID = 5792076230093251340L;
    /**
     * Constructs an <code>HardDepreciationException</code> with no
     * detail message.
     */
    public HardDeprecationException() {
        super();
    }

    /**
     * Constructs an <code>HardDepreciationException</code> with the
     * specified detail message.
     *
     * @param   s   the detail message.
     */
    public HardDeprecationException(String s) {
        super(s);
    }

}
