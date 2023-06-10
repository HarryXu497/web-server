package server.handler.routes;

import assets.AssetEngine;
import server.handler.Handler;
import server.handler.methods.Get;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles all assets and files which must be statically hosted
 * @author Harry Xu
 * @version 1.0 - May 21st 2023
 */
public class FileHandler extends Handler implements Get {

    /** The template engine which holds all the asset files */
    private final AssetEngine assets;

    /** The url on which to host the file */
    private final String directory;

    /**
     * Constructs a File Handler which is used to handle static file assets
     * @param assets the asset engine which holds the static asset
     * @param directory the url on which to host the file
     */
    public FileHandler(AssetEngine assets, String directory) {
        this.assets = assets;
        this.directory = directory;
    }

    /**
     * get
     * Handles the GET request on the request's url.
     * Serves the requested static asset file.
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
        byte[] fileContent = this.assets.getAsset(this.directory + filename);

        // Headers
        Map<String, String> headers = new HashMap<>();

        String contentType = "text/" + extension;

        switch (extension) {
            case "jpg":
            case "jpeg":
                contentType = "image/jpeg";
                break;
            case "ico":
                contentType = "image/x-icon";
                break;
        }

        headers.put("Content-Type", contentType);
        headers.put("Content-Length", Integer.toString(fileContent.length));
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
