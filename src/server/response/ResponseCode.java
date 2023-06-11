package server.response;

/**
 * represents an HTTP response code and its associated message
 * @author Harry Xu
 * @version 1.0 - May 20th 2023
 */
public enum ResponseCode {
    /** 200 OK */
    OK(200, "OK"),
    /** 201 Created */
    CREATED(201, "Created"),
    /** 302 Found */
    FOUND(302, "Found"),
    /** 302 See Other */
    SEE_OTHER(303, "See Other"),
    /** 400 Bad Request */
    BAD_REQUEST(400, "Bad Request"),
    /** 401 Unauthorized */
    UNAUTHORIZED(401, "Unauthorized"),
    /** 403 Forbidden */
    FORBIDDEN(403, "Forbidden"),
    /** 404 Not Found */
    NOT_FOUND(404, "Not Found"),
    /** 500 Internal Server Error */
    INTERNAL_SERVER_ERROR(500, "Interval Server Error");

    /** HTTP response code */
    private final int code;

    /** HTTP response message*/
    private final String message;

    /**
     * Constructs an enum value with a code and message
     * @param code the response code
     * @param message the response message
     */
    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * getCode
     * gets the response code of the enum value
     * @return the HTTP response code.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status">Response Codes</a>
     */
    public int getCode() {
        return this.code;
    }

    /**
     * getMessage
     * gets the response message corresponding to the response code
     * @return the HTTP response message
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status">Response Codes</a>
     */
    public String getMessage() {
        return this.message;
    }
}
