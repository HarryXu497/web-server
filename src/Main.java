import assets.AssetEngine;
import coderunner.CodeRunner;
import database.Database;
import server.WebServer;
import server.handler.Handler;
import server.handler.routes.AboutRoute;
import server.handler.routes.HomeRoute;
import server.handler.routes.LogInRoute;
import server.handler.routes.LogOutRoute;
import server.handler.routes.NotFoundRoute;
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

/**
 * A web application for problem/contest hosting and judging code
 * written by Tommy Shan and Harry Xu for the ICS3U6
 * Computer Science course at Richmond Hill High School.
 * This project contains:
 * <ul>
 *     <li>A multithreaded web server with a custom design inspired by Spring Boot, Angular, and NestJS</li>
 *     <li>A custom templating language heavily inspired by Svelte and EJS</li>
 *     <li>An asset and template engine to hold and compile assets and templates</li>
 *     <li>A class to compile, run, and test submitted Java code in a background thread</li>
 *     <li>A filter to prevent malicious code from being inserted into submitted code</li>
 *     <li>A backend database with SQLite to allow for data persistence</li>
 * </ul>
 * @author Tommy Shan and Harry Xu
 * @version 1.0 - June 11th 2023
 */
public class Main {
    /** Resets the color of the console */
    private static final String ANSI_RESET = "\u001B[0m";

    /** Sets the color of the console output to red */
    private static final String ANSI_RED = "\u001B[31m";

    public static void main(String[] args) {
        try {
            // Instantiate singleton dependencies
            TemplateEngine templateEngine = new TemplateEngine("frontend/templates");

            AssetEngine assetEngine = new AssetEngine("frontend/styles", "frontend/images", "frontend/js", "frontend/favicon");

            CodeRunner codeRunner = new CodeRunner();

            Database database = new Database();

            // Initialize problems and admin
            String username = "Tommy_Shan";

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
            assets.put("frontend/favicon/", "/");

            WebServer server = new WebServer(templateEngine, assetEngine, routes, assets, notFoundHandler);

            server.serve(5000, port -> System.out.println(ANSI_RED + "[INFO] Accepting clients on port " + port + ANSI_RESET));
        } catch (Exception e) {
            System.out.println("An Error occurred");
            e.printStackTrace();
        }
    }
}