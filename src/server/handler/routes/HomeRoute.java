package server.handler.routes;

import server.request.Request;
import server.response.Response;
import server.handler.Handler;
import server.handler.methods.Get;
import server.response.ResponseCode;

import java.util.HashMap;
import java.util.Map;

public class HomeRoute extends Handler implements Get {
    @Override
    public Response get(Request req) {

        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Type", "text/html; charset=iso-8859-1");

        return new Response(
                new Response.StatusLine(ResponseCode.OK),
                headers,
                "<h1>Hello!</h1>"
        );
    }
}
