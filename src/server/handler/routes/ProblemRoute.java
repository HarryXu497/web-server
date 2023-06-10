package server.handler.routes;

import database.Database;
import database.model.Problem;
import database.model.User;
import server.handler.Handler;
import server.handler.NotFoundException;
import server.handler.methods.Get;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;
import template.TemplateEngine;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Responsible for handling the `/problem/:problemId` route.
 * Displays the information for one problem
 * @author Harry Xu
 * @version 1.0 - June 9th 2023
 */
public class ProblemRoute extends Handler implements Get {

    /** The template engine which contains and compiles the templates */
    private final TemplateEngine templateEngine;

    /** The database which contains application state */
    private final Database database;

    /**
     * Constructs a ProblemRoute handler
     * @param templateEngine the template engine which holds and compiles the templates
     * @param database the database which holds persisted application state
     */
    public ProblemRoute(TemplateEngine templateEngine, Database database) {
        this.templateEngine = templateEngine;
        this.database = database;
    }

    /**
     * get
     * Handles the GET request on the request's url.
     * Serves the `problem.th` template file.
     * @param req the HTTP request to handle
     * @return the server HTTP response
     */
    @Override
    public Response get(Request req) {
        // Get problem id
        String problemIdRaw = req.getStatusLine().getRouteParams().get("problemId");

        int problemId;

        try {
            problemId = Integer.parseInt(problemIdRaw);
        } catch (NumberFormatException e) {
            throw new NotFoundException("problem with id " + problemIdRaw + " cannot be found");
        }

        // Fetch problem from id
        Problem problemFromDB;

        try {
            problemFromDB = this.database.problems().getProblemById(problemId);
        } catch (SQLException e) {
            // No Results (i.e. Problem not found)
            if (e.getErrorCode() == 0) {
                throw new NotFoundException("Problem with id " + problemId + " not found");
            }

            throw new RuntimeException(e);
        }

        // Authenticate user
        User currentUser = this.database.users().getCurrentUserFromRequest(req);

        // Whether to show the solved text
        boolean showSolved = false;

        if (currentUser != null) {
            List<Integer> userSolved = this.database.solvedProblems().getAllSolvedProblems(currentUser.getUserID());
            showSolved = userSolved.contains(problemId);
        }

        // Compile template with data
        String body = this.templateEngine.compile("frontend/templates/problem.th", new Data(
                problemFromDB,
                showSolved,
                currentUser
        ));

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
     * Exposes data as public properties for reflection
     * @author Harry Xu
     * @version 1.0 - June 8th 2023
     */
    public class Data {
        /** Problem id */
        public int id;

        /** Problem title */
        public String name;

        /** Problem content */
        public String content;

        /** Problem type */
        public String type;

        /** Problem difficulty from 1 to 10 inclusive */
        public int difficulty;

        /** Name of the problem's author */
        public String authorName;

        /** The text to display if the user has solved the problem before */
        public String solvedByUserText;

        /** Change navbar based on authentication status */
        public boolean loggedIn;

        /** The points the user has */
        public int points;

        /**
         * Constructs this data container class.
         * @param problem the problem represented by this URL
         * @param solvedByUser whether this problem has been solved by the user before
         * @param currentUser the logged-in user or null if no user logged-in
         */
        public Data(Problem problem, boolean solvedByUser, User currentUser) {
            // Problem data
            this.id = problem.getProblemID();
            this.name = problem.getTitle();
            this.content = problem.getContent();
            this.type = problem.getType();
            this.difficulty = problem.getDifficulty();

            // Fetch author name
            try {
                this.authorName = database.users().getUserById(
                        problem.getAuthorID()
                ).getUserName();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // Create solved text
            if (solvedByUser) {
                this.solvedByUserText = "[SOLVED]";
            } else {
                this.solvedByUserText = "";
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
