package server.handler.routes;

import server.handler.Handler;
import server.handler.methods.Get;
import server.handler.methods.Post;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;
import template.TemplateEngine;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for handling the problem submission route ("/problems/:problemId/submit")
 * @author Harry Xu
 * @version 1.0 - May 30th 2023
 */
public class SubmitRoute extends Handler implements Get, Post {

    private final TemplateEngine templateEngine;

    public SubmitRoute(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public Response get(Request req) {
        // Compile template with data
        String body = this.templateEngine.compile("frontend/templates/submit.th", null);

        // Headers
        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Type", "text/html; charset=iso-8859-1");
        headers.put("Vary", "Accept-Encoding");
        headers.put("Accept-Ranges", "none");

        return new Response(
                new Response.StatusLine(ResponseCode.OK),
                headers,
                body
        );
    }

    @Override
    public Response post(Request req) {

        String code;

        try {
            code = URLDecoder.decode(req.getBody().get("code"), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // This should never happen - value came from JDK's StandardCharsets
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        System.out.println(code);

        return null;
    }
}
