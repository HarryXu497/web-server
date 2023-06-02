package template;

/**
 * represents a directive in the template, which is a special syntax which allows for control flow in HTML
 * <div>
 * Example:
 * <pre>
 *  {#if data.isSignedIn}
 *      &lt;div&gt;You are signed in!&lt;/div&gt;
 *  {/if}
 *  </pre>
 *  </div>
 * @author Harry Xu
 * @version 1.0 - May 20th 2023
 * */
public class Directive {
    /** Directive Type */
    private final DirectiveType type;

    /** The start of the interpolation braces as an index number */
    private int startNumber;

    /** The end of the interpolation braces as an index number */
    private int endNumber;

    /** The tokens of the directive */
    private final String[] tokens;

    /**
     * constructs a Directive with a type, start and end number, and tokens
     * @param type the directive type
     * @param startNumber the start of the interpolation braces
     * @param endNumber the end of the interpolation braces
     * @param tokens the directive token
     */
    public Directive(DirectiveType type, int startNumber, int endNumber, String[] tokens) {
        this.type = type;
        this.endNumber = endNumber;
        this.startNumber = startNumber;
        this.tokens = tokens;
    }

    /**
     * getType
     * gets the type of the directive
     * @return the directive as a value of the {@link DirectiveType} enum
     * */
    public DirectiveType getType() {
        return this.type;
    }

    /**
     * getStartNumber
     * gets the index of the opening directive brace
     * @return the start index
     */
    public int getStartNumber() {
        return this.startNumber;
    }

    /**
     * getEndNumber
     * gets the index of the closing directive brace
     * @return the end index
     */
    public int getEndNumber() {
        return this.endNumber;
    }

    /**
     * getTokens
     * gets the tokens that make up this directive
     * @return the token as an array of string
     */
    public String[] getTokens() {
        return this.tokens;
    }

    /**
     * setStartNumber
     * sets the index of the opening directive brace
     * @param startNumber the start index
     */
    public void setStartNumber(int startNumber) {
        this.startNumber = startNumber;
    }

    /**
     * setEndNumber
     * sets the index of the closing directive brace
     * @param endNumber the end index
     */
    public void setEndNumber(int endNumber) {
        this.endNumber = endNumber;
    }
}
