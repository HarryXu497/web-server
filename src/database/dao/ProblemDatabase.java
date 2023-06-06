package database.dao;

import database.model.Problem;
import database.statement.SQLStatement;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class ProblemDatabase {
    public ProblemDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:problem.db");
                Statement stm = c.createStatement()
            ) {
                String sql = "CREATE TABLE IF NOT EXISTS PROBLEMLIST " +
                        "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "TITLE TEXT, " +
                        "CONTENT TEXT, " +
                        "DIFFICULTY INT     NOT NULL, " +
                        "TYPE TEXT          NOT NULL, " +
                        "USER_ID     NOT NULL," +
                        "FOREIGN KEY (USER_ID)" +
                        "   REFERENCES USERLIST (USER_ID));";

                stm.executeUpdate(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void addProblem(Problem p) {
        try {
            Class.forName("org.sqlite.JDBC");

            String sql = SQLStatement.insertStatement()
                    .insertInto("PROBLEMLIST")
                    .columns("TITLE", "CONTENT", "DIFFICULTY", "TYPE", "USER_ID")
                    .values("?", "?", "?", "?", "?")
                    .toString();

            try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:problem.db");
                PreparedStatement stm = c.prepareStatement(sql)
            ) {
                c.setAutoCommit(false);

                stm.setString(1, p.getTitle());
                stm.setString(2, p.getContent());
                stm.setInt(3, p.getDifficulty());
                stm.setString(4, p.getType());
                stm.setInt(5, p.getAuthorID());
                stm.executeUpdate();
                c.commit();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Problem getProblemById(int targetId) {
        try {
            Class.forName("org.sqlite.JDBC");
            try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:problem.db");
                Statement stm = c.createStatement()
            ) {
                c.setAutoCommit(false);

                String sql = SQLStatement.selectStatement()
                        .select("*")
                        .from("PROBLEMLIST")
                        .where("ID = " + targetId)
                        .toString();

                try (ResultSet rs = stm.executeQuery(sql)) {
                    int id = rs.getInt("ID");
                    String title = rs.getString("TITLE");
                    String content = rs.getString("CONTENT");
                    int difficulty = rs.getInt("DIFFICULTY");
                    String type = rs.getString("TYPE");
                    int authorId = rs.getInt("USER_ID");

                    return new Problem(id, difficulty, title, content, type, LocalDateTime.now(), null, authorId);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Problem getProblemByTitle(String targetTitle) {
        try {
            Class.forName("org.sqlite.JDBC");
            try (
                    Connection c = DriverManager.getConnection("jdbc:sqlite:problem.db");
                    Statement stm = c.createStatement();
                ) {

                c.setAutoCommit(false);

                String sql = SQLStatement.selectStatement()
                        .select("*")
                        .from("PROBLEMLIST")
                        .where("TITLE = " + targetTitle)
                        .toString();
                try (ResultSet rs = stm.executeQuery(sql)) {
                    int id = rs.getInt("ID");
                    String title = rs.getString("TITLE");
                    String content = rs.getString("CONTENT");
                    int difficulty = rs.getInt("DIFFICULTY");
                    String type = rs.getString("TYPE");
                    int authorId = rs.getInt("USER_ID");

                    return new Problem(id, difficulty, title, content, type, null, null, authorId);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<Problem> getProblemByType(String targetType) {
        ArrayList<Problem> ret = new ArrayList<>();
        Connection c = null;
        Statement stm = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:problem.db");
            c.setAutoCommit(false);
            stm = c.createStatement();
            String sql = SQLStatement.selectStatement().select("*").from("PROBLEMLIST").where("TYPE = '" + targetType + "'").toString();
            ResultSet rs = stm.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("ID");
                String title = rs.getString("TITLE");
                String content = rs.getString("CONTENT");
                int difficulty = rs.getInt("DIFFICULTY");
                String type = rs.getString("TYPE");
                int authorId = rs.getInt("USER_ID");

                Problem p = new Problem(id, difficulty, title, content, type, null, null, authorId);
                ret.add(p);
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

    public ArrayList<Problem> getAllProblems() {
        ArrayList<Problem> ret = new ArrayList<>();
        Connection c = null;
        Statement stm = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:problem.db");
            c.setAutoCommit(false);
            stm = c.createStatement();
            String sql = SQLStatement.selectStatement()
                    .select("*")
                    .from("PROBLEMLIST")
                    .toString();
            System.out.println(sql);
            ResultSet rs = stm.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("ID");
                String title = rs.getString("TITLE");
                String content = rs.getString("CONTENT");
                int difficulty = rs.getInt("DIFFICULTY");
                String type = rs.getString("TYPE");
                int authorId = rs.getInt("USER_ID");

                Problem p = new Problem(id, difficulty, title, content, type, null, null, authorId);

                System.out.println(p);

                ret.add(p);
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
}
