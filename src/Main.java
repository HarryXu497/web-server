import assets.AssetEngine;
import coderunner.CodeRunner;
import coderunner.Utils;
import database.Database;
import database.dao.UserDatabase;
import database.model.Problem;
import database.model.User;
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

            byte[] salt = UserDatabase.getSalt();
            String password = UserDatabase.hashPassword("cheese", salt);


            database.users().addUser(
                    new User(
                            -1,
                            "Tommy_Shan",
                            password,
                            salt
                    )
            );

            database.problems().addProblem(
                    new Problem(
                            -1,
                            1,
                            "Trianglane",
                            "Tommy feels sleepy during geography class, so he decides to do his math homework. He is shocked when he opened his homework packaged that's assigned my Mr. Choi...\n" +
                            "\n" +
                            "It's too hard for him, a grade 9 student, to solve the problem. So he asked you to help him solve the problem for him.\n" +
                            "\n" +
                            "Given two numbers A and B, determine the value of A + B.",
                            "Simple Math",
                            1
                    )
            );

            Problem p = database.problems().getProblemByTitle("Trianglane");
            User u = database.users().getByUsername("Tommy_Shan");

            System.out.println(p.getTitle());
            System.out.println("\t" + p.getProblemID());
            System.out.println(u.getUserName());
            System.out.println("\t" + u.getUserID());

            database.solvedProblems().addTransaction(u.getUserID(), p.getProblemID());

            // Routes
            LinkedHashMap<String, Handler> routes = new LinkedHashMap<>();

            routes.put("/", new HomeRoute(templateEngine));
            routes.put("/problems/", new ProblemsRoute(templateEngine, database));
            routes.put("/problems/:problemId", new ProblemRoute(templateEngine, database));
            routes.put("/problems/:problemId/submit", new SubmitRoute(templateEngine, codeRunner, database));
            routes.put("/problems/:problemId/tests", new TestsRoute(templateEngine, codeRunner));
            routes.put("/problems/:problemId/submissions", new SubmissionPollRoute(codeRunner));
            routes.put("/sign-up", new SignUpRoute(templateEngine, database));
            routes.put("/log-in", new LogInRoute(templateEngine, database));
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