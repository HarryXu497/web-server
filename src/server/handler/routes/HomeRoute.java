package server.handler.routes;

import database.Database;
import database.model.User;
import server.request.Request;
import server.response.Response;
import server.handler.Handler;
import server.handler.methods.Get;
import server.response.ResponseCode;
import template.TemplateEngine;

import java.util.Map;

/**
 * Responsible for handling the root route ("/")
 * @author Harry Xu
 * @version 1.0 - May 30th 2023
 */
public class HomeRoute extends Handler implements Get {

    /** The template engine which contains and compiles the templates */
    private final TemplateEngine templateEngine;

    /** The database used to create and store users */
    private final Database database;

    /**
     * Constructs a HomeRoute handler
     * @param templateEngine the template engine which holds and compiles the templates
     * @param database the data which holds application state, including the authenticated user
     */
    public HomeRoute(TemplateEngine templateEngine, Database database) {
        this.templateEngine = templateEngine;
        this.database = database;
    }

    /**
     * get
     * Handles the GET request on the request's url.
     * Serves the `index.th` template file.
     * @param req the HTTP request to handle
     * @return the server HTTP response
     */
    @Override
    public Response get(Request req) {
        // Authenticate user
        User currentUser = this.database.users().getCurrentUserFromRequest(req);

        // Compile template with data
        String body = this.templateEngine.compile("frontend/templates/index.th", new Data(currentUser));

        // Headers
        Map<String, String> headers = Handler.htmlHeaders();

        return new Response(
                new Response.StatusLine(ResponseCode.OK),
                headers,
                body
        );
    }

    /**
     * Container for template data.
     * Exposes data as public properties for reflection
     * @author Harry Xu
     * @version 1.0 - May 23rd 2023
     */
    public static class Data {
        /** Whether the user is logged in */
        public boolean loggedIn;

        /** The points of the logged-in user or -1 if there is no user */
        public int points;

        /**
         * Constructs this data container class
         * @param currentUser the logged-in user or null if there is no user logged-in
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
