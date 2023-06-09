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

public class AboutRoute extends Handler implements Get {

    /** The template engine which contains and compiles the templates */
    private final TemplateEngine templateEngine;

    /** The database used to create and store users */
    private final Database database;

    /**
     * constructs a HomeRoute handler
     * @param templateEngine the template engine which holds and compiles the templates
     */
    public AboutRoute(TemplateEngine templateEngine, Database database) {
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
        // Authenticate user
        User currentUser = this.database.users().getCurrentUserFromRequest(req);

        // Compile template with data
        String body = this.templateEngine.compile("frontend/templates/about.th", new Data(currentUser));

        // Headers
        Map<String, String> headers = Handler.htmlHeaders();

        return new Response(
                new Response.StatusLine(ResponseCode.NOT_FOUND),
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
