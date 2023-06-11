package database.dao;

import database.model.User;
import database.statement.SQLStatement;
import server.request.Request;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Wraps raw SQL connections to the user table in a more accessible API
 * @author Tommy Shan
 * @version - June 1st 2023
 */
public class UserDatabase {
    /** JDBC URL to connect to the database */
    private static final String JDBC_URL = "jdbc:sqlite:user.db";

    /** Class name of the JDBC driver */
    private static final String JDBC_CLASS_NAME = "org.sqlite.JDBC";

    /**
     * Constructs the object and initializes its data.
     * Creates a users table if it does not exist
     * @throws SQLException if an error occurs while using SQL
     */
    public UserDatabase() throws SQLException {
        // Loads the JDBC driver
        try {
            Class.forName(JDBC_CLASS_NAME);
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
                Connection conn = DriverManager.getConnection(JDBC_URL);
                Statement statement = conn.createStatement()
        ) {
            statement.executeUpdate(sql);
        }
    }

    /**
     * addUser
     * Attempts to add a {@link User} to the database
     * @param user the user to insert into the database
     * @throws SQLException if an error occurs while using SQL
     */
    public void addUser(User user) throws SQLException {
        // Loads the JDBC driver
        try {
            Class.forName(JDBC_CLASS_NAME);
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
                Connection conn = DriverManager.getConnection(JDBC_URL);
                PreparedStatement statement = conn.prepareStatement(sql);
        ) {
            // Set parameters
            statement.setString(1, user.getUsername());
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
     * @return the user with the specified id or null if not found
     * @throws SQLException if an error occurs while using SQL
     */
    public User getUserById(int targetId) throws SQLException {
        // Load JDBC driver
        try {
            Class.forName(JDBC_CLASS_NAME);
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
                Connection conn = DriverManager.getConnection(JDBC_URL);
                PreparedStatement statement = conn.prepareStatement(sql);
        ) {
            // Set parameters
            statement.setInt(1, targetId);

            // Execute and get results
            try (ResultSet resultSet = statement.executeQuery()) {
                // Return null if no user found
                if (!resultSet.isBeforeFirst() ) {
                    return null;
                }

                // Get result fields
                int id = resultSet.getInt("ID");
                String username = resultSet.getString("USERNAME");
                String password = resultSet.getString("PASSWORD");
                byte[] salt = resultSet.getBytes("SALT");
                int points = resultSet.getInt("POINTS");

                return new User(id, username, password, salt, points);
            }
        }
    }

    /**
     * getByUsername
     * Attempts to retrieve a user with a specified username
     * @param targetUsername the username to search for
     * @return the found user or null if not found
     * @throws SQLException if an error occurs while using SQL
     */
    public User getUserByUsername(String targetUsername) throws SQLException {
        // Load JDBC driver
        try {
            Class.forName(JDBC_CLASS_NAME);
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
            Connection conn = DriverManager.getConnection(JDBC_URL);
            PreparedStatement statement = conn.prepareStatement(sql);
        ) {
            // Set parameters
            statement.setString(1, targetUsername);

            // Execute and get results
            try (ResultSet resultSet = statement.executeQuery()) {
                // Return null if no user found
                if (!resultSet.isBeforeFirst() ) {
                    return null;
                }

                // Get result fields
                int id = resultSet.getInt("ID");
                String username = resultSet.getString("USERNAME");
                String password = resultSet.getString("PASSWORD");
                byte[] salt = resultSet.getBytes("SALT");
                int points = resultSet.getInt("POINTS");

                return new User(id, username, password, salt, points);
            }
        }
    }

    /**
     * updatePoints
     * Attempts to update the points that a user has
     * @param userId the username to search for
     * @param newPoints the new amount of points the user has
     * @throws SQLException if an error occurs while using SQL
     */
    public void updatePoints(int userId, int newPoints) throws SQLException {
        // Load JDBC driver
        try {
            Class.forName(JDBC_CLASS_NAME);
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
                Connection conn = DriverManager.getConnection(JDBC_URL);
                PreparedStatement statement = conn.prepareStatement(sql);
        ) {
            // Set parameters
            statement.setInt(1, newPoints);
            statement.setInt(2, userId);

            // Execute update
            statement.executeUpdate();
        }
    }

    /**
     * authenticate
     * Attempts to authenticate the user with the specified credentials and returns the current user
     * @param username the username of the user
     * @param hashedPassword the hashed and salted password of the user
     * @return the current user if the credentials are valid or
     *         null if no user is found or the credentials are invalid
     */
    public User authenticate(String username, String hashedPassword) {
        // Get user by username
        User requestedUser;

        try {
            requestedUser = this.getUserByUsername(username);
        } catch (SQLException e) {
            return null;
        }

        // Check if the hashed passwords match
        if ((requestedUser != null) && (requestedUser.getPassword().equals(hashedPassword))) {
            return requestedUser;
        }

        return null;
    }

    /**
     * login
     * Attempts to log in the user with the specified username and plaintext password.
     * It computes the hash of the passwords with the username's salt and checks for a match.
     * @param username the username of the user
     * @param password the plaintext password of the user
     * @return the requested user if the credentials are valid or
     *         null if no user is found or the credentials are invalid
     */
    public User login(String username, String password) {
        // Get requested user
        User requestedUser;

        try {
            requestedUser = this.getUserByUsername(username);
        } catch (SQLException e) {
            return null;
        }

        if (requestedUser == null) {
            return null;
        }

        // Compute hash using the requested user's salt
        byte[] salt = requestedUser.getSalt();

        String newHashedPassword = UserDatabase.hashPassword(password, salt);

        // Check for match
        if (requestedUser.getPassword().equals(newHashedPassword)) {
            return requestedUser;
        }

        return null;
    }

    /**
     * getCurrentUser
     * Gets the current user with information from a {@link Request}
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
     * @throws SQLException if an error occurs while using SQLows
     */
    public void createAdminUser(String username, String password) throws SQLException {
        byte[] salt = UserDatabase.getSalt();
        String hashedPassword = UserDatabase.hashPassword(password, salt);

        // Load JDBC driver
        try {
            Class.forName(JDBC_CLASS_NAME);
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

        // Prepare and execute statement
        try (
                Connection conn = DriverManager.getConnection(JDBC_URL);
                PreparedStatement statement = conn.prepareStatement(sql);
        ) {
            // Set parameters
            // Custom id
            statement.setInt(1, 1);
            statement.setString(2, username);
            statement.setString(3, hashedPassword);
            statement.setBytes(4, salt);
            statement.setInt(5, 0);

            statement.executeUpdate();
        }
    }

    /**
     * getSalt
     * Creates a secure random salt as bytes.
     * @return the salt as an array of bytes
     */
    public static byte[] getSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * hashPassword
     * Hashes a plaintext password with a given salt
     * @param password the plaintext password to compute the hash for
     * @param salt the salt to compute the hash with
     */
    public static String hashPassword(String password, byte[] salt) {
        // Get digest with SHA-256
        MessageDigest messageDigest;

        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Hashing algorithm SHA-256 cannot be found");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // Add salt
        messageDigest.update(salt);

        // Compute digest with SHA-256
        byte[] bytes = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));

        StringBuilder hashedPassword = new StringBuilder();

        // Converts the hash to hexadecimal string
        for (byte b : bytes) {
            hashedPassword.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }

        return hashedPassword.toString();
    }
}
