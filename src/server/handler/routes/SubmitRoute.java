package server.handler.routes;

import coderunner.CodeRunner;
import coderunner.Submission;
import coderunner.Task;
import coderunner.Utils;
import database.Database;
import database.model.User;
import server.handler.Handler;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        String username = req.getCookies().get("username");
        String password = req.getCookies().get("password");

        User currentUser = this.database.users().authenticate(username, password);

        // Do not allow access -> redirect to login page
        if (currentUser == null) {
            Map<String, String> redirectHeaders = new HashMap<>();

            redirectHeaders.put("Location", "http://localhost:5000/log-in");

            return new Response(
                    new Response.StatusLine(ResponseCode.FOUND),
                    redirectHeaders,
                    ""
            );
        }

        // Compile template with data
        String body = this.templateEngine.compile("frontend/templates/submit.th", null);

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

        // Get user information
        String username = req.getCookies().get("username");
        String password = req.getCookies().get("password");

        // Authenticate
        User currentUser = this.database.users().authenticate(username, password);

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
                        password
                ));
            } catch (IOException e) {
                return errorResponse;
            }

            // Redirect to tests page
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
}
