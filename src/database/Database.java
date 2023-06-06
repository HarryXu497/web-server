package database;

import database.dao.ProblemDatabase;
import database.dao.UserDatabase;
import database.dao.SolvedProblemsDatabase;

public class Database {
    private final ProblemDatabase problemDatabase;
    private final UserDatabase userDatabase;
    private final SolvedProblemsDatabase solvedProblemsDatabase;

    public Database() {
        this.problemDatabase = new ProblemDatabase();
        this.userDatabase = new UserDatabase();
        this.solvedProblemsDatabase = new SolvedProblemsDatabase();
    }

    public ProblemDatabase problems() {
        return this.problemDatabase;
    }

    public UserDatabase users() {
        return this.userDatabase;
    }

    public SolvedProblemsDatabase solvedProblems() {
        return this.solvedProblemsDatabase;
    }
}
