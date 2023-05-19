package server.handler;

import server.request.Request;
import server.response.Response;
import server.handler.methods.Delete;
import server.handler.methods.Get;
import server.handler.methods.Post;
import server.handler.methods.Put;

public abstract class Handler {
    public Response handle(Request req) throws HandlerException {
        switch (req.getStatusLine().getMethod()) {
            case GET: {
                if (this instanceof Get) {
                    return ((Get) this).get(req);
                }

                throw new HandlerException("get handler not implemented on this route");
            }
            case POST: {
                if (this instanceof Post) {
                    return ((Post) this).post(req);
                }

                throw new HandlerException("post handler not implemented on this route");
            }
            case PUT: {
                if (this instanceof Post) {
                    return ((Put) this).put(req);
                }

                throw new HandlerException("post handler not implemented on this route");
            }
            case DELETE: {
                if (this instanceof Post) {
                    return ((Delete) this).delete(req);
                }

                throw new HandlerException("post handler not implemented on this route");
            }
            default: {
                throw new HandlerException("No handler implemented on this route");
            }

        }
    }
}
