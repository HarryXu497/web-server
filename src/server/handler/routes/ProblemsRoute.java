package server.handler.routes;

import server.handler.Handler;
import server.handler.methods.Get;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;
import template.TemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProblemsRoute extends Handler implements Get {

    /** The template engine which contains and compiles the templates */
    private final TemplateEngine templateEngine;

    /**
     * constructs a ProblemsRoute handler
     * @param templateEngine the template engine which holds and compiles the templates
     */
    public ProblemsRoute(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public Response get(Request req) {
        // Compile template with data
        String body = this.templateEngine.compile("frontend/templates/problems.th", new Data(
                ProblemsDB.db.getAll()
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

    /**
     * container for the template data
     * @author Harry Xu
     * @version 1.0 - May 23rd 2023
     */
    public static class Data {
        public List<ProblemsDB.Problem> problems;

        public Data(List<ProblemsDB.Problem> problems) {
            this.problems = problems;
        }
    }
}
