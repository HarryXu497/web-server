package assets;

/**
 * This exception should be thrown when an attempt to access an asset cannot find the asset
 * @author Harry Xu
 * @version 1.0 - June 8th 2023
 */
public class AssetNotFoundException extends RuntimeException {
    /** Constructs a default {@link AssetNotFoundException} */
    public AssetNotFoundException() {
        super();
    }

    /**
     * Constructs a {@link AssetNotFoundException} with an error message
     * @param message the error message
     */
    public AssetNotFoundException(String message) {
        super(message);
    }
}

