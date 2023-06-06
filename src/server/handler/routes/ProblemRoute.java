package server.handler.routes;

import database.Database;
import database.model.Problem;
import server.handler.Handler;
import server.handler.methods.Get;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;
import template.TemplateEngine;

import java.util.HashMap;
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
        int id = Integer.parseInt(req.getStatusLine().getRouteParams().get("problemId"));

        Problem problemFromDB = this.database.problems().getProblemById(id);

        // Compile template with data
        String body = this.templateEngine.compile("frontend/templates/problem.th", new Data(
                problemFromDB
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

        public Data(Problem problem) {
            this.id = problem.getProblemID();
            this.name = problem.getTitle();
            this.content = problem.getContent();
            this.type = problem.getType();
            this.difficulty = problem.getDifficulty();
            this.authorName = database.users().getUserById(
                    problem.getAuthorID()
            ).getUserName();
        }
    }

}
