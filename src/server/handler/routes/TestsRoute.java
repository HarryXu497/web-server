package server.handler.routes;

import database.Database;
import database.model.User;
import server.handler.Handler;
import server.handler.methods.Get;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;
import template.TemplateEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for handling the `/problems/:problemId/tests` route
 * Returns an empty shell which is populated by a JavaScript file
 * @author Harry Xu
 * @version 1.0 - June 9th 2023
 */
public class TestsRoute extends Handler implements Get {

    /** The template engine which contains and compiles the templates */
    private final TemplateEngine templateEngine;

    /** The database which holds users for user auth*/
    private final Database database;

    /**
     * Constructs a TestsRoute handler
     * @param templateEngine the template engine which holds and compiles the templates
     * @param database the database which holds persisted application state
     */
    public TestsRoute(TemplateEngine templateEngine, Database database) {
        this.templateEngine = templateEngine;
        this.database = database;
    }

    /**
     * get
     * Handles the GET request on the request's url.
     * Serves the `tests.th` template file.
     * @param req the HTTP request to handle
     * @return the server HTTP response
     */
    @Override
    public Response get(Request req) {
        // Authenticate user
        User currentUser = this.database.users().getCurrentUserFromRequest(req);

        // User not authenticated -> redirect to login page
        if (currentUser == null) {
            Map<String, String> redirectHeaders = new HashMap<>();

            redirectHeaders.put("Location", "http://localhost:5000/log-in?next=" + req.getStatusLine().getLocation());

            return new Response(
                    new Response.StatusLine(ResponseCode.FOUND),
                    redirectHeaders,
                    ""
            );
        }

        // Compile template with data
        String body = this.templateEngine.compile("frontend/templates/tests.th", new Data(currentUser));

        // Headers
        Map<String, String> headers = Handler.htmlHeaders();

        return new Response(
                new Response.StatusLine(ResponseCode.OK),
                headers,
                body
        );
    }

    /**
     * Container class for template data
     * @author Harry Xu
     * @version 1.0 - June 8th 2023
     */
    public static class Data {
        /** if the user is authenticated */
        public boolean loggedIn;

        /** the points that the authenticated user has or -1 if there is no logged-in user*/
        public int points;

        /**
         * Constructs this container class
         * @param currentUser the current user if authenticated or null otherwise
         */
        public Data(User currentUser) {
            this.loggedIn = currentUser != null;

            if (this.loggedIn) {
                this.points = currentUser.getPoints();
            } else {
                this.points = -1;
            }
        }
    }
}
