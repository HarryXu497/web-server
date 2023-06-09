package database.dao;

import database.model.Problem;
import database.model.User;
import database.model.UserProblem;
import database.statement.SQLStatement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SolvedProblemsDatabase {
    public SolvedProblemsDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            try (
                    Connection c = DriverManager.getConnection("jdbc:sqlite:problem_user.db");
                    Statement stm = c.createStatement()
            ) {
                String sql = "CREATE TABLE IF NOT EXISTS PROBLEM_USER (" +
                        "PROBLEM_ID INTEGER NOT NULL," +
                        "USER_ID INTEGER NOT NULL," +
                        "PRIMARY KEY (PROBLEM_ID, USER_ID));";

                stm.executeUpdate(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void addTransaction(int userId, int problemId) {
        try {
            Class.forName("org.sqlite.JDBC");

            String sql = SQLStatement.insertStatement()
                    .insertInto("PROBLEM_USER")
                    .orReplace()
                    .columns("PROBLEM_ID", "USER_ID")
                    .values("?", "?")
                    .toString();

            try (
                    Connection c = DriverManager.getConnection("jdbc:sqlite:problem_user.db");
                    PreparedStatement stm = c.prepareStatement(sql)
            ) {
                c.setAutoCommit(false);

                stm.setInt(1, problemId);
                stm.setInt(2, userId);
                stm.executeUpdate();
                c.commit();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> getAllSolvedProblems(int userId) {
        try {
            Class.forName("org.sqlite.JDBC");
            try (
                    Connection c = DriverManager.getConnection("jdbc:sqlite:problem_user.db");
                    Statement stm = c.createStatement()
            ) {
                c.setAutoCommit(false);

                String sql = SQLStatement.selectStatement()
                        .select("*")
                        .from("PROBLEM_USER")
                        .where("USER_ID = " + userId)
                        .toString();

                List<Integer> problemIDs = new ArrayList<>();

                try (ResultSet rs = stm.executeQuery(sql)) {
                    while (rs.next()) {
                        int problemID = rs.getInt("PROBLEM_ID");

                        problemIDs.add(problemID);
                    }
                }

                return problemIDs;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initializeTransactions(User owner, List<Problem> problems) {
        for (Problem problem : problems) {
            this.addTransaction(owner.getUserID(), problem.getProblemID());
        }
    }
}
