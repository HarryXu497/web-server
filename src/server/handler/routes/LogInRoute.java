package server.handler.routes;

import database.Database;
import database.dao.UserDatabase;
import database.model.User;
import server.handler.Handler;
import server.handler.methods.Get;
import server.handler.methods.Post;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;
import template.TemplateEngine;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LogInRoute extends Handler implements Get, Post {

    /** The template engine which holds all templates */
    private final TemplateEngine templateEngine;

    /** The database used to create and store users */
    private final Database database;

    public LogInRoute(TemplateEngine templateEngine, Database database) {
        this.templateEngine = templateEngine;
        this.database = database;
    }

    @Override
    public Response get(Request req) {
        Map<String, String> headers = Handler.htmlHeaders();

        String body = this.templateEngine.compile("frontend/templates/log-in.th", null);

        return new Response(
                new Response.StatusLine(ResponseCode.OK),
                headers,
                body
        );
    }

    @Override
    public Response post(Request req) {
        Map<String, String> body = req.getBody();

        // Headers
        Map<String, String> headers = new HashMap<>();

        // Get username and password
        String username = body.get("username");
        String password = body.get("password");

        User requestedUser;

        try {
            requestedUser = this.database.users().getByUsername(username);
        } catch (SQLException e) {
            // Redirect to log in page with error message
            headers.put("Location", "http://localhost:5000/log-in?error=1");
            return new Response(
                    new Response.StatusLine(ResponseCode.SEE_OTHER),
                    headers,
                    ""
            );
        }

        byte[] salt = requestedUser.getSalt();

        System.out.println("STORED" + requestedUser.getPassword() + " " + Arrays.toString(salt));
        System.out.println(UserDatabase.hashPassword(password, salt));

        boolean isAuthenticated = this.database.users().authenticate(requestedUser, UserDatabase.hashPassword(password, salt));

        if (isAuthenticated) {
            headers.put("Location", "http://localhost:5000/");
            headers.put("Set-Cookie", "username=" + username + "; Secure\nSet-Cookie: password=" + requestedUser.getPassword() + "; Secure");
        } else {
            headers.put("Location", "http://localhost:5000/log-in?error=2");
        }

        return new Response(
                new Response.StatusLine(ResponseCode.SEE_OTHER),
                headers,
                ""
        );
    }
}
