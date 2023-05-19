package server.handler;

import java.util.HashMap;
import java.util.Map;

public class Route {
    private final String route;
    private final Map<String, String> routeParameters;
    private final Map<String, String> queryParameters;

    public Route(URL base, String route) {
        this.route = route;
        this.routeParameters = new HashMap<>();
        this.queryParameters = new HashMap<>();

        String[] splitRoute = this.route.split("\\?");

        String href = splitRoute[0];
        String rawQueryParams = splitRoute[1];

        // Route params
        String[] pathSegments = href.split("/");

        for (int i = 0; i < pathSegments.length; i++) {
            String routeSegment = pathSegments[i];
            String urlSegment = base.getPathSegments()[i];

            if (urlSegment.startsWith(":")) {
                this.routeParameters.put(urlSegment.substring(1), routeSegment);
            }
        }

        // Query params
        String[] queryParams = rawQueryParams.split("&");

        for (String pair : queryParams) {
            String[] queryPair = pair.split("=");

            this.queryParameters.put(queryPair[0], queryPair[1]);
        }

    }

    public String getRoute() {
        return this.route;
    }

    public Map<String, String> getQueryParameters() {
        return this.queryParameters;
    }

    public Map<String, String> getRouteParameters() {
        return this.routeParameters;
    }
}
