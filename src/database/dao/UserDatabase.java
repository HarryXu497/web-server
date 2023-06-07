package database.dao;

import database.model.Role;
import database.model.User;
import database.statement.SQLStatement;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;

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
                        "NUMOFPOINT INT     NOT NULL, " +
                        "NUMOFSOLVE INT     NOT NULL, " +
                        "NUMOFSUBMIT INT    NOT NULL, " +
                        "SALT TEXT NOT NULL," +
                        "ROLE TEXT, " +
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
                    .columns("USERNAME", "NUMOFPOINT", "NUMOFSOLVE", "NUMOFSUBMIT", "ROLE", "PASSWORD", "SALT")
                    .values("?", "?", "?", "?", "?", "?", "?")
                    .toString();

            try (
                    Connection c = DriverManager.getConnection("jdbc:sqlite:user.db");
                    PreparedStatement stm = c.prepareStatement(sql);
            ) {
                c.setAutoCommit(false);

                stm.setString(1, u.getUserName());
                stm.setInt(2, u.getNumOfPoint());
                stm.setInt(3, u.getNumOfSolve());
                stm.setInt(4, u.getNumOfSubmit());
                stm.setString(5, u.getRole());
                stm.setString(6, u.getPassword());
                stm.setString(7, u.getSalt());

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
                Statement stm = c.createStatement();
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
                String salt = rs.getString("SALT");

                int numOfPoint = rs.getInt("NUMOFPOINT");
                int numOfSolve = rs.getInt("NUMOFSOLVE");
                int numOfSubmit = rs.getInt("NUMOFSUBMIT");

                String roleString = rs.getString("ROLE");
                Role role = Role.valueOf(roleString);

                return new User(userName, id, numOfPoint, numOfSolve, numOfSubmit, role, password, salt);
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
                    .where("USERNAME = " + username)
                    .toString();

            try (ResultSet resultSet = stm.executeQuery(sql)) {
                int id = resultSet.getInt("ID");
                String userName = resultSet.getString("USERNAME");
                String password = resultSet.getString("PASSWORD");
                String salt = resultSet.getString("SALT");

                int numOfPoint = resultSet.getInt("NUMOFPOINT");
                int numOfSolve = resultSet.getInt("NUMOFSOLVE");
                int numOfSubmit = resultSet.getInt("NUMOFSUBMIT");

                String roleString = resultSet.getString("ROLE");
                Role role = Role.valueOf(roleString);

                return new User(userName, id, numOfPoint, numOfSolve, numOfSubmit, role, password, salt);
            }
        }
    }

    public boolean logInMatched(User user, String password) {
        return user.getPassword().equals(password);
    }

    public static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public static String hashPassword(String password, byte[] salt) {
        String hashedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hashedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return hashedPassword;
    }
}
