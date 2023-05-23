package server.handler;

import server.request.Request;
import server.response.Response;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps {@link URL URL patterns} to different {@link Handler Handlers} and
 * allows them to be dispatched correctly.
 * @author Harry XU
 * @version 1.0 - May 21st 2023
 * */
public class Handlers {
    /** mapping of URL patterns to handlers */
    private final LinkedHashMap<URL, Handler> registry;

    /**
     * constructs a Handlers class with an empty registry
     */
    public Handlers() {
        this.registry = new LinkedHashMap<>();
    }

    /**
     * register
     * Registers a URL to Handler mapping
     * @param route the URL pattern
     * @param handler the route handler
     */
    public void register(URL route, Handler handler) {
        this.registry.put(route, handler);
    }

    /**
     * register
     * Registers a String URL pattern to a handler
     * @param route the URL pattern as a string
     * @param handler the route handler
     */
    public void register(String route, Handler handler) {
        this.register(new URL(route), handler);
    }

    /**
     * dispatch
     * Dispatches the first registered handler that matches the request and returns the response
     * @param req the request to handle
     * @return the response of the dispatched handler
     * @throws HandlerException if no handler is found that can handle the request
     */
    public Response dispatch(Request req) throws HandlerException {
        for (Map.Entry<URL, Handler> entry : this.registry.entrySet()) {
            URL url = entry.getKey();
            String route = req.getStatusLine().getLocation();

            if (url.matches(route)) {
                // Populate the request parameters
                req.getStatusLine().populateRequestParams(url, route);

                // TODO: remove this
                System.out.println(req.getStatusLine());

                // Dispatch the handle method
                return entry.getValue().handle(req);
            }
        }

        throw new HandlerException("No handler can handle the request " + req.getStatusLine());
    }

    /**
     * toString
     * converts this object to a string
     * @return a properly formatted string representation of this object
     */
    @Override
    public String toString() {
        return "Handlers" + registry;
    }
}
