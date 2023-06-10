package database.dao;

import database.model.Problem;
import database.model.User;
import database.statement.SQLStatement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Wraps raw SQL connections to the problem_user table in a more accessible API.
 * This table acts as a many-to-many join table between the problem and user table.
 * It is used to mark problems as solved by users.
 * @author Tommy Shan
 * @version - June 7th 2023
 */
public class SolvedProblemsDatabase {
    /** JDBC URL to connect to the database */
    private static final String JDBC_URL = "jdbc:sqlite:problem_user.db";

    /**
     * Constructs the object and initializes its data.
     * Creates a problems table if it does not exist.
     * @throws SQLException if an error occurs while using SQL
     */
    public SolvedProblemsDatabase() throws SQLException {
        // Load JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Create raw SQL statement
        String sql = "CREATE TABLE IF NOT EXISTS PROBLEM_USER (" +
                "PROBLEM_ID INTEGER NOT NULL," +
                "USER_ID INTEGER NOT NULL," +
                "PRIMARY KEY (PROBLEM_ID, USER_ID));";

        // Create and execute statement
        try (
                Connection conn = DriverManager.getConnection(JDBC_URL);
                Statement statement = conn.createStatement()
        ) {
            statement.executeUpdate(sql);
        }
    }

    /**
     * markAsSolved
     * Marks a problem with a specified id as solved by a user with a specified id.
     * @param userId the id of the user who solved the problem
     * @param problemId the id of the solved problem
     * @throws SQLException if an error occurs while using SQL
     */
    public void markAsSolved(int userId, int problemId) throws SQLException {
        // Load JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Create SQL statement
        String sql = SQLStatement.insertStatement()
                .insertInto("PROBLEM_USER")
                .orReplace()
                .columns("PROBLEM_ID", "USER_ID")
                .values("?", "?")
                .toString();

        // Create and execute statement
        try (
                Connection conn = DriverManager.getConnection(JDBC_URL);
                PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            // Set parameters
            statement.setInt(1, problemId);
            statement.setInt(2, userId);

            statement.executeUpdate();
        }
    }

    /**
     * getAllSolvedProblems
     * Attempts to get all the ids of the problems solved by a user.
     * @param userId the id of the user
     * @return a list of problems ids that the user has solved
     * @throws SQLException if an error occurs while using SQL
     */
    public List<Integer> getAllSolvedProblems(int userId) throws SQLException {
        // Load JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Construct SQL statement
        String sql = SQLStatement.selectStatement()
                .select("*")
                .from("PROBLEM_USER")
                .where("USER_ID = " + userId)
                .toString();

        List<Integer> problemIDs = new ArrayList<>();

        // Create and execute statement
        try (
                Connection conn = DriverManager.getConnection(JDBC_URL);
                Statement statement = conn.createStatement()
        ) {
            // Gets problem ids and adds to list
            try (ResultSet rs = statement.executeQuery(sql)) {
                while (rs.next()) {
                    int problemID = rs.getInt("PROBLEM_ID");

                    problemIDs.add(problemID);
                }
            }

            return problemIDs;
        }
    }

    /**
     * initializeSolvedProblems
     * Helper method to mark all problems in a list as completed by their author.
     * @param problems the problems to mark
     * @throws SQLException if an error occurs while using SQL
     */
    public void initializeSolvedProblems(List<Problem> problems) throws SQLException {
        for (Problem problem : problems) {
            this.markAsSolved(problem.getAuthorID(), problem.getProblemID());
        }
    }
}
