package server.handler.routes;

import database.Database;
import database.dao.UserDatabase;
import database.model.Role;
import database.model.User;
import server.handler.Handler;
import server.handler.methods.Get;
import server.handler.methods.Post;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;
import template.TemplateEngine;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SignUpRoute extends Handler implements Get, Post {

    /** The template engine which holds all templates */
    private final TemplateEngine templateEngine;

    /** The database used to create and store users */
    private final Database database;

    public SignUpRoute(TemplateEngine templateEngine, Database database) {
        this.templateEngine = templateEngine;
        this.database = database;
    }

    @Override
    public Response get(Request req) {
        Map<String, String> headers = Handler.htmlHeaders();

        String body = this.templateEngine.compile("frontend/templates/sign-up.th", null);


        return new Response(
                new Response.StatusLine(ResponseCode.OK),
                headers,
                body
        );
    }

    @Override
    public Response post(Request req) {

        Map<String, String> body = req.getBody();

        String username = body.get("username");
        String password = body.get("password");

        String hashedPassword;
        byte[] salt;

        try {
            salt = UserDatabase.getSalt();
            hashedPassword = UserDatabase.hashPassword(password, salt);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        StringBuilder saltAsString = new StringBuilder();

        for (byte b : salt) {
            saltAsString.append(b);
        }

        this.database.users().addUser(new User(
                username,
                -1, // User id does not matter for insert
                0,
                0,
                0,
                Role.USER,
                hashedPassword,
                saltAsString.toString()
        ));

        Map<String, String> headers = new HashMap<>();

        headers.put("Location", "http://localhost:5000/");
        headers.put("Set-Cookie", "username=" + username +"; Secure\nSet-Cookie: password=" + hashedPassword + "; Secure");

        return new Response(
                new Response.StatusLine(ResponseCode.SEE_OTHER),
                headers,
                ""
        );
    }
}
