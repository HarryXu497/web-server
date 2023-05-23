package server.handler.methods;

import server.request.Request;
import server.response.Response;

/**
 * represents a handler that can handle a GET HTTP request
 * @author Harry Xu
 * @version 1.0 - May 20th 2023
 */
public interface Get {
    /**
     * get
     * Handles a GET HTTP request
     * @param req the HTTP request
     * @return the HTTP response to the request
     */
    Response get(Request req);
}
