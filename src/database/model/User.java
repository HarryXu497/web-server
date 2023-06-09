package database.model;

public class User {
    private final int userID;
    private final String username;
    private final byte[] salt;
    private final String password;
    private final int points;

    /**
     * Constructor for this data container class
     * @param username the username of the user
     * @param userID the id of the user
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
     * Getter method for variable userID
     * @return variable userID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * getUserName
     * Getter method for variable userName
     * @return variable userName
     */
    public String getUserName() {
        return this.username;
    }

    /**
     * getPassword
     * Getter method for variable password
     * @return variable password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * getSalt
     * Gets the password salt, stored as bytes
     * @return stored salt
     */
    public byte[] getSalt() {
        return this.salt;
    }

    /**
     * getPoints
     * Gets the amount of points that the user has
     * @return stored salt
     */
    public int getPoints() {
        return this.points;
    }
}
