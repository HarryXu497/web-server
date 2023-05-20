package server.handler.routes;

import server.handler.Handler;
import server.handler.methods.Get;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;
import template.TemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class FileHandler extends Handler implements Get {

    private final TemplateEngine templateEngine;
    private final String directory;

    public FileHandler(TemplateEngine templateEngine, String directory) {
        this.templateEngine = templateEngine;
        this.directory = directory;
    }

    @Override
    public Response get(Request req) throws Exception {

        Map<String, String> headers = new HashMap<>();

        String url = req.getStatusLine().getLocation();
        String extension = url.substring(url.lastIndexOf(".") + 1);

        String location = req.getStatusLine().getLocation();
        String fileContent = this.templateEngine.getTemplate(this.directory + location.substring(location.lastIndexOf("/") + 1));

        headers.put("Content-Type", "text/" + extension);
        headers.put("Content-Length", Integer.toString(fileContent.length()));

        return new Response(
                new Response.StatusLine(ResponseCode.OK),
                headers,
                fileContent);
    }
}
