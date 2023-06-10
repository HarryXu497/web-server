package database.model;

/**
 * Represents a user entity in the application
 * @author Tommy Shan
 * @version 1.0 - June 1st 2023
 */
public class User {
    /** The unique id of the user */
    private final int userID;

    /** The username id of the problem */
    private final String username;

    /** The hashed password of the user */
    private final String password;

    /** The salt used to hash the password */
    private final byte[] salt;

    /** The points that a user has */
    private final int points;

    /**
     * Constructs a {@link User} dataclass
     * @param userID the id of the user
     * @param username the username of the user
     * @param password the hashed password of the user
     * @param salt the salt used to hash the password
     * @param points the amount of points that the user has
     */
    public User(int userID, String username, String password, byte[] salt, int points) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.points = points;
    }

    /**
     * getUserID
     * Gets the id of the user
     * @return the user id
     */
    public int getUserID() {
        return this.userID;
    }

    /**
     * getUsername
     * Gets the username of the user
     * @return the user username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * getPassword
     * Gets the hashed and salted password of the user
     * @return the user password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * getSalt
     * Gets the salt used to hash the password, stored as bytes
     * @return the user salt
     */
    public byte[] getSalt() {
        return this.salt;
    }

    /**
     * getPoints
     * Gets the amount of points that the user has
     * @return the user points
     */
    public int getPoints() {
        return this.points;
    }
}
