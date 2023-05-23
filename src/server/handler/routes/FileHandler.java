package server.handler.routes;

import server.handler.Handler;
import server.handler.methods.Get;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;
import template.TemplateEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles all assets and files which must be statically hosted
 * @author Harry Xu
 * @version 1.0 - May 21st 2023
 */
public class FileHandler extends Handler implements Get {

    /** The template engine which holds all the asset files */
    private final TemplateEngine templateEngine;

    /** The directory of the file to host */
    private final String directory;

    public FileHandler(TemplateEngine templateEngine, String directory) {
        this.templateEngine = templateEngine;
        this.directory = directory;
    }

    @Override
    public Response get(Request req) {

        Map<String, String> headers = new HashMap<>();

        String url = req.getStatusLine().getLocation();
        String extension = url.substring(url.lastIndexOf(".") + 1);

        String location = req.getStatusLine().getLocation();
        String fileContent = this.templateEngine.getTemplate(this.directory + location.substring(location.lastIndexOf("/") + 1));

        headers.put("Content-Type", "text/" + extension);
        headers.put("Content-Length", Integer.toString(fileContent.length()));
        headers.put("Cache-Control", "public, max-age=86400");
        headers.put("Vary", "Accept-Encoding");
        headers.put("Accept-Ranges", "none");

        return new Response(
                new Response.StatusLine(ResponseCode.OK),
                headers,
                fileContent);
    }
}
