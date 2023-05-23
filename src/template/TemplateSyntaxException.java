package template;

/**
 * This exception should be thrown if a .th template contains invalid syntax
 * @author Harry Xu
 * @version 1.0 - May 20th 2023
 */
public class TemplateSyntaxException extends RuntimeException {
    public TemplateSyntaxException(String message) {
        super(message);
    }

    public TemplateSyntaxException(Throwable cause) {
        super(cause);
    }
}
