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

/**
 * Wraps raw SQL connections to the database in a more friendly API
 * @author Tommy Shan
 * @version - June 6th 2023
 * */
public class UserDatabase {
    /**
     * Constructs the object and initializes its data.
     * Creates a users table if it does not exist
     */
    public UserDatabase() throws SQLException {
        // Loads the JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Create SQL statement
        String sql = "CREATE TABLE IF NOT EXISTS USERLIST " +
                "(ID INTEGER PRIMARY KEY NOT NULL, " +
                "USERNAME TEXT UNIQUE, " +
                "SALT BLOB NOT NULL," +
                "PASSWORD TEXT NOT NULL," +
                "POINTS INT NOT NULL);";

        // Create and execute statement
        try (
            Connection conn = DriverManager.getConnection("jdbc:sqlite:user.db");
            Statement statement = conn.createStatement()
        ) {
            statement.executeUpdate(sql);
        }
    }

    /**
     * addUser
     * Attempts to add a {@link User} to the database
     * @param user the user to insert into the database
     * @throws SQLException the error thrown when the class does not exist
     */
    public void addUser(User user) throws SQLException {
        // Loads the JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Create SQL statement
        String sql = SQLStatement.insertStatement()
                .insertInto("USERLIST")
                .columns("USERNAME", "PASSWORD", "SALT", "POINTS")
                .values("?", "?", "?", "?")
                .toString();

        // Prepare and execute statement
        try (
                Connection conn = DriverManager.getConnection("jdbc:sqlite:user.db");
                PreparedStatement statement = conn.prepareStatement(sql);
        ) {
            // Set parameters
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getPassword());
            statement.setBytes(3, user.getSalt());
            statement.setInt(4, user.getPoints());

            // Execute update
            statement.executeUpdate();
        }

    }

    /**
     * getUserById
     * Attempts to get a {@link User} by its id
     * @param targetId the id of user to get
     * @return the user with the specified id
     * @throws SQLException if an error occurs while using SQL
     */
    public User getUserById(int targetId) throws SQLException {
        // Load JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Create SQL statement
        String sql = SQLStatement.selectStatement()
                .select("*")
                .from("USERLIST")
                .where("ID = ?")
                .toString();

        // Prepare and execute statement
        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:user.db");
                PreparedStatement statement = c.prepareStatement(sql);
        ) {
            // Set parameters
            statement.setInt(1, targetId);

            // Execute and get results
            try (ResultSet results = statement.executeQuery()) {
                // Get result fields
                int id = results.getInt("ID");
                String userName = results.getString("USERNAME");
                String password = results.getString("PASSWORD");
                byte[] salt = results.getBytes("SALT");
                int points = results.getInt("POINTS");

                return new User(id, userName, password, salt, points);
            }
        }
    }

    /**
     * getByUsername
     * Attempts to retrieve a user with a specified username
     * @param username the username to search for
     * @return the found user
     * @throws SQLException if an error occurs while using SQL
     */
    public User getUserByUsername(String username) throws SQLException {
        // Load JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Create SQL statement
        String sql = SQLStatement.selectStatement()
                .select("*")
                .from("USERLIST")
                .where("USERNAME = ?")
                .toString();

        // Prepare and execute statement
        try (
            Connection c = DriverManager.getConnection("jdbc:sqlite:user.db");
            PreparedStatement statement = c.prepareStatement(sql);
        ) {
            // Set parameters
            statement.setString(1, username);

            // Execute and get results
            try (ResultSet results = statement.executeQuery()) {
                // Get result fields
                int id = results.getInt("ID");
                String userName = results.getString("USERNAME");
                String password = results.getString("PASSWORD");
                byte[] salt = results.getBytes("SALT");
                int points = results.getInt("POINTS");

                return new User(id, userName, password, salt, points);
            }
        }
    }

    /**
     * updatePoints
     * Attempts to update the points that a user has
     * @param userId the username to search for
     * @throws SQLException if an error occurs while using SQL
     */
    public void updatePoints(int userId, int newPoints) throws SQLException {
        // Load JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Create SQL statement
        String sql = SQLStatement.updateStatement()
                .update("USERLIST")
                .columns("POINTS")
                .values("?")
                .where("ID = ?")
                .toString();

        // Prepare and execute statement
        try (
                Connection conn = DriverManager.getConnection("jdbc:sqlite:user.db");
                PreparedStatement statement = conn.prepareStatement(sql);
        ) {
            // Set parameters
            statement.setInt(1, newPoints);
            statement.setInt(2, userId);

            // Execute update
            statement.executeUpdate();
        }
    }

    public User authenticate(String username, String hashedPassword) {
        User requestedUser;

        try {
            requestedUser = this.getUserByUsername(username);
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
            requestedUser = this.getUserByUsername(username);
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
