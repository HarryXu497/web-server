package server.handler;

import java.util.Arrays;

public class URL {
    private final String path;
    private final String[] pathSegments;

    public URL(String path) {
        this.path = path;
        this.pathSegments = path.split("/");
    }

    public String[] getPathSegments() {
        return this.pathSegments;
    }

    public boolean matches(String route) {
        String[] routeSegments = route.split("/");

        if (this.pathSegments.length != routeSegments.length) {
            return false;
        }

        for (int i = 0; i < this.pathSegments.length; i++) {
            String curSegment = this.pathSegments[i];
            String otherSegment = routeSegments[i];

            if (!curSegment.equals(otherSegment) && !((curSegment.startsWith(":")) || curSegment.equals("*"))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return this.path;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return this.matches((String) obj);
        }

        return super.equals(obj);
    }
}
