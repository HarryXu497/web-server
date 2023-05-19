package server.response;

import server.request.RequestMethod;

import java.util.Map;

public class Response {

    private final StatusLine statusLine;
    private final Map<String, String> headers;
    private final String body;

    public Response(StatusLine statusLine, Map<String, String> headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;

        if (body.length() > 0) {
            this.headers.put("Content-Length", Integer.toString(body.length()));
        }
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(this.statusLine);

        for (Map.Entry<String, String> header : this.headers.entrySet()) {
            res.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append("\n");
        }

        res.append("\n");
        res.append(this.body);

        return res.toString();
    }

    public static class StatusLine {
        private static final String DEFAULT_PROTOCOL = "HTTP/1.1";
        private final String protocol;
        private final ResponseCode code;

        public StatusLine(ResponseCode code) {
            this(DEFAULT_PROTOCOL, code);
        }

        public StatusLine(String protocol, ResponseCode code) {
            this.protocol = protocol;
            this.code = code;
        }

        @Override
        public String toString() {
            return this.protocol + " " + this.code.getCode() + " " + this.code.getMessage() + "\n";
        }
    }
}
