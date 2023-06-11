package server.request;

/**
 * Represents the possible requests methods of an HTTP request
 * @author Harry Xu
 * @version 1.0 - May 21st 2023
 */
public enum RequestMethod {
    /** GET request */
    GET,
    /** POST request */
    POST,
    /** PUT request */
    PUT,
    /** DELETE request */
    DELETE,
    /** HEAD request */
    HEAD,
    /** CONNECT request */
    CONNECT,
    /** OPTIONS request */
    OPTIONS,
    /** TRACE request */
    TRACE,
    /** PATCH request */
    PATCH,
}
