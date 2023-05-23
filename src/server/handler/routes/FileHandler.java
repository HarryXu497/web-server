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

    /** The url on which to host the file */
    private final String directory;

    /**
     * constructs a File Handler which is used to handle static file assets
     * @param templateEngine the template engine which holds the static asset
     * @param directory the url on which to host the file
     */
    public FileHandler(TemplateEngine templateEngine, String directory) {
        this.templateEngine = templateEngine;
        this.directory = directory;
    }

    /**
     * get
     * handles the GET request on the request's url
     * @param req the HTTP request to handle
     * @return the server HTTP response
     */
    @Override
    public Response get(Request req) {
        // Full request URL
        String url = req.getStatusLine().getLocation();

        // Get file name
        String filename = url.substring(url.lastIndexOf("/") + 1);

        // Get file extension
        String extension = url.substring(url.lastIndexOf(".") + 1);

        // Appends file name to hosting directory
        String fileContent = this.templateEngine.getTemplate(this.directory + filename);

        // Headers
        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Type", "text/" + extension);
        headers.put("Content-Length", Integer.toString(fileContent.length()));
        headers.put("Cache-Control", "public, max-age=86400");
        headers.put("Vary", "Accept-Encoding");
        headers.put("Accept-Ranges", "none");

        return new Response(
                new Response.StatusLine(ResponseCode.OK),
                headers,
                fileContent
        );
    }
}
