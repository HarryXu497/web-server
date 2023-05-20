package server.request;

import server.handler.URL;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private final StatusLine statusLine;
    private final Map<String, String> headers;
    private final Map<String, String> body;

    public Request(StatusLine statusLine, Map<String, String> headers, Map<String, String> body) {
        this.headers = headers;
        this.body = body;
        this.statusLine = statusLine;
    }

    public StatusLine getStatusLine() {
        return this.statusLine;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public Map<String, String> getBody() {
        return this.body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "statusLine=" + statusLine +
                ", headers=" + headers +
                ", body=" + body +
                '}';
    }

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
        List<String> rawHeaders = lines.subList(1, lines.size() - 2);
        Map<String, String> headers = new HashMap<>();

        for (String line : rawHeaders) {
            String key = line.substring(0, line.indexOf(" "));
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

                body.put(splitPair[0], splitPair[1]);
            }
        }

        // Request body
        return new Request(statusLine, headers, body);
    }

    public static class StatusLine {
        private final RequestMethod method;
        private final String URL;
        private final String protocol;
        private final String location;
        private final Map<String, String> queryParams;
        private Map<String, String> routeParams;

        public StatusLine(RequestMethod method, String URL, String protocol) {
            this.method = method;
            this.URL = URL;
            this.protocol = protocol;

            String[] splitUrl = this.URL.split("\\?");

            this.location = splitUrl[0];
            this.queryParams = new HashMap<>();

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
         */
        public void populateRequestParams(URL url, String route) {
            this.routeParams = new HashMap<>();

            String[] routeSegments = route.split("/");
            String[] urlSegments = url.getPathSegments();

            System.out.println(Arrays.toString(routeSegments));
            System.out.println(Arrays.toString(urlSegments));

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

        public RequestMethod getMethod() {
            return this.method;
        }

        public String getURL() {
            return this.URL;
        }

        public String getProtocol() {
            return this.protocol;
        }

        public Map<String, String> getQueryParams() {
            return this.queryParams;
        }

        public String getLocation() {
            return this.location;
        }

        public Map<String, String> getRouteParams() {
            if (this.routeParams == null) {
                throw new IllegalStateException("populateRequestParams() should be invoked calling this method");
            }

            return this.routeParams;
        }

        @Override
        public String toString() {
            return "StatusLine{" +
                    "method=" + method +
                    ", URL='" + URL + '\'' +
                    ", protocol='" + protocol + '\'' +
                    ", location='" + location + '\'' +
                    ", queryParams=" + queryParams +
                    ", routeParams=" + routeParams +
                    '}';
        }
    }
}
