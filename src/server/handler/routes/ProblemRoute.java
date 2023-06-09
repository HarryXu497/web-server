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

public class ProblemRoute extends Handler implements Get {

    /** The template engine which contains and compiles the templates */
    private final TemplateEngine templateEngine;

    /** The database which contains application state */
    private final Database database;

    /**
     * constructs a ProblemRoute handler
     * @param templateEngine the template engine which holds and compiles the templates
     */
    public ProblemRoute(TemplateEngine templateEngine, Database database) {
        this.templateEngine = templateEngine;
        this.database = database;
    }

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

    public class Data {
        public int id;
        public String name;
        public String content;
        public String type;
        public int difficulty;
        public String authorName;
        public String solvedByUserText;
        public boolean loggedIn;
        public int points;

        public Data(Problem problem, boolean solvedByUser, User currentUser) {
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

            if (solvedByUser) {
                this.solvedByUserText = "[SOLVED]";
            } else {
                this.solvedByUserText = "";
            }

            this.loggedIn = currentUser != null;

            if (this.loggedIn) {
                this.points = currentUser.getPoints();
            } else {
                this.points = -1;
            }
        }
    }

}
