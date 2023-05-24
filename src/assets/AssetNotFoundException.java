package assets;

public class AssetNotFoundException extends RuntimeException {
    public AssetNotFoundException() {
        super();
    }

    public AssetNotFoundException(String message) {
        super(message);
    }
}

