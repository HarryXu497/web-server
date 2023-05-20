package server.handler.methods;

import server.request.Request;
import server.response.Response;

public interface Get {
    Response get(Request req) throws Exception;
}
