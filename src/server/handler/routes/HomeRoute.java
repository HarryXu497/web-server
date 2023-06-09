package server.handler.routes;

import database.Database;
import database.model.User;
import server.request.Request;
import server.response.Response;
import server.handler.Handler;
import server.handler.methods.Get;
import server.response.ResponseCode;
import template.TemplateEngine;

import java.util.HashMap;
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
     * constructs a HomeRoute handler
     * @param templateEngine the template engine which holds and compiles the templates
     * @param database the data which holds application state, including the authenticated user
     */
    public HomeRoute(TemplateEngine templateEngine, Database database) {
        this.templateEngine = templateEngine;
        this.database = database;
    }

    /**
     * get
     * handles the GET request on the request's url
     * @param req the HTTP request to handle
     * @return the server HTTP response
     */
    @Override
    public Response get(Request req) {

        User currentUser = this.database.users().getCurrentUserFromRequest(req);

        // Compile template with data
        String body = this.templateEngine.compile("frontend/templates/index.th", new Data(currentUser));

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

    /**
     * container for the template data
     * @author Harry Xu
     * @version 1.0 - May 23rd 2023
     */
    public static class Data {
        public boolean loggedIn;
        public int points;

        /**
         * constructs this data container class
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
