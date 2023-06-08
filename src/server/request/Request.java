package server.request;

import server.handler.URL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an HTTP request to the server, to which the server responds to
 * An HTTP requests consists of a status line, containing the protocol, request method, and url,
 * the requests headers, and the requests body.
 * @author Harry Xu
 * @version 1.0 - May 21st 2023
 */
public class Request {
    /** the status line, which contains the request protocol, method, and url */
    private final StatusLine statusLine;

    /** a map of the request headers */
    private final Map<String, String> headers;

    /** a map of all key value pairs in the request body */
    private final Map<String, String> body;

    /** a map of all cookies from the headers */
    private final Map<String, String> cookies;

    /**
     * constructs an HTTP request with a status line, headers, and body
     * @param statusLine the HTTP requests status line
     * @param headers a map of the request headers
     * @param body a map of the request body
     */
    public Request(StatusLine statusLine, Map<String, String> headers, Map<String, String> body) {
        this.headers = headers;
        this.body = body;
        this.statusLine = statusLine;

        // Populate cookies
        this.cookies = new HashMap<>();

        String rawCookies = this.headers.get("Cookie");

        if (rawCookies != null) {
            for (String cookiePair : rawCookies.split(";")) {
                String[] cookieKeyValue = cookiePair.trim().split("=");

                if (cookieKeyValue.length == 1) {
                    this.cookies.put(cookieKeyValue[0], "");
                } else {
                    this.cookies.put(cookieKeyValue[0], cookieKeyValue[1]);
                }
            }
        }
    }

    /**
     * getStatusLine
     * gets the status line of the request
     * @return the status line
     */
    public StatusLine getStatusLine() {
        return this.statusLine;
    }

    /**
     * getHeaders
     * gets the headers of the request
     * @return the request headers
     */
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    /**
     * getBody
     * gets the body of the request
     * @return the request body
     */
    public Map<String, String> getBody() {
        return this.body;
    }

    /**
     * getCookies
     * gets the cookies of the request
     * @return the request cookies parsed from the headers
     */
    public Map<String, String> getCookies() {
        return this.cookies;
    }

    /**
     * toString
     * converts the request to a string
     * @return the request in string representation
     */
    @Override
    public String toString() {
        return "Request{" +
                "statusLine=" + statusLine +
                ", headers=" + headers +
                ", body=" + body +
                '}';
    }

    /**
     * parse
     * parses and constructs an HTTP request from an HTTP request string.
     * @param lines a list of all lines in the request
     * @return the parsed request as a {@link Request}
     */
    public static Request parse(List<String> lines) {
        // The first line contains the status line
        // e.g. GET / HTTP/1.1
        String statusLineStr = lines.get(0);
        String[] statusLineTokens = statusLineStr.split(" ");

        String method = statusLineTokens[0];
        // Remove Query Params
        String URL = statusLineTokens[1];
        String status = statusLineTokens[2];

        StatusLine statusLine = new StatusLine(RequestMethod.valueOf(method), URL, status);

        // Headers
        List<String> rawHeaders = lines.subList(1, lines.size() - 1);
        Map<String, String> headers = new HashMap<>();

        for (String line : rawHeaders) {
            String key = line.substring(0, line.indexOf(":"));
            String value = line.substring(line.indexOf(" ") + 1);

            headers.put(key, value);
        }

        // Request body
        Map<String, String> body = new HashMap<>();

        String rawBody = lines.get(lines.size() - 1);

        if (rawBody.length() > 0) {
            String[] bodyPairs = rawBody.split("&");

            for (String pair : bodyPairs) {
                String[] splitPair = pair.split("=");

                // Account for empty request body
                if (splitPair.length == 1) {
                    body.put(splitPair[0], "");
                    continue;
                }

                body.put(splitPair[0], splitPair[1]);
            }
        }

        // Request body
        return new Request(statusLine, headers, body);
    }

