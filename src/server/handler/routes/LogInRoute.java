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

/**
 * Responsible for handling the `/log-in` route
 * Handles both the get and post requests to this route
 * @author Harry Xu
 * @version 1.0 - June 9th 2023
 */
public class LogInRoute extends Handler implements Get, Post {

    /** The template engine which holds all templates */
    private final TemplateEngine templateEngine;

    /** The database used to create and store users */
    private final Database database;

    /**
     * Constructs a LogInRoute handler with its dependencies
     * @param templateEngine the template engine used to compile templates
     * @param database the database to authenticate and login users with
     * */
    public LogInRoute(TemplateEngine templateEngine, Database database) {
        this.templateEngine = templateEngine;
        this.database = database;
    }

    /**
     * get
     * Handles the GET request on the request's url.
     * Serves the `log-in.th` template file.
     * @param req the HTTP request to handle
     * @return the server HTTP response
     */
    @Override
    public Response get(Request req) {
        // Headers
        Map<String, String> headers = Handler.htmlHeaders();

        // Get error code
        String errorCode = req.getStatusLine().getQueryParams().get("error");

        // Authenticate user
        User currentUser = this.database.users().getCurrentUserFromRequest(req);

        // Compile template with data
        String body = this.templateEngine.compile("frontend/templates/log-in.th", new Data(errorCode, currentUser));

        return new Response(
                new Response.StatusLine(ResponseCode.OK),
                headers,
                body
        );
    }

    /**
     * post
     * Handles the POST request on the request's url.
     * Logs in the user to the application.
     * @param req the HTTP request to handle
     * @return the server HTTP response
     */
    @Override
    public Response post(Request req) {
        // Get request body
        Map<String, String> body = req.getBody();

        // Get username and password
        String username = body.get("username");
        String password = body.get("password");

        // Log in user
        User currentUser = this.database.users().login(username, password);

        // Headers
        Map<String, String> headers = new HashMap<>();

        // User is authenticated
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
            // User cannot be authenticated
            // Show error message
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
     * Exposes data as public properties for reflection
     * @author Harry Xu
     * @version 1.0 - June 8th 2023
     */
    public static class Data {
        /** Error message for the sign-up form */
        public String errorMessage;

        /** Change navbar based on authentication status */
        public boolean loggedIn;

        /** The points the user has */
        public int points;

        /**
         * Constructs a data container object with an error code
         * @param errorCode the vendor-specific SQLite error code
         * @param currentUser the logged-in user, or null if not logged-in
         */
        public Data(String errorCode, User currentUser) {
            // Username exists already
            if (errorCode == null) {
                this.errorMessage = "";
            } else {
                this.errorMessage = "<div class=\"error-message\">Something Went Wrong</div>";
            }

            // User auth status related fields
            this.loggedIn = currentUser != null;

            if (this.loggedIn) {
                this.points = currentUser.getPoints();
            } else {
                this.points = -1;
            }
        }
    }
}
