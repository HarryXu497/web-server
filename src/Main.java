import assets.AssetEngine;
import server.WebServer;
import server.handler.Handler;
import server.handler.routes.HomeRoute;
import template.TemplateEngine;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            TemplateEngine templateEngine = new TemplateEngine("frontend/templates");

            AssetEngine assetEngine = new AssetEngine("frontend/styles");

            // Routes
            LinkedHashMap<String, Handler> routes = new LinkedHashMap<>();

            routes.put("/", new HomeRoute(templateEngine));
            routes.put("/:id", new HomeRoute(templateEngine));

            // Assets
            Map<String, String> assets = new HashMap<>();
            assets.put("frontend/styles/", "/static/css/");

            WebServer server = new WebServer(templateEngine, assetEngine, routes, assets);

            server.serve(5000);
        } catch (Exception e) {
            System.out.println("An Error occurred");
            e.printStackTrace();
        }
    }
}