package server.response;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Represents an HTTP response, which can be stringified to send the response to the client.
 * An HTTP response consists of a status line, containing the protocol, status code, and status message,
 * a list of key value headers, and a text body.
 * @author Harry Xu
 * @version 1.0 - May 21st 2023
 */
public class Response {
    /** the status line, containing the protocol, status code, and message */
    private final StatusLine statusLine;

    /** the HTTP response headers */
    private final Map<String, String> headers;

    /** the HTTP response body */
    private final byte[] body;

    /**
     * constructs a response with a status line, headers, and a body
     * @param statusLine the HTTP response status line
     * @param headers a map of the HTTP response headers
     * @param body the HTTP response body
     */
    public Response(StatusLine statusLine, Map<String, String> headers, String body) {
        this(statusLine, headers, body.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * constructs a response with a status line, headers, and a body
     * @param statusLine the HTTP response status line
     * @param headers a map of the HTTP response headers
     * @param body the HTTP response body
     */
    public Response(StatusLine statusLine, Map<String, String> headers, byte[] body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;

        if (body.length > 0) {
            this.headers.put("Content-Length", Integer.toString(body.length));
        }
    }

    /**
     * toString
     * converts the HTTP response to a properly formatted HTTP response string,
     * allowing it to be sent to the client.
     * @return the HTTP response as a string
     */
    public byte[] toBytes() {
        StringBuilder res = new StringBuilder();

        // Status line
        res.append(this.statusLine);

        // Headers
        for (Map.Entry<String, String> header : this.headers.entrySet()) {
            res.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append("\r\n");
        }

        res.append("\r\n");

        byte[] statusAndHeaders = res.toString().getBytes(StandardCharsets.UTF_8);
        byte[] body = this.body;

        byte[] responseInBytes = new byte[statusAndHeaders.length + body.length];

        System.arraycopy(statusAndHeaders, 0, responseInBytes, 0, statusAndHeaders.length);

        System.arraycopy(body, 0, responseInBytes, statusAndHeaders.length, body.length);

        return responseInBytes;
    }

    /**
     * Represents the status line of an HTTP response, which is the first line in the response.
     * It contains the protocol, usually HTTP/1.1, the status code, and status message.
     * @author Harry Xu
     * @version 1.0 - May 21st 2023
     */
    public static class StatusLine {
        /** default protocol, usually HTTP/1.1 */
        private static final String DEFAULT_PROTOCOL = "HTTP/1.1";

        /** the HTTP response protocol */
        private final String protocol;

        /** an enum containing response code and response messages  */
        private final ResponseCode code;

        /**
         * Constructs a status line with a response code and message, and a default protocol
         * @param code the response code and message
         */
        public StatusLine(ResponseCode code) {
            this(DEFAULT_PROTOCOL, code);
        }

        /**
         * Constructs a status line with a response code and message, and a custom protocol
         * @param protocol the HTTP protocol of the response
         * @param code the response code and message
         */
        public StatusLine(String protocol, ResponseCode code) {
            this.protocol = protocol;
            this.code = code;
        }

        /**
         * toString
         * stringifies the status line into a formatted HTTP response status line
         */
        @Override
        public String toString() {
            return this.protocol + " " + this.code.getCode() + " " + this.code.getMessage() + "\r\n";
        }
    }
}
