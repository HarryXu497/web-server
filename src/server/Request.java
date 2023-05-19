package server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private final Map<String, String> headers;
    private final String body;
    private final StatusLine statusLine;

    public Request(Map<String, String> headers, String body, StatusLine statusLine) {
        this.headers = headers;
        this.body = body;
        this.statusLine = statusLine;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public String getBody() {
        return this.body;
    }

    public StatusLine getStatusLine() {
        return this.statusLine;
    }

    @Override
    public String toString() {
        return "Request{" +
                "headers=" + this.headers +
                ", body='" + this.body + '\'' +
                ", statusLine=" + this.statusLine +
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
        List<String> rawHeaders = lines.subList(1, lines.size() - 1);
        Map<String, String> headers = new HashMap<>();

        for (String line : rawHeaders) {
            String key = line.substring(0, line.indexOf(" "));
            String value = line.substring(line.indexOf(" ") + 1);

            headers.put(key, value);
        }

        // Request body
        return new Request(headers, "", statusLine);
    }
}
