package database.dao;

import database.model.Role;
import database.model.User;
import database.statement.SQLStatement;

import javax.sql.rowset.serial.SerialBlob;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Arrays;

public class UserDatabase {
    public UserDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:user.db");
                Statement stm = c.createStatement()
            ) {
                String sql = "CREATE TABLE IF NOT EXISTS USERLIST " +
                        "(ID INTEGER PRIMARY KEY, " +
                        "USERNAME TEXT, " +
                        "SALT BLOB NOT NULL," +
                        "PASSWORD TEXT);";
                stm.executeUpdate(sql);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addUser(User u) {
        try {
            Class.forName("org.sqlite.JDBC");

            String sql = SQLStatement.insertStatement()
                    .insertInto("USERLIST")
                    .columns("USERNAME", "PASSWORD", "SALT")
                    .values("?", "?", "?")
                    .toString();

            try (
                    Connection c = DriverManager.getConnection("jdbc:sqlite:user.db");
                    PreparedStatement stm = c.prepareStatement(sql);
            ) {
                c.setAutoCommit(false);

                stm.setString(1, u.getUserName());
                stm.setString(2, u.getPassword());
                stm.setBytes(3, u.getSalt());

                stm.executeUpdate();

                c.commit();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User getUserById(int targetId) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:user.db");
                Statement stm = c.createStatement()
            ) {
            c.setAutoCommit(false);
            String sql = SQLStatement.selectStatement()
                    .select("*")
                    .from("USERLIST")
                    .where("ID = " + targetId)
                    .toString();

            try (ResultSet rs = stm.executeQuery(sql)) {
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

        try (
            Connection c = DriverManager.getConnection("jdbc:sqlite:user.db");
            Statement stm = c.createStatement();
        ) {
            c.setAutoCommit(false);
            String sql = SQLStatement.selectStatement()
                    .select("*")
                    .from("USERLIST")
                    .where("USERNAME = \"" + username + "\"")
                    .toString();

            try (ResultSet resultSet = stm.executeQuery(sql)) {
                int id = resultSet.getInt("ID");
                String userName = resultSet.getString("USERNAME");
                String password = resultSet.getString("PASSWORD");
                byte[] salt = resultSet.getBytes("SALT");

                return new User(id, userName, password, salt);
            }
        }
    }

    public boolean authenticate(User user, String password) {
        return user.getPassword().equals(password);
    }

    public static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public static String hashPassword(String password, byte[] salt) {
        System.out.println("PARAMETERS: " + password + " : " + Arrays.toString(salt));

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);

            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hashedPassword = new StringBuilder();

            for (byte b : bytes) {
                hashedPassword.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }

            System.out.println("HASHED PASS: " + hashedPassword);

            return hashedPassword.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
