package database.model;

public class UserProblem {
    private final int userID;
    private final int problemID;

    public UserProblem(int userID, int problemID) {
        this.userID = userID;
        this.problemID = problemID;
    }

    public int getProblemID() {
        return this.problemID;
    }

    public int getUserID() {
        return this.userID;
    }
}
