package server.handler.routes;

import coderunner.CodeRunner;
import coderunner.Submission;
import coderunner.Task;
import coderunner.Utils;
import database.Database;
import database.model.User;
import filter.Filter;
import server.handler.Handler;
import server.handler.NotFoundException;
import server.handler.methods.Get;
import server.handler.methods.Post;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;
import template.TemplateEngine;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for handling the problem submission route ("/problems/:problemId/submit")
 * @author Harry Xu
 * @version 1.0 - May 30th 2023
 */
public class SubmitRoute extends Handler implements Get, Post {

    private final TemplateEngine templateEngine;
    private final CodeRunner codeRunner;
    private final Database database;

    public SubmitRoute(TemplateEngine templateEngine, CodeRunner codeRunner, Database database) {
        this.templateEngine = templateEngine;
        this.codeRunner = codeRunner;
        this.database = database;
    }

    @Override
    public Response get(Request req) {
        // Authenticate user
        User currentUser = this.database.users().getCurrentUserFromRequest(req);

        // Do not allow access -> redirect to login page
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
        String body = this.templateEngine.compile("frontend/templates/submit.th", new Data(currentUser));

        // Headers
        Map<String, String> headers = Handler.htmlHeaders();

        return new Response(
                new Response.StatusLine(ResponseCode.OK),
                headers,
                body
        );
    }

    @Override
    public Response post(Request req) {
        // Get code from request
        String code;

        try {
            code = URLDecoder.decode(req.getBody().get("code"), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // This should never happen - value came from JDK's StandardCharsets
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // Headers
        Map<String, String> headers = new HashMap<>();

        // Default error response
        Response errorResponse = new Response(
                new Response.StatusLine(ResponseCode.INTERNAL_SERVER_ERROR),
                headers,
                ""
        );

        // Get user hashed password
        String password = req.getCookies().get("password");

        // Authenticate
        User currentUser = this.database.users().getCurrentUserFromRequest(req);

        // User authenticated
        if (currentUser != null) {
            // Get problem id
            String problemId = req.getStatusLine().getRouteParams().get("problemId");

            // Queue submission
            try {
                this.codeRunner.enqueue(new Submission(
                        new Task(
                                code,
                                "problems/" + problemId + "/Main.java",
                                Utils.allFilesInDirectory("problems/" + problemId + "/input"),
                                Utils.allFilesInDirectory("problems/" + problemId + "/output"),
                                Utils.allFilesInDirectory("problems/" + problemId + "/answers")
                        ),
                        password,
                        Integer.parseInt(problemId)
                ));
            } catch (IOException e) {
                return errorResponse;
            } catch (NumberFormatException e) {
                throw new NotFoundException("Problem with id " + problemId + " not found");
            }

            // Redirect to the tests page
            headers.put("Location", "http://localhost:5000/problems/" + problemId + "/tests");
        } else {
            // Redirect to log in if not authenticated
            headers.put("Location", "http://localhost:5000/log-in?next=" + req.getStatusLine().getLocation());
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
