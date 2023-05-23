package server.handler.routes;

import server.request.Request;
import server.response.Response;
import server.handler.Handler;
import server.handler.methods.Get;
import server.response.ResponseCode;
import template.TemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class HomeRoute extends Handler implements Get {

    private final TemplateEngine templateEngine;

    public HomeRoute(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public Response get(Request req) {

        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Type", "text/html; charset=iso-8859-1");
        headers.put("Vary", "Accept-Encoding");
        headers.put("Accept-Ranges", "none");
        headers.put("Set-Cookie", "beans=bagels");

        boolean isSignedIn = req.getStatusLine().getQueryParams().containsKey("isSignedIn");
        String id = req.getStatusLine().getRouteParams().get("id");

        String body = this.templateEngine.compile("frontend/templates/index.th", new Data(isSignedIn, id));

        return new Response(
                new Response.StatusLine(ResponseCode.OK),
                headers,
                body
        );
    }

    public static class Data {
        public boolean isSignedIn;
        public boolean idExists;
        public String id;

        public Data(boolean isSignedIn, String id) {
            this.isSignedIn = isSignedIn;
            this.id = id;
            this.idExists = id != null;
        }
    }
}
