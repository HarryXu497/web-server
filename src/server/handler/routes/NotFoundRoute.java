package server.handler.routes;

import database.Database;
import database.model.User;
import server.handler.Handler;
import server.handler.methods.Get;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;
import template.TemplateEngine;

import java.util.Map;

/**
 * Responsible for handling any routes that are not mapped to any routes
 * @author Harry Xu
 * @version 1.0 - June 9th 2023
 */
public class NotFoundRoute extends Handler implements Get {

    /** The template engine which contains and compiles the templates */
    private final TemplateEngine templateEngine;

    /** The database used to create and store users */
    private final Database database;

    /**
     * Constructs a HomeRoute handler
     * @param templateEngine the template engine which holds and compiles the templates
     * @param database the database which holds persisted application state
     */
    public NotFoundRoute(TemplateEngine templateEngine, Database database) {
        this.templateEngine = templateEngine;
        this.database = database;
    }

    /**
     * get
     * Handles the GET request on the request's url.
     * Serves the `not-found.th` template file.
     * @param req the HTTP request to handle
     * @return the server HTTP response
     */
    @Override
    public Response get(Request req) {
        // Authenticate user
        User currentUser = this.database.users().getCurrentUserFromRequest(req);

        // Compile template with data
        String body = this.templateEngine.compile("frontend/templates/not-found.th", new Data(currentUser));

        // Headers
        Map<String, String> headers = Handler.htmlHeaders();

        return new Response(
                new Response.StatusLine(ResponseCode.NOT_FOUND),
                headers,
                body
        );
    }

    /**
     * Container class for template data
     * Exposes data as public properties for reflection
     * @author Harry Xu
     * @version 1.0 - June 8th 2023
     */
    public static class Data {
        /** Change navbar based on authentication status */
        public boolean loggedIn;

        /** The points the user has */
        public int points;

        /**
         * Constructs this data container class
         * @param currentUser the logged-in user or null if no user logged-in
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
