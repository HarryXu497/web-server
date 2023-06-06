package database;

import database.dao.ProblemDatabase;
import database.dao.UserDatabase;

public class Database {
    private final ProblemDatabase problemDatabase;
    private final UserDatabase userDatabase;

    public Database() {
        this.problemDatabase = new ProblemDatabase();
        this.userDatabase = new UserDatabase();
    }

    public ProblemDatabase problems() {
        return this.problemDatabase;
    }

    public UserDatabase users() {
        return this.userDatabase;
    }
}