    /**
     * Represents the status line of an HTTP requests, which is the first line in the request.
     * It contains the request method, url, and protocol
     * @author Harry Xu
     * @version 1.0 - May 21st 2023
     */
    public static class StatusLine {
        /** the request method of the request */
        private final RequestMethod method;

        /** the full url, including query parameters */
        private final String url;

        /** the url, without any query parameters */
        private final String location;

        /** the request protocol, usually HTTP/1.1 */
        private final String protocol;

        /** the query parameters of the request, parsed into a map */
        private final Map<String, String> queryParams;

        /** the route parameters of the request, which must be parsed and populated according to a {@link URL}*/
        private Map<String, String> routeParams;

        /**
         * constructs a status line with a method, url, and protocol
         * @param method the request method
         * @param url the full url of the request
         * @param protocol the protocol of the request
         */
        public StatusLine(RequestMethod method, String url, String protocol) {
            this.method = method;
            this.url = url;
            this.protocol = protocol;

            // Parse location and query params
            String[] splitUrl = this.url.split("\\?");

            this.location = splitUrl[0];
            this.queryParams = new HashMap<>();

            // populate query params if they exist
            if (splitUrl.length == 2) {
                for (String pair : splitUrl[1].split("&")) {
                    String[] keyValue = pair.split("=");

                    if (keyValue.length != 2) {
                        continue;
                    }

                    this.queryParams.put(keyValue[0], keyValue[1]);
                }
            }
        }

        /**
         * populateRequestParams
         * populates the request params of the request given the URL it has matched with
         * @param url the url to populate the request param keys from
         * @param route the actual visited route to retrieve the request param values from
         * @throws IllegalStateException if the routes do not match. They should be checked for matching with url.matches() first
         */
        public void populateRequestParams(URL url, String route) {
            this.routeParams = new HashMap<>();

            String[] routeSegments = route.split("/");
            String[] urlSegments = url.getPathSegments();

            if (urlSegments.length != routeSegments.length) {
                throw new IllegalStateException("The 2 routes should be checked if they matched with url.matches() before invoking this method");
            }

            for (int i = 0; i < urlSegments.length; i++) {
                String curSegment = urlSegments[i];
                String otherSegment = routeSegments[i];

                if (curSegment.startsWith(":")) {
                    this.routeParams.put(curSegment.substring(1), otherSegment);
                } else if ((!curSegment.equals(otherSegment)) && (!curSegment.equals("*"))) {
                    throw new IllegalStateException("The 2 routes should be checked if they matched with url.matches() before invoking this method");
                }
            }
        }

        /**
         * getMethod
         * gets the request method
         * @return the request method
         */
        public RequestMethod getMethod() {
            return this.method;
        }

        /**
         * getUrl
         * gets the request url
         * @return the request url
         */
        public String getUrl() {
            return this.url;
        }

        /**
         * getLocation
         * gets the request location
         * @return the request location
         */
        public String getLocation() {
            return this.location;
        }

        /**
         * getProtocol
         * gets the request protocol
         * @return the request protocol
         */
        public String getProtocol() {
            return this.protocol;
        }

        /**
         * getQueryParams
         * gets the query parameters in a map
         * @return the query parameters
         */
        public Map<String, String> getQueryParams() {
            return this.queryParams;
        }

        /**
         * getRouteParams
         * gets the route parameters in a map
         * @return the route parameters
         * @throws IllegalStateException if the request parameters have not been populated before invoking this method
         */
        public Map<String, String> getRouteParams() {
            if (this.routeParams == null) {
                throw new IllegalStateException("populateRequestParams() should be invoked calling this method");
            }

            return this.routeParams;
        }

        /**
         * toString
         * converts the status line to a string
         * @return the status line in string representation
         */
        @Override
        public String toString() {
            return "StatusLine{" +
                    "method=" + method +
                    ", url='" + url + '\'' +
                    ", protocol='" + protocol + '\'' +
                    ", location='" + location + '\'' +
                    ", queryParams=" + queryParams +
                    ", routeParams=" + routeParams +
                    '}';
        }
    }
}
