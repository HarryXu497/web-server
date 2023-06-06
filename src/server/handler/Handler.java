package server.handler;

import server.request.Request;
import server.response.Response;
import server.handler.methods.Delete;
import server.handler.methods.Get;
import server.handler.methods.Post;
import server.handler.methods.Put;

import java.util.HashMap;
import java.util.Map;

/**
 * The superclass for all route handlers in a web application.
 * Any subclasses of this class should implement at least one of the HTTP method interfaces
 * @author Harry Xu
 * @version 1.0 - May 20th 2023
 */
public abstract class Handler {
    /**
     * handle
     * dispatches the correct handler implemented on a subclass based on the request
     * @param req the request from  to dispatch the handler for
     * @return the response from the dispatched handler
     * @throws HandlerException if no handler exists that can handle the specific request
     */
    public Response handle(Request req) throws HandlerException {
        switch (req.getStatusLine().getMethod()) {
            case GET: {
                if (this instanceof Get) {
                    return ((Get) this).get(req);
                }

                throw new HandlerException("GET handler not implemented on this route");
            }
            case POST: {
                if (this instanceof Post) {
                    return ((Post) this).post(req);
                }

                throw new HandlerException("POST handler not implemented on this route");
            }
            case PUT: {
                if (this instanceof Put) {
                    return ((Put) this).put(req);
                }

                throw new HandlerException("PUT handler not implemented on this route");
            }
            case DELETE: {
                if (this instanceof Delete) {
                    return ((Delete) this).delete(req);
                }

                throw new HandlerException("DELETE handler not implemented on this route");
            }
            default: {
                throw new HandlerException("No handler implemented on this route");
            }
        }
    }

    public static Map<String, String> htmlHeaders() {
        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Type", "text/html; charset=iso-8859-1");
        headers.put("Vary", "Accept-Encoding");
        headers.put("Accept-Ranges", "none");

        return headers;
    }
}
