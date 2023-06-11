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
import java.util.List;

/**
 * Wraps raw SQL connections to the problem table in a more accessible API
 * @author Tommy Shan
 * @version - June 1st 2023
 */
public class ProblemDatabase {
    /** JDBC URL to connect to the database */
    private static final String JDBC_URL = "jdbc:sqlite:problem.db";

    /** Class name of the JDBC driver */
    private static final String JDBC_CLASS_NAME = "org.sqlite.JDBC";

    /**
     * Constructs the object and initializes its data.
     * Creates a problems table if it does not exist.
     * @throws SQLException if an error occurs while using SQL
     */
    public ProblemDatabase() throws SQLException {
        // Load JDBC driver
        try {
            Class.forName(JDBC_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Create and execute statement
        try (
            Connection conn = DriverManager.getConnection(JDBC_URL);
            Statement statement = conn.createStatement()
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

            statement.executeUpdate(sql);
        }
    }

    /**
     * addProblem
     * Attempts to add a problem to the problems table.
     * @param problem the problem to be inserted
     * @throws SQLException if an error occurs while using SQL
     */
    public void addProblem(Problem problem) throws SQLException {
        // Load JDBC driver
        try {
            Class.forName(JDBC_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Create SQL statement
        String sql = SQLStatement.insertStatement()
                .insertInto("PROBLEMLIST")
                .columns("TITLE", "CONTENT", "DIFFICULTY", "TYPE", "USER_ID")
                .values("?", "?", "?", "?", "?")
                .toString();

        // Prepare and execute statement
        try (
                Connection conn = DriverManager.getConnection(JDBC_URL);
                PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            // Set parameters
            statement.setString(1, problem.getTitle());
            statement.setString(2, problem.getContent());
            statement.setInt(3, problem.getDifficulty());
            statement.setString(4, problem.getType());
            statement.setInt(5, problem.getAuthorID());

            statement.executeUpdate();
        }
    }

    /**
     * getProblemById
     * Attempts to retrieve a problem with its id.
     * @param targetId the id of the problem
     * @return the requested problem or null if not found
     * @throws SQLException if an error occurs while using SQL
     */
    public Problem getProblemById(int targetId) throws SQLException {
        // Load JDBC driver
        try {
            Class.forName(JDBC_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Create SQL statement
        String sql = SQLStatement.selectStatement()
                .select("*")
                .from("PROBLEMLIST")
                .where("ID = ?")
                .toString();

        // Prepare and execute statement
        try (
            Connection conn = DriverManager.getConnection(JDBC_URL);
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            // Set parameters
            statement.setInt(1, targetId);

            // Get result
            try (ResultSet resultSet = statement.executeQuery()) {
                // Return null if no problem found
                if (!resultSet.isBeforeFirst() ) {
                    return null;
                }

                int id = resultSet.getInt("ID");
                String title = resultSet.getString("TITLE");
                String content = resultSet.getString("CONTENT");
                int difficulty = resultSet.getInt("DIFFICULTY");
                String type = resultSet.getString("TYPE");
                int authorId = resultSet.getInt("USER_ID");

                return new Problem(id, difficulty, title, content, type, authorId);
            }
        }
    }

    /**
     * getProblemByTitle
     * Attempts to retrieve a problem with its title
     * @param targetTitle the title of the problem
     * @return the requested problem or null if not found
     * @throws SQLException if an error occurs while using SQL
     */
    public Problem getProblemByTitle(String targetTitle) throws SQLException {
        // Load JDBC driver
        try {
            Class.forName(JDBC_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Create SQL statement
        String sql = SQLStatement.selectStatement()
                .select("*")
                .from("PROBLEMLIST")
                .where("TITLE = ?")
                .toString();

        // Prepare and execute statement
        try (
                Connection conn = DriverManager.getConnection(JDBC_URL);
                PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            // Set parameters
            statement.setString(1, targetTitle);

            // Get result
            try (ResultSet resultSet = statement.executeQuery()) {
                // Return null if no problem found
                if (!resultSet.isBeforeFirst() ) {
                    return null;
                }

                // Get fields
                int id = resultSet.getInt("ID");
                String title = resultSet.getString("TITLE");
                String content = resultSet.getString("CONTENT");
                int difficulty = resultSet.getInt("DIFFICULTY");
                String type = resultSet.getString("TYPE");
                int authorId = resultSet.getInt("USER_ID");

                return new Problem(id, difficulty, title, content, type, authorId);
            }
        }
    }

    /**
     * getProblemByTitle
     * Attempts to retrieve a problem with its type
     * @param targetType the type/category of the problem
     * @return a list of all problems with the specified type
     * @throws SQLException if an error occurs while using SQL
     */
    public List<Problem> getProblemByType(String targetType) throws SQLException {
        // Load JDBC driver
        try {
            Class.forName(JDBC_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Create SQL statement
        String sql = SQLStatement.selectStatement()
                .select("*")
                .from("PROBLEMLIST")
                .where("TYPE = ?")
                .toString();

        // Get problems
        ArrayList<Problem> problems = new ArrayList<>();

        // Prepare and execute statement
        try (
                Connection conn = DriverManager.getConnection(JDBC_URL);
                PreparedStatement statement = conn.prepareStatement(sql)
            ) {

            // Set parameters
            statement.setString(1, targetType);

            // Get results and add to list
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Get fields
                    int id = resultSet.getInt("ID");
                    String title = resultSet.getString("TITLE");
                    String content = resultSet.getString("CONTENT");
                    int difficulty = resultSet.getInt("DIFFICULTY");
                    String type = resultSet.getString("TYPE");
                    int authorId = resultSet.getInt("USER_ID");

                    problems.add(new Problem(id, difficulty, title, content, type, authorId));
                }
            }
        }

        return problems;
    }

    /**
     * getAllProblems
     * Attempts to retrieve a problem with its type
     * @return a list of all problems
     * @throws SQLException if an error occurs while using SQL
     */
    public List<Problem> getAllProblems() throws SQLException {
        // Load JDBC driver
        try {
            Class.forName(JDBC_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Create SQL statement
        String sql = SQLStatement.selectStatement()
                .select("*")
                .from("PROBLEMLIST")
                .toString();

        // Get all problems
        ArrayList<Problem> problems = new ArrayList<>();

        // Create and execute statement
        try (
                Connection conn = DriverManager.getConnection(JDBC_URL);
                Statement statement = conn.createStatement()
        ) {
            // Get results
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    // Get fields
                    int id = resultSet.getInt("ID");
                    String title = resultSet.getString("TITLE");
                    String content = resultSet.getString("CONTENT");
                    int difficulty = resultSet.getInt("DIFFICULTY");
                    String type = resultSet.getString("TYPE");
                    int authorId = resultSet.getInt("USER_ID");

                    problems.add(new Problem(id, difficulty, title, content, type, authorId));
                }
            }
        }

        return problems;
    }

    /**
     * populateFromDirectory
     * Loads problems from a base directory.
     * Searches for a `problem.txt` file in each problem folder and parses it for metadata.
     * @param baseDirectory the directory containing all the problems
     * @throws IOException if an exception occurs while opening or reading files
     * @throws SQLException if an error occurs while using SQL
     */
    public void populateFromDirectory(String baseDirectory) throws IOException, SQLException {
        // Create SQL statement to insert with a custom id
        String sql = SQLStatement.insertStatement()
                .insertInto("PROBLEMLIST")
                .orReplace()
                .columns("ID", "TITLE", "CONTENT", "DIFFICULTY", "TYPE", "USER_ID")
                .values("?", "?", "?", "?", "?", "?")
                .toString();

        // List all files in the base directory
        File dir = new File(baseDirectory);

        File[] problemFolders = dir.listFiles();

        if (problemFolders != null) {

            // Load JDBC driver
            try {
                Class.forName(JDBC_CLASS_NAME);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            for (File problemDirectory : problemFolders) {
                // "1", "2", ...
                String problemId = problemDirectory.getPath().split("\\\\")[1];

                // Read file containing metadata
                File metadataFile = new File(problemDirectory, "problem.txt");

                // Read file information
                String[] problemMetadata = new String[4];
                StringBuilder problemContent = new StringBuilder();

                // Read file content
                try (BufferedReader file = new BufferedReader(new FileReader(metadataFile))) {
                    // Read problem metadata
                    for (int i = 0; i < problemMetadata.length; i++) {
                        problemMetadata[i] = file.readLine();
                    }

                    // Read problem content
                    int currentChar = file.read();

                    while (currentChar != -1) {
                        problemContent.append((char) currentChar);
                        currentChar = file.read();
                    }
                }

                // Prepare and execute insert statement
                try (
                        Connection conn = DriverManager.getConnection(JDBC_URL);
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
