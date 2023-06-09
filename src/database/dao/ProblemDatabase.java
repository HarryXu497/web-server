package database.dao;

import database.model.Problem;
import database.statement.SQLStatement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ProblemDatabase {
    public ProblemDatabase() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (
            Connection c = DriverManager.getConnection("jdbc:sqlite:problem.db");
            Statement stm = c.createStatement()
        ) {
            String sql = "CREATE TABLE IF NOT EXISTS PROBLEMLIST " +
                    "(ID INTEGER PRIMARY KEY NOT NULL, " +
                    "TITLE TEXT UNIQUE, " +
                    "CONTENT TEXT, " +
                    "DIFFICULTY INT     NOT NULL, " +
                    "TYPE TEXT          NOT NULL, " +
                    "USER_ID     NOT NULL," +
                    "FOREIGN KEY (USER_ID)" +
                    "   REFERENCES USERLIST (USER_ID));";

            stm.executeUpdate(sql);
        }
    }

    public void addProblem(Problem p) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        String sql = SQLStatement.insertStatement()
                .insertInto("PROBLEMLIST")
                .columns("TITLE", "CONTENT", "DIFFICULTY", "TYPE", "USER_ID")
                .values("?", "?", "?", "?", "?")
                .toString();

        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:problem.db");
                PreparedStatement stm = c.prepareStatement(sql)
        ) {
            stm.setString(1, p.getTitle());
            stm.setString(2, p.getContent());
            stm.setInt(3, p.getDifficulty());
            stm.setString(4, p.getType());
            stm.setInt(5, p.getAuthorID());

            stm.executeUpdate();
        }
    }

    public Problem getProblemById(int targetId) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        String sql = SQLStatement.selectStatement()
                .select("*")
                .from("PROBLEMLIST")
                .where("ID = ?")
                .toString();

        try (
            Connection c = DriverManager.getConnection("jdbc:sqlite:problem.db");
            PreparedStatement stm = c.prepareStatement(sql)
        ) {

            stm.setInt(1, targetId);

            try (ResultSet rs = stm.executeQuery()) {
                int id = rs.getInt("ID");
                String title = rs.getString("TITLE");
                String content = rs.getString("CONTENT");
                int difficulty = rs.getInt("DIFFICULTY");
                String type = rs.getString("TYPE");
                int authorId = rs.getInt("USER_ID");

                return new Problem(id, difficulty, title, content, type, authorId);
            }
        }
    }

    public Problem getProblemByTitle(String targetTitle) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        String sql = SQLStatement.selectStatement()
                .select("*")
                .from("PROBLEMLIST")
                .where("TITLE = ?")
                .toString();

        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:problem.db");
                PreparedStatement stm = c.prepareStatement(sql)
            ) {

            stm.setString(1, targetTitle);

            try (ResultSet rs = stm.executeQuery()) {
                int id = rs.getInt("ID");
                String title = rs.getString("TITLE");
                String content = rs.getString("CONTENT");
                int difficulty = rs.getInt("DIFFICULTY");
                String type = rs.getString("TYPE");
                int authorId = rs.getInt("USER_ID");

                return new Problem(id, difficulty, title, content, type, authorId);
            }
        }
    }
    public ArrayList<Problem> getProblemByType(String targetType) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        String sql = SQLStatement.selectStatement()
                .select("*")
                .from("PROBLEMLIST")
                .where("TYPE = ?")
                .toString();

        ArrayList<Problem> problems = new ArrayList<>();

        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:problem.db");
                PreparedStatement statement = c.prepareStatement(sql)
            ) {

            statement.setString(1, targetType);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String title = rs.getString("TITLE");
                    String content = rs.getString("CONTENT");
                    int difficulty = rs.getInt("DIFFICULTY");
                    String type = rs.getString("TYPE");
                    int authorId = rs.getInt("USER_ID");

                    problems.add(new Problem(id, difficulty, title, content, type, authorId));
                }
            }
        }

        return problems;
    }

    public ArrayList<Problem> getAllProblems() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        ArrayList<Problem> problems = new ArrayList<>();

        try (
                Connection c = DriverManager.getConnection("jdbc:sqlite:problem.db");
                Statement stm = c.createStatement()
            ) {


            String sql = SQLStatement.selectStatement()
                    .select("*")
                    .from("PROBLEMLIST")
                    .toString();

            try (ResultSet rs = stm.executeQuery(sql)) {
                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String title = rs.getString("TITLE");
                    String content = rs.getString("CONTENT");
                    int difficulty = rs.getInt("DIFFICULTY");
                    String type = rs.getString("TYPE");
                    int authorId = rs.getInt("USER_ID");

                    problems.add(new Problem(id, difficulty, title, content, type, authorId));
                }
            }
        }

        return problems;
    }

    public void populateFromDirectory(String baseDirectory) throws IOException, SQLException {
        File dir = new File(baseDirectory);

        File[] problemFolders = dir.listFiles();

        String sql = SQLStatement.insertStatement()
                .insertInto("PROBLEMLIST")
                .orReplace()
                .columns("ID", "TITLE", "CONTENT", "DIFFICULTY", "TYPE", "USER_ID")
                .values("?", "?", "?", "?", "?", "?")
                .toString();

        if (problemFolders != null) {
            for (File problemDirectory : problemFolders) {
                // "1", "2", ...
                String problemId = problemDirectory.getPath().split("\\\\")[1];

                // Read file containing metadata
                File metadataFile = new File(problemDirectory, "problem.txt");

                // Read file information
                String[] problemMetadata = new String[4];
                StringBuilder problemContent = new StringBuilder();

                try (BufferedReader file = new BufferedReader(new FileReader(metadataFile))) {
                    // Read problem metadata
                    for (int i = 0; i < problemMetadata.length; i++) {
                        problemMetadata[i] = file.readLine();
                    }

                    // Read problem content
                    int currentChar;

                    while ((currentChar = file.read()) != -1) {
                        problemContent.append((char) currentChar);
                    }
                }

                // Load JDBC driver
                try {
                    Class.forName("org.sqlite.JDBC");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                try (
                        Connection conn = DriverManager.getConnection("jdbc:sqlite:problem.db");
                        PreparedStatement statement = conn.prepareStatement(sql)
                ) {
                    // Set parameters
                    statement.setInt(1, Integer.parseInt(problemId));
                    statement.setString(2, problemMetadata[0]);
                    statement.setString(3, problemContent.toString());
                    statement.setInt(4, Integer.parseInt(problemMetadata[2]));
                    statement.setString(5, problemMetadata[1]);
                    statement.setInt(6, Integer.parseInt(problemMetadata[3]));

                    // Execute update
                    statement.executeUpdate();
                }
            }
        }
    }
}
