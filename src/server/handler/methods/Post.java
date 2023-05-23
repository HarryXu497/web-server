package server.handler.methods;

import server.request.Request;
import server.response.Response;

/**
 * represents a handler that can handle a POST HTTP request
 * @author Harry Xu
 * @version 1.0 - May 20th 2023
 */
public interface Post {
    /**
     * post
     * Handles a POST HTTP request
     * @param req the HTTP request
     * @return the HTTP response to the request
     */
    Response post(Request req);
}
