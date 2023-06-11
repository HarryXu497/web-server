package server.handler;

/**
 * Represents a URL pattern which can be used to match against URLs.
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/URL_Pattern_API">The URL pattern API</a>
 * @author Harry Xu
 * @version 1.0 - May 21st 2023
 */
public class URL {
    /** the url pattern as a string */
    private final String path;

    /** the url pattern as an array of tokens */
    private final String[] pathSegments;

    /**
     * Constructs a URL pattern with a path
     * @param path the URL pattern
     */
    public URL(String path) {
        this.path = path;
        this.pathSegments = path.split("/");
    }

    /**
     * getPath
     * gets the path
     * @return the path segments as a string
     */
    public String getPath() {
        return this.path;
    }

    /**
     * getPathSegments
     * gets the path segments
     * @return the path segments as an array of strings
     */
    public String[] getPathSegments() {
        return this.pathSegments;
    }

    /**
     * matches
     * determines if a URL matches with the defined pattern of this class
     * @param route the url to match against
     * @return if the route matches the pattern defined by this {@link URL}
     */
    public boolean matches(String route) {
        // Split into tokens
        String[] routeSegments = route.split("/");

        // Segments must have the same length
        if (this.pathSegments.length != routeSegments.length) {
            return false;
        }

        // Check each token against each other
        for (int i = 0; i < this.pathSegments.length; i++) {
            String curSegment = this.pathSegments[i];
            String otherSegment = routeSegments[i];

            // Allows for route parameters and wildcards
            if (!curSegment.equals(otherSegment) && !((curSegment.startsWith(":")) || curSegment.equals("*"))) {
                return false;
            }
        }

        return true;
    }

    /**
     * toString
     * converts the pattern to string
     * @return the pattern as a string
     * */
    @Override
    public String toString() {
        return this.path;
    }
}
