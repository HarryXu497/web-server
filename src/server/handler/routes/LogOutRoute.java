package server.handler.routes;

import server.handler.Handler;
import server.handler.methods.Post;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for handling the `/log-out` route.
 * Handles the post request to log out of the application
 * @author Harry Xu
 * @version 1.0 - June 9th 2023
 */
public class LogOutRoute extends Handler implements Post {
    /**
     * post
     * Handles the POST request on the request's url
     * Logs the user out of the application
     * @param req the HTTP request to handle
     * @return the server HTTP response
     */
    @Override
    public Response post(Request req) {

        // Headers
        Map<String, String> headers = new HashMap<>();

        // Redirect and deleted cookies
        headers.put("Location", "http://localhost:5000");
        headers.put("Set-Cookie", "username=deleted; Max-Age=0\nSet-Cookie: password=deleted; Max-Age=0");

        return new Response(
                new Response.StatusLine(ResponseCode.SEE_OTHER),
                headers,
                ""
        );
    }
}
