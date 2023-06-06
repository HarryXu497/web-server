package database;

public class Submission {
    private int id;
    private int problemId;
    private int userId;

    /**
     * Submission
     * Constructor for Class Submission
     * @param id the submission id
     * @param problemId the id of the problem that the problem belong to
     * @param userId the id of the user that submitted the code
     */
    public Submission(int id, int problemId, int userId) {
        setId(id);
        setProblemId(problemId);
        setUserId(userId);
    }

    /**
     * getId
     * Getter method for variable id
     * @return variable id
     */
    public int getId() {
        return id;
    }

    /**
     * setId
     * Setter method for variable
     * @param id variable id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * getProblemId
     * Getter method for variable problemId
     * @return variable problemId
     */
    public int getProblemId() {
        return problemId;
    }

    /**
     * setProblemId
     * Setter method for variable problemId
     * @param problemId variable problemId
     */
    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    /**
     * getUserId
     * Getter method for variable userId
     * @return variable userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * setUserId
     * Setter method for variable userId
     * @param userId variable userId
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
