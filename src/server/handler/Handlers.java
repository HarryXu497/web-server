package server.handler;

import server.request.Request;
import server.response.Response;

import java.util.HashMap;
import java.util.Map;

public class Handlers {
    private final Map<URL, Handler> registry;

    public Handlers() {
        this.registry = new HashMap<>();
    }

    public void register(URL route, Handler handler) {
        this.registry.put(route, handler);
    }

    public void register(String route, Handler handler) {
        this.register(new URL(route), handler);
    }

    public Response dispatch(Request req) throws HandlerException {
        for (Map.Entry<URL, Handler> entry : this.registry.entrySet()) {
            if (entry.getKey().matches(req.getStatusLine().getURL())) {
                return entry.getValue().handle(req);
            }
        }

        throw new HandlerException("No handler can handler the request " + req);
    }
}
