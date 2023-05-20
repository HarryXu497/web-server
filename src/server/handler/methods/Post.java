package server.handler.methods;

import server.request.Request;
import server.response.Response;

public interface Post {
    Response post(Request req) throws Exception;
}
