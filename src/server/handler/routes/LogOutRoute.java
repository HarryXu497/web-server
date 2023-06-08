package server.handler.routes;

import server.handler.Handler;
import server.handler.methods.Post;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;

import java.util.HashMap;
import java.util.Map;

public class LogOutRoute extends Handler implements Post {
    @Override
    public Response post(Request req) {

        Map<String, String> headers = new HashMap<>();

        headers.put("Location", "http://localhost:5000");
        headers.put("Set-Cookie", "username=deleted; Max-Age=0\nSet-Cookie: password=deleted; Max-Age=0");

        return new Response(
                new Response.StatusLine(ResponseCode.SEE_OTHER),
                headers,
                ""
        );
    }
}
