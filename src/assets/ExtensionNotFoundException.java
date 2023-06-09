package assets;

/**
 * This exception should be thrown when an asset with an unrecognized file extension is read
 * @author Harry Xu
 * @version 1.0 - June 8th 2023
 */
public class ExtensionNotFoundException extends RuntimeException {
    /** Constructs a default ExtensionNotFoundException */
    public ExtensionNotFoundException() {
        super();
    }

    /**
     * Constructs a ExtensionNotFoundException with an error message
     * @param message the error message
     */
    public ExtensionNotFoundException(String message) {
        super(message);
    }
}
