package server.handler.routes;

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

    /**
     * constructs a HomeRoute handler which is used to handle static file assets
     * @param templateEngine the template engine which holds and compiles the templates
     */
    public HomeRoute(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * get
     * handles the GET request on the request's url
     * @param req the HTTP request to handle
     * @return the server HTTP response
     */
    @Override
    public Response get(Request req) {
        // TODO: remove this -> just for demonstration of query and route params
        // Extract data from URL
        // Query Params
        boolean isSignedIn = req.getStatusLine().getQueryParams().containsKey("isSignedIn");

        // Route Params
        String id = req.getStatusLine().getRouteParams().get("id");

        // Compile template with data
        String body = this.templateEngine.compile("frontend/templates/index.th", new Data(isSignedIn, id));

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
        public boolean isSignedIn;
        public boolean idExists;
        public String id;

        /**
         * constructs this data container class
         * @param isSignedIn whether the user is signed in
         * @param id the id route param
         */
        public Data(boolean isSignedIn, String id) {
            this.isSignedIn = isSignedIn;
            this.id = id;
            this.idExists = id != null;
        }
    }
}
