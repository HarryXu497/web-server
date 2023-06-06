package server.handler.routes;

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

    /**
     * constructs a ProblemRoute handler
     * @param templateEngine the template engine which holds and compiles the templates
     */
    public ProblemRoute(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public Response get(Request req) {
        int id = Integer.parseInt(req.getStatusLine().getRouteParams().get("problemId"));

        System.out.println(id);

        // Compile template with data
        String body = this.templateEngine.compile("frontend/templates/problem.th", new Data(
                ProblemsDB.db.getById(id)
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

    public static class Data {
        public int id;
        public String name;
        public String content;
        public String type;
        public int submissions;
        public String rate;

        public Data(ProblemsDB.Problem problem) {
            this.id = problem.id;
            this.name = problem.name;
            this.content = problem.content;
            this.type = problem.type;
            this.submissions = problem.submissionCount;
            this.rate = problem.successRateAsString;
        }
    }

}
