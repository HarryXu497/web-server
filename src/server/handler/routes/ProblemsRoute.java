package server.handler.routes;

import database.Database;
import database.model.Problem;
import database.model.User;
import server.handler.Handler;
import server.handler.methods.Get;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;
import template.TemplateEngine;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Responsible for handling the `/problems` route.
 * Displays a list of all problems
 * @author Harry Xu
 * @version 1.0 - June 9th 2023
 */
public class ProblemsRoute extends Handler implements Get {

    /** The template engine which contains and compiles the templates */
    private final TemplateEngine templateEngine;

    /** The database which contains application state */
    private final Database database;

    /**
     * Constructs a ProblemsRoute handler
     * @param templateEngine the template engine which holds and compiles the templates
     * @param database the database which holds persisted application state
     */
    public ProblemsRoute(TemplateEngine templateEngine, Database database) {
        this.templateEngine = templateEngine;
        this.database = database;
    }

    /**
     * get
     * Handles the GET request on the request's url.
     * Serves the `problems.th` template file.
     * @param req the HTTP request to handle
     * @return the server HTTP response
     */
    @Override
    public Response get(Request req) {
        // Authenticate user
        User currentUser = this.database.users().getCurrentUserFromRequest(req);

        List<Integer> solvedList = null;

        // User authenticated
        if (currentUser != null) {
            try {
                solvedList = this.database.solvedProblems().getAllSolvedProblems(currentUser.getUserID());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        List<Problem> problems = null;

        // Get problems from database
        try {
            problems = this.database.problems().getAllProblems();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Transform problems for templating
        List<TemplateProblem> templateProblems = null;

        if (problems != null) {
            templateProblems = new ArrayList<>();

            for (Problem problem : problems) {
                boolean solved = (solvedList != null) && (solvedList.contains(problem.getProblemID()));

                templateProblems.add(new TemplateProblem(problem, solved));
            }
        }

        // Compile template with data
        String body = this.templateEngine.compile("frontend/templates/problems.th", new Data(
                templateProblems,
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
    public static class Data {
        /** List of problems for iteration in the template */
        public List<TemplateProblem> problems;

        /** If there is an error when loading the problems */
        public boolean isError;

        /** Change navbar based on authentication status */
        public boolean loggedIn;

        /** The points the user has */
        public int points;

        /**
         * Constructs this data container class.
         * @param problems the problems from the data
         * @param currentUser the logged-in user or null if no user logged-in
         */
        public Data(List<TemplateProblem> problems, User currentUser) {
            // If error, create empty iterable and set error flag to true
            if (problems == null) {
                this.isError = true;
                problems = new ArrayList<>();
            }

            this.problems = problems;

            // User auth status related fields
            this.loggedIn = currentUser != null;

            if (this.loggedIn) {
                this.points = currentUser.getPoints();
            } else {
                this.points = -1;
            }
        }
    }

    /**
     * A wrapper of a problem which makes its properties available for templating via reflection
     * @author Harry Xu
     * @version 1.0 - June 6th 2023
     */
    public class TemplateProblem {
        /** Problem id */
        public int id;

        /** Problem title */
        public String title;

        /** Problem content */
        public String content;

        /** Problem type */
        public String type;

        /** Problem difficulty from 1 to 10 inclusive */
        public int difficulty;

        /** Name of the problem's author */
        public String authorName;

        /** The text to display if the user has solved the problem before*/
        public String solvedStatus;

        /**
         * Constructs this problem data container class.
         * @param problem the problem represented by this URL
         * @param solvedByUser whether this problem has been solved by the user before
         */
        public TemplateProblem(Problem problem, boolean solvedByUser) {
            // Problem data
            this.id = problem.getProblemID();
            this.title = problem.getTitle();
            this.content = problem.getContent();
            this.type = problem.getType();
            this.difficulty = problem.getDifficulty();

            // Fetch author name
            try {
                this.authorName = database.users().getUserById(
                        problem.getAuthorID()
                ).getUsername();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (NullPointerException e) {
                throw new RuntimeException("User with author id " + problem.getAuthorID() + " cannot be found");
            }

            // Create solved text
            if (solvedByUser) {
                this.solvedStatus = " [Solved]";
            } else {
                this.solvedStatus = "";
            }

        }
    }
}
