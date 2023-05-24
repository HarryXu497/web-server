package assets;

public class ExtensionNotFoundException extends RuntimeException {
    public ExtensionNotFoundException() {
        super();
    }

    public ExtensionNotFoundException(String message) {
        super(message);
    }
}
