package database.dao;

import database.model.Role;
import database.model.User;
import database.statement.SQLStatement;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class UserDatabase {
    public UserDatabase() {
        Connection c = null;
        Statement stm = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:user.db");
            stm = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS USERLIST " +
                    "(ID INT PRIMARY KEY NOT NULL, " +
                    "USERNAME TEXT, " +
                    "NUMOFPOINT INT     NOT NULL, " +
                    "NUMOFSOLVE INT     NOT NULL, " +
                    "NUMOFSUBMIT INT    NOT NULL, " +
                    "ROLE TEXT, " +
                    "PASSWORD TEXT);";
            stm.executeUpdate(sql);
            stm.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public void addUser(User u) {
        Connection c = null;
        Statement stm = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:user.db");
            c.setAutoCommit(false);
            stm = c.createStatement();
            String sql = "INSERT OR REPLACE INTO USERLIST (ID,USERNAME,NUMOFPOINT,NUMOFSOLVE,NUMOFSUBMIT,ROLE,PASSWORD) " +
                    "VALUES (" + u.getUserID() + ", '" + u.getUserName() + "', " + u.getNumOfPoint() + ", " + u.getNumOfSolve() + ", " + u.getNumOfSubmit() + ", '" + u.getRole() + "', '" + u.getPassword() + "');";
            stm.executeUpdate(sql);
            stm.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public User getUserById(int targetId) {
        Connection c = null;
        Statement stm = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:user.db");
            c.setAutoCommit(false);
            stm = c.createStatement();
            String sql = SQLStatement.selectStatement().select("*").from("USERLIST").where("ID = " + targetId).toString();
            ResultSet rs = stm.executeQuery( sql);
            int id = rs.getInt("ID");
            String userName = rs.getString("USERNAME");
            int numOfPoint = rs.getInt("NUMOFPOINT");
            int numOfSolve = rs.getInt("NUMOFSOLVE");
            int numOfSubmit = rs.getInt("NUMOFSUBMIT");
            String roleString = rs.getString("ROLE");
            Role role = null;
            if(roleString.equals("ADMIN")) {
                role = Role.ADMIN;
            } else if(roleString.equals("MODERATOR")) {
                role = Role.MODERATOR;
            } else if(roleString.equals("USER")) {
                role = Role.USER;
            }
            User u = new User(userName, id, numOfPoint, numOfSolve, numOfSubmit, role, "pw");
            if(targetId == u.getUserID()) {
                return u;
            }
            rs.close();
            stm.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return null;
    }

    public ArrayList<User> getAllUser() {
        ArrayList<User> ret = new ArrayList<>();
        Connection c = null;
        Statement stm = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:problem.db");
            c.setAutoCommit(false);
            stm = c.createStatement();
            String sql = SQLStatement.selectStatement().select("*").from("USERLIST").toString();
            ResultSet rs = stm.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("ID");
                String userName = rs.getString("USERNAME");
                int numOfPoint = rs.getInt("NUMOFPOINT");
                int numOfSolve = rs.getInt("NUMOFSOLVE");
                int numOfSubmit = rs.getInt("NUMOFSUBMIT");
                String roleString = rs.getString("ROLE");
                Role role = null;
                if(roleString.equals("ADMIN")) {
                    role = Role.ADMIN;
                } else if(roleString.equals("MODERATOR")) {
                    role = Role.MODERATOR;
                } else if(roleString.equals("USER")) {
                    role = Role.USER;
                }
                User u = new User(userName, id, numOfPoint, numOfSolve, numOfSubmit, role, "pw");
                ret.add(u);
            }
            rs.close();
            stm.close();
            c.close();
            return ret;
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return null;
    }

    public boolean logInMatched(User user, String password) {
        if(user.getPassword().equals(password)) {
            return true;
        }
        return false;
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
