package server.handler.routes;

import server.handler.Handler;
import server.handler.methods.Get;
import server.handler.methods.Post;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;
import template.TemplateEngine;

import java.util.Map;

public class LogInRoute extends Handler implements Get, Post {

    private final TemplateEngine templateEngine;

    public LogInRoute(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public Response get(Request req) {
        Map<String, String> headers = Handler.htmlHeaders();

        String body = this.templateEngine.compile("frontend/templates/log-in.th", null);


        return new Response(
                new Response.StatusLine(ResponseCode.OK),
                headers,
                body
        );
    }

    @Override
    public Response post(Request req) {
        throw new RuntimeException("Not Implemented.");
    }
}
