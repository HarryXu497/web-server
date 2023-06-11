package template;

/**
 * This exception should be thrown if a .th template contains invalid syntax
 * @author Harry Xu
 * @version 1.0 - May 20th 2023
 */
public class TemplateSyntaxException extends RuntimeException {
    /**
     * Constructs a {@link TemplateSyntaxException} with an error message
     * @param message the error message
     */
    public TemplateSyntaxException(String message) {
        super(message);
    }

    /**
     * Constructs a {@link TemplateSyntaxException} with a cause
     * @param cause the cause of the exceptiob
     */
    public TemplateSyntaxException(Throwable cause) {
        super(cause);
    }
}
