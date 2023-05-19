package server.request;

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

        public StatusLine(RequestMethod method, String URL, String protocol) {
            this.method = method;
            this.URL = URL;
            this.protocol = protocol;
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

        @Override
        public String toString() {
            return "StatusLine{" +
                    "method=" + this.method +
                    ", URL='" + this.URL + '\'' +
                    ", status='" + this.protocol + '\'' +
                    '}';
        }
    }
}
