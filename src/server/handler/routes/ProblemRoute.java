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
import java.util.HashMap;
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
        int problemId = Integer.parseInt(req.getStatusLine().getRouteParams().get("problemId"));

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

        // TODO: make this dynamic later
        // Authenticate user
        String username = req.getCookies().get("username");
        String hashedPassword = req.getCookies().get("password");

        User currentUser = this.database.users().authenticate(username, hashedPassword);

        boolean showSolved = false;

        if (currentUser != null) {
            List<Integer> userSolved = this.database.solvedProblems().getAllSolvedProblems(currentUser.getUserID());
            showSolved = userSolved.contains(problemId);
        }

        // Compile template with data
        String body = this.templateEngine.compile("frontend/templates/problem.th", new Data(
                problemFromDB,
                showSolved
        ));

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

    public class Data {
        public int id;
        public String name;
        public String content;
        public String type;
        public int difficulty;
        public String authorName;
        public String solvedByUserText;

        public Data(Problem problem, boolean solvedByUser) {
            this.id = problem.getProblemID();
            this.name = problem.getTitle();
            this.content = problem.getContent();
            this.type = problem.getType();
            this.difficulty = problem.getDifficulty();
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
        }
    }

}
