package server.handler.routes;

import coderunner.CodeRunner;
import coderunner.Submission;
import coderunner.Task;
import coderunner.Utils;
import database.Database;
import database.model.Problem;
import database.model.User;
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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for handling the problem submission route ("/problems/:problemId/submit")
 * @author Harry Xu
 * @version 1.0 - May 30th 2023
 */
public class SubmitRoute extends Handler implements Get, Post {

    /** The template engine which holds all templates */
    private final TemplateEngine templateEngine;

    /** The code runner instance running the submitted code */
    private final CodeRunner codeRunner;

    /** The database used to authenticate and manage users */
    private final Database database;

    /**
     * Constructs a SubmitRoute with its dependencies
     * @param templateEngine the template engine which holds and compiles the templates
     * @param codeRunner the object responsible for compiling, executing, and testing submitted code
     * @param database the database which holds persisted application state
     */
    public SubmitRoute(TemplateEngine templateEngine, CodeRunner codeRunner, Database database) {
        this.templateEngine = templateEngine;
        this.codeRunner = codeRunner;
        this.database = database;
    }

    /**
     * get
     * Handles the GET request on the request's url.
     * Serves the `submit.th` template file.
     * @param req the HTTP request to handle
     * @return the server HTTP response
     */
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

        // Get problem id
        String problemIdRaw = req.getStatusLine().getRouteParams().get("problemId");

        int problemId;

        try {
            problemId = Integer.parseInt(problemIdRaw);
        } catch (NumberFormatException e) {
            throw new NotFoundException("Problem with id " + problemIdRaw + " not found");
        }

        // Fetch problem
        Problem problem;

        try {
            problem = this.database.problems().getProblemById(problemId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (problem == null) {
            throw new NotFoundException("Problem with id " + problemId + " not found");
        }

        // Compile template with data
        String body = this.templateEngine.compile("frontend/templates/submit.th", new Data(currentUser, problem.getTitle()));

        // Headers
        Map<String, String> headers = Handler.htmlHeaders();

        return new Response(
                new Response.StatusLine(ResponseCode.OK),
                headers,
                body
        );
    }

    /**
     * post
     * Handles the POST request on the request's url
     * Submits code to the code runner to be compiled, run, and tested.
     * @param req the HTTP request to handle
     * @return the server HTTP response
     */
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
     * Exposes data as public properties for reflection
     * @author Harry Xu
     * @version 1.0 - June 8th 2023
     */
    public static class Data {
        /** if the user is authenticated */
        public boolean loggedIn;

        /** the points that the authenticated user has or -1 if there is no logged-in user */
        public int points;

        /** The name of the problem being submitted to */
        public String problemName;

        /**
         * Constructs this container class
         * @param currentUser the current user if authenticated or null otherwise
         * @param problemName the name of the problem
         */
        public Data(User currentUser, String problemName) {
            this.problemName = problemName;

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
