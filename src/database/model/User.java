package database.model;

public class User {
    private int userID;
    private String username;
    private byte[] salt;
    private String password;

    /**
     * Constructor for class database.model.User
     * @param username the username of the user
     * @param userID the id of the user
     */
    public User(int userID, String username, String password, byte[] salt) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.salt = salt;
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
     * getPassword
     * Getter method for salt, stored as a string
     * @return variable password
     */
    public byte[] getSalt() {
        return this.salt;
    }
}
