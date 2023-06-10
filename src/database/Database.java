package database;

import database.dao.ProblemDatabase;
import database.dao.UserDatabase;
import database.dao.SolvedProblemsDatabase;

import java.sql.SQLException;

/**
 * Wraps a singleton instance of each of {@link ProblemDatabase}, {@link UserDatabase}, and {@link SolvedProblemsDatabase}
 * in a single class to be shared throughout the entire application.
 * @author Harry Xu
 * @version 1.0 - June 2nd 2023
 */
public class Database {
    /** Problem database instance */
    private final ProblemDatabase problemDatabase;
    /** User database instance */
    private final UserDatabase userDatabase;
    /** Solved problem database instance */
    private final SolvedProblemsDatabase solvedProblemsDatabase;

    /**
     * Constructs this class and initializes its database fields to a singleton instance
     * @throws SQLException if an exception occurs with SQL while instantiating the databases
     */
    public Database() throws SQLException {
        this.problemDatabase = new ProblemDatabase();
        this.userDatabase = new UserDatabase();
        this.solvedProblemsDatabase = new SolvedProblemsDatabase();
    }

    /**
     * problems
     * Gets the {@link ProblemDatabase} of this wrapper class
     * @return the {@link ProblemDatabase} instance
     */
    public ProblemDatabase problems() {
        return this.problemDatabase;
    }

    /**
     * users
     * Gets the {@link UserDatabase} of this wrapper class
     * @return the {@link UserDatabase} instance
     */
    public UserDatabase users() {
        return this.userDatabase;
    }

    /**
     * solvedProblems
     * Gets the {@link SolvedProblemsDatabase} of this wrapper class
     * @return the {@link SolvedProblemsDatabase} instance
     */
    public SolvedProblemsDatabase solvedProblems() {
        return this.solvedProblemsDatabase;
    }
}
