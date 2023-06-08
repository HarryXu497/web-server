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

import java.sql.SQLException;
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

        String errorCode = req.getStatusLine().getQueryParams().get("error");

        String body = this.templateEngine.compile("frontend/templates/log-in.th", new Data(errorCode));

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

        // Log in user
        User currentUser = this.database.users().login(username, password);

        if (currentUser != null) {
            // Redirect to `next` query parameter
            String redirectTo = req.getStatusLine().getQueryParams().get("next");

            // Redirect to home page if no `next` query parameter
            if (redirectTo == null) {
                redirectTo = "";
            }

            headers.put("Location", "http://localhost:5000" + redirectTo);
            headers.put("Set-Cookie", "username=" + username + "; Secure\nSet-Cookie: password=" + currentUser.getPassword() + "; Secure");
        } else {
            // Server error
            headers.put("Location", "http://localhost:5000/log-in?error=1");
        }

        return new Response(
                new Response.StatusLine(ResponseCode.SEE_OTHER),
                headers,
                ""
        );
    }

    /**
     * Container class for template data
     * @author Harry Xu
     * @version 1.0 - June 8th 2023
     */
    public static class Data {
        /** Error message for the sign-up form*/
        public String errorMessage;

        /**
         * Constructs a data container object with an error code
         * @param errorCode the vendor-specific SQLite error code
         */
        public Data(String errorCode) {
            // Username exists already
            if (errorCode == null) {
                this.errorMessage = "";
            } else {
                this.errorMessage = "<div class=\"error-message\">Something Went Wrong</div>";
            }
        }
    }
}
