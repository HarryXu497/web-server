package template;

/**
 * This exception should be thrown if a .th template cannot be located
 * @author Harry Xu
 * @version 1.0 - May 20th 2023
 */
public class TemplateNotFoundException extends RuntimeException {
    /** Constructs a default {@link TemplateNotFoundException} */
    public TemplateNotFoundException() {
        super();
    }

    /**
     * Constructs a {@link TemplateNotFoundException} with an error message
     * @param message the error message
     */
    public TemplateNotFoundException(String message) {
        super(message);
    }
}
