import assets.AssetEngine;
import coderunner.CodeRunner;
import database.Database;
import database.model.User;
import server.WebServer;
import server.handler.Handler;
import server.handler.routes.*;
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

            // Initialize problems and admin
            String username = "Harry_Xu";

            database.problems().populateFromDirectory("problems");
            database.users().createAdminUser(username, "cheese");

            database.solvedProblems().initializeSolvedProblems(database.problems().getAllProblems());

            // Routes
            LinkedHashMap<String, Handler> routes = new LinkedHashMap<>();

            routes.put("/", new HomeRoute(templateEngine, database));
            routes.put("/problems/", new ProblemsRoute(templateEngine, database));
            routes.put("/problems/:problemId", new ProblemRoute(templateEngine, database));
            routes.put("/problems/:problemId/submit", new SubmitRoute(templateEngine, codeRunner, database));
            routes.put("/problems/:problemId/tests", new TestsRoute(templateEngine, database));
            routes.put("/problems/:problemId/submissions", new SubmissionPollRoute(codeRunner, database));
            routes.put("/about", new AboutRoute(templateEngine, database));
            routes.put("/sign-up", new SignUpRoute(templateEngine, database));
            routes.put("/log-in", new LogInRoute(templateEngine, database));
            routes.put("/log-out", new LogOutRoute());

            // Not found handler
            Handler notFoundHandler = new NotFoundRoute(templateEngine, database);

            // Assets
            Map<String, String> assets = new HashMap<>();
            assets.put("frontend/styles/", "/static/css/");
            assets.put("frontend/images/", "/static/images/");
            assets.put("frontend/js/", "/static/js/");

            WebServer server = new WebServer(templateEngine, assetEngine, routes, assets, notFoundHandler);

            server.serve(5000);
        } catch (Exception e) {
            System.out.println("An Error occurred");
            e.printStackTrace();
        }
    }
}