package database.dao;

import database.model.User;
import database.statement.SQLStatement;
import server.request.Request;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Map;

public class UserDatabase {
    public UserDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:user.db");
                Statement stm = c.createStatement()
            ) {
                String sql = "CREATE TABLE IF NOT EXISTS USERLIST " +
                        "(ID INTEGER PRIMARY KEY NOT NULL, " +
                        "USERNAME TEXT UNIQUE, " +
                        "SALT BLOB NOT NULL," +
                        "PASSWORD TEXT NOT NULL," +
                        "POINTS INT NOT NULL);";
                stm.executeUpdate(sql);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addUser(User user) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        String sql = SQLStatement.insertStatement()
                .insertInto("USERLIST")
                .columns("USERNAME", "PASSWORD", "SALT", "POINTS")
                .values("?", "?", "?", "?")
                .toString();

        try (
                Connection conn = DriverManager.getConnection("jdbc:sqlite:user.db");
                PreparedStatement statement = conn.prepareStatement(sql);
        ) {

            statement.setString(1, user.getUserName());
            statement.setString(2, user.getPassword());
            statement.setBytes(3, user.getSalt());
            statement.setInt(4, user.getPoints());

            statement.executeUpdate();
        }

    }

    public User getUserById(int targetId) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String sql = SQLStatement.selectStatement()
                .select("*")
                .from("USERLIST")
                .where("ID = ?")
                .toString();


        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:user.db");
                PreparedStatement statement = c.prepareStatement(sql);
            ) {

            statement.setInt(1, targetId);

            try (ResultSet results = statement.executeQuery()) {
                int id = results.getInt("ID");
                String userName = results.getString("USERNAME");
                String password = results.getString("PASSWORD");
                byte[] salt = results.getBytes("SALT");
                int points = results.getInt("POINTS");

                return new User(id, userName, password, salt, points);
            }
        }

    }

    public User getByUsername(String username) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        String sql = SQLStatement.selectStatement()
                .select("*")
                .from("USERLIST")
                .where("USERNAME = ?")
                .toString();

        try (
            Connection c = DriverManager.getConnection("jdbc:sqlite:user.db");
            PreparedStatement statement = c.prepareStatement(sql);
        ) {

            statement.setString(1, username);

            try (ResultSet results = statement.executeQuery()) {
                int id = results.getInt("ID");
                String userName = results.getString("USERNAME");
                String password = results.getString("PASSWORD");
                byte[] salt = results.getBytes("SALT");
                int points = results.getInt("POINTS");

                return new User(id, userName, password, salt, points);
            }
        }
    }

    public void updatePoints(int userId, int newPoints) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        String sql = SQLStatement.updateStatement()
                .update("USERLIST")
                .columns("POINTS")
                .values("?")
                .where("ID = ?")
                .toString();

        try (
                Connection conn = DriverManager.getConnection("jdbc:sqlite:user.db");
                PreparedStatement statement = conn.prepareStatement(sql);
        ) {

            statement.setInt(1, newPoints);
            statement.setInt(2, userId);

            statement.executeUpdate();
        }
    }

    public User authenticate(String username, String hashedPassword) {
        User requestedUser;

        try {
            requestedUser = this.getByUsername(username);
        } catch (SQLException e) {
            return null;
        }

        if (requestedUser.getPassword().equals(hashedPassword)) {
            return requestedUser;
        }

        return null;
    }

    public User login(String username, String password) {

        User requestedUser;

        try {
            requestedUser = this.getByUsername(username);
        } catch (SQLException e) {
            return null;
        }

        byte[] salt = requestedUser.getSalt();

        String newHashedPassword = UserDatabase.hashPassword(password, salt);

        if (requestedUser.getPassword().equals(newHashedPassword)) {
            return requestedUser;
        }

        return null;
    }

    /**
     * getCurrentUser
     * gets the current user with information from a {@link Request}
     * @param req the request with user authentication information
     * @return the current {@link User} or null if there is no user logged in
     */
    public User getCurrentUserFromRequest(Request req) {
        // Get request body
        Map<String, String> cookies = req.getCookies();

        // Get username and password
        String username = cookies.get("username");
        String password = cookies.get("password");

        return this.authenticate(username, password);
    }

    /**
     * createAdminUser
     * Creates an admin user with a username and password.
     * All problems on the judge may to attributed to this user
     * @param username the username of the admin
     * @param password the plaintext password of the admin
     */
    public void createAdminUser(String username, String password) throws NoSuchAlgorithmException, SQLException {
        byte[] salt = UserDatabase.getSalt();
        String hashedPassword = UserDatabase.hashPassword(password, salt);

        // Load JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Insert with custom id of 1
        String sql = SQLStatement.insertStatement()
                .insertInto("USERLIST")
                .orReplace()
                .columns("ID", "USERNAME", "PASSWORD", "SALT", "POINTS")
                .values("?", "?", "?", "?", "?")
                .toString();

        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:user.db");
                PreparedStatement stm = c.prepareStatement(sql);
        ) {
            // Set parameters
            // Custom id
            stm.setInt(1, 1);
            stm.setString(2, username);
            stm.setString(3, hashedPassword);
            stm.setBytes(4, salt);
            stm.setInt(5, 0);

            stm.executeUpdate();
        }
    }

    public static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public static String hashPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);

            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hashedPassword = new StringBuilder();

            for (byte b : bytes) {
                hashedPassword.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }

            return hashedPassword.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
