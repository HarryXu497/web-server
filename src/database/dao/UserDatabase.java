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
                        "PASSWORD TEXT);";
                stm.executeUpdate(sql);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addUser(User u) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        String sql = SQLStatement.insertStatement()
                .insertInto("USERLIST")
                .columns("USERNAME", "PASSWORD", "SALT")
                .values("?", "?", "?")
                .toString();

        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:user.db");
                PreparedStatement stm = c.prepareStatement(sql);
        ) {

            stm.setString(1, u.getUserName());
            stm.setString(2, u.getPassword());
            stm.setBytes(3, u.getSalt());

            stm.executeUpdate();
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

            try (ResultSet rs = statement.executeQuery()) {
                int id = rs.getInt("ID");
                String userName = rs.getString("USERNAME");
                String password = rs.getString("PASSWORD");
                byte[] salt = rs.getBytes("SALT");

                return new User(id, userName, password, salt);
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

            try (ResultSet resultSet = statement.executeQuery()) {
                int id = resultSet.getInt("ID");
                String userName = resultSet.getString("USERNAME");
                String password = resultSet.getString("PASSWORD");
                byte[] salt = resultSet.getBytes("SALT");

                return new User(id, userName, password, salt);
            }
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
}
