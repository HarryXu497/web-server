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


public class ProblemsRoute extends Handler implements Get {

    /** The template engine which contains and compiles the templates */
    private final TemplateEngine templateEngine;

    /** The database which contains application state */
    private final Database database;

    /**
     * constructs a ProblemsRoute handler
     * @param templateEngine the template engine which holds and compiles the templates
     */
    public ProblemsRoute(TemplateEngine templateEngine, Database database) {
        this.templateEngine = templateEngine;
        this.database = database;
    }

    @Override
    public Response get(Request req) {
        // Authenticate user
        User currentUser = this.database.users().getCurrentUserFromRequest(req);

        List<Integer> solvedList = null;

        // User authenticated
        if (currentUser != null) {
            solvedList = this.database.solvedProblems().getAllSolvedProblems(currentUser.getUserID());
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
     * container for the template data
     * @author Harry Xu
     * @version 1.0 - May 23rd 2023
     */
    public static class Data {
        public List<TemplateProblem> problems;
        public boolean isError;
        public boolean loggedIn;
        public int points;

        public Data(List<TemplateProblem> problems, User currentUser) {
            // If error, create empty iterable and set error flag to true
            if (problems == null) {
                this.isError = true;
                problems = new ArrayList<>();
            }

            this.problems = problems;

            this.loggedIn = currentUser != null;

            if (this.loggedIn) {
                this.points = currentUser.getPoints();
            } else {
                this.points = -1;
            }
        }
    }

    /**
     * A wrapper of a problem which makes its properties available for templating
     * @author Harry Xu
     * @version 1.0 - June 6th 2023
     */
    public class TemplateProblem {
        public String title;
        public int difficulty;
        public String content;
        public String type;
        public int id;
        public String authorName;
        public String solvedStatus;

        public TemplateProblem(Problem problem, boolean solvedByUser) {
            this.title = problem.getTitle();
            this.difficulty = problem.getDifficulty();
            this.content = problem.getContent();
            this.id = problem.getProblemID();
            this.type = problem.getType();

            if (solvedByUser) {
                this.solvedStatus = " [Solved]";
            } else {
                this.solvedStatus = "";
            }

            try {
                this.authorName = database.users().getUserById(
                        problem.getAuthorID()
                ).getUserName();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
