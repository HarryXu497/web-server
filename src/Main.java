import assets.AssetEngine;
import coderunner.CodeRunner;
import database.Database;
import server.WebServer;
import server.handler.Handler;
import server.handler.routes.HomeRoute;
import server.handler.routes.LogInRoute;
import server.handler.routes.ProblemRoute;
import server.handler.routes.ProblemsRoute;
import server.handler.routes.SignUpRoute;
import server.handler.routes.SubmissionPollRoute;
import server.handler.routes.SubmitRoute;
import server.handler.routes.TestsRoute;
import template.TemplateEngine;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            TemplateEngine templateEngine = new TemplateEngine("frontend/templates");

            AssetEngine assetEngine = new AssetEngine("frontend/styles", "frontend/images", "frontend/js");

            CodeRunner codeRunner = new CodeRunner();

            Database database = new Database();

            // Routes
            LinkedHashMap<String, Handler> routes = new LinkedHashMap<>();

            routes.put("/", new HomeRoute(templateEngine));
            routes.put("/problems/", new ProblemsRoute(templateEngine, database));
            routes.put("/problems/:problemId", new ProblemRoute(templateEngine, database));
            routes.put("/problems/:problemId/submit", new SubmitRoute(templateEngine, codeRunner));
            routes.put("/problems/:problemId/tests", new TestsRoute(templateEngine, codeRunner));
            routes.put("/problems/:problemId/submissions", new SubmissionPollRoute(codeRunner));
            routes.put("/sign-up", new SignUpRoute(templateEngine, database));
            routes.put("/log-in", new LogInRoute(templateEngine));
            routes.put("/:id", new HomeRoute(templateEngine));

            // Assets
            Map<String, String> assets = new HashMap<>();
            assets.put("frontend/styles/", "/static/css/");
            assets.put("frontend/images/", "/static/images/");
            assets.put("frontend/js/", "/static/js/");

            WebServer server = new WebServer(templateEngine, assetEngine, routes, assets);

            server.serve(5000);
        } catch (Exception e) {
            System.out.println("An Error occurred");
            e.printStackTrace();
        }
    }
}