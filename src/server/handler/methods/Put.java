package server.handler.methods;

import server.request.Request;
import server.response.Response;

public interface Put {
    Response put(Request req) throws Exception;
}
