package server.handler.methods;

import server.request.Request;
import server.response.Response;

/**
 * represents a handler that can handle a PUT HTTP request
 * @author Harry Xu
 * @version 1.0 - May 20th 2023
 */
public interface Put {
    /**
     * put
     * Handles a PUT HTTP request
     * @param req the HTTP request
     * @return the HTTP response to the request
     */
    Response put(Request req);
}
