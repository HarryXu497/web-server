package database.dao;

import database.model.Problem;
import database.statement.SQLStatement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
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
                        "(ID INTEGER PRIMARY KEY, " +
                        "TITLE TEXT, " +
                        "CONTENT TEXT, " +
                        "DIFFICULTY INT     NOT NULL, " +
                        "TYPE TEXT          NOT NULL, " +
                        "USER_ID     NOT NULL," +
                        "SOLVES INT NOT NULL," +
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
                    .columns("ID", "TITLE", "CONTENT", "DIFFICULTY", "TYPE", "USER_ID", "SOLVES")
                    .values("?", "?", "?", "?", "?", "?", "?")
                    .toString();

            try (
                    Connection c = DriverManager.getConnection("jdbc:sqlite:problem.db");
                    PreparedStatement stm = c.prepareStatement(sql)
            ) {
                c.setAutoCommit(false);

                stm.setInt(1, p.getProblemID());
                stm.setString(2, p.getTitle());
                stm.setString(3, p.getContent());
                stm.setInt(4, p.getDifficulty());
                stm.setString(5, p.getType());
                stm.setInt(6, p.getAuthorID());
                stm.setInt(7, p.getSolves());
                stm.executeUpdate();
                c.commit();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Problem getProblemById(int targetId) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

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
                int solves = rs.getInt("SOLVES");

                return new Problem(id, difficulty, title, content, type, LocalDateTime.now(), null, solves, authorId);
            }
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
                    int solves = rs.getInt("SOLVES");

                    return new Problem(id, difficulty, title, content, type, null, null, solves, authorId);
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
                int solves = rs.getInt("SOLVES");

                Problem p = new Problem(id, difficulty, title, content, type, null, null, solves, authorId);
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
                int solves = rs.getInt("SOLVES");

                Problem p = new Problem(id, difficulty, title, content, type, null, null, solves, authorId);

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
