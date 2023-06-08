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
import java.sql.SQLException;
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

        String errorCode = req.getStatusLine()
                .getQueryParams()
                .get("error");

        String body = this.templateEngine.compile("frontend/templates/sign-up.th", new Data(errorCode));


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

        Map<String, String> headers = new HashMap<>();

        if ((username.length() == 0) || (password.length() == 0)) {
            headers.put("Location", "http://localhost:5000/sign-up?error=3");

            return new Response(
                    new Response.StatusLine(ResponseCode.SEE_OTHER),
                    headers,
                    ""
            );
        }

        String hashedPassword;
        byte[] salt;

        try {
            salt = UserDatabase.getSalt();
            hashedPassword = UserDatabase.hashPassword(password, salt);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        try {
            this.database.users().addUser(new User(
                    -1, // User id does not matter for insert
                    username,
                    hashedPassword,
                    salt
            ));
        } catch (SQLException e) {
            // Username exists
            if (e.getErrorCode() == 19) {
                headers.put("Location", "http://localhost:5000/sign-up?error=2");
            } else {
                // Generic error
                headers.put("Location", "http://localhost:5000/sign-up?error=1");
            }

            return new Response(
                    new Response.StatusLine(ResponseCode.SEE_OTHER),
                    headers,
                    ""
            );
        }

        headers.put("Location", "http://localhost:5000/");
        headers.put("Set-Cookie", "username=" + username +"; Secure\nSet-Cookie: password=" + hashedPassword + "; Secure");

        return new Response(
                new Response.StatusLine(ResponseCode.SEE_OTHER),
                headers,
                ""
        );
    }

    /**
     * Container class for template data
     * @author Harry Xu
     * @version 1.0 - June 8th 2023
     */
    public static class Data {
        /** Error message for the sign-up form*/
        public String errorMessage;

        /**
         * Constructs a data container object with an error code
         * @param errorCode the vendor-specific SQLite error code
         */
        public Data(String errorCode) {
            // Username exists already
            if (errorCode == null) {
                this.errorMessage = "";
            } else {
                switch (errorCode) {
                    case "3":
                        this.errorMessage = "<div class=\"error-message\">Username or Password cannot be empty</div>";
                        break;

                    case "2":
                        this.errorMessage = "<div class=\"error-message\">Username already exists</div>";
                        break;

                    default:
                        this.errorMessage = "<div class=\"error-message\">Something Went Wrong</div>";
                        break;
                }
            }
        }
    }
}