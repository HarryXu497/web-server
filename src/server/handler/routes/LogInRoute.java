package server.handler.routes;

import database.Database;
import database.model.User;
import server.handler.Handler;
import server.handler.methods.Get;
import server.handler.methods.Post;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;
import template.TemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class LogInRoute extends Handler implements Get, Post {

    /** The template engine which holds all templates */
    private final TemplateEngine templateEngine;

    /** The database used to create and store users */
    private final Database database;

    public LogInRoute(TemplateEngine templateEngine, Database database) {
        this.templateEngine = templateEngine;
        this.database = database;
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
        Map<String, String> body = req.getBody();

        // Headers
        Map<String, String> headers = new HashMap<>();

        // Get username and password
        String username = body.get("username");
        String password = body.get("password");

        User authUser = this.database.users().login(username, password);

        if (authUser != null) {
            // Redirect to `next` query parameter
            String redirectTo = req.getStatusLine().getQueryParams().get("next");

            // Redirect to home page if no `next` query parameter
            if (redirectTo == null) {
                redirectTo = "";
            }

            headers.put("Location", "http://localhost:5000" + redirectTo);
            headers.put("Set-Cookie", "username=" + username + "; Secure\nSet-Cookie: password=" + authUser.getPassword() + "; Secure");
        } else {
            headers.put("Location", "http://localhost:5000/log-in?error=2");
        }

        return new Response(
                new Response.StatusLine(ResponseCode.SEE_OTHER),
                headers,
                ""
        );
    }
}
