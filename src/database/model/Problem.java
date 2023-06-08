package database.model;

public class Problem {
    private int problemID;
    private int difficulty;
    private String title;
    private String content;
    private String type;
    private int authorID;

    /**
     * Construct for class database.model.Problem
     * @param problemID the id of the problem
     * @param difficulty the difficulty of the problem
     * @param title the title of the problem
     * @param content the content of the problem
     * @param type the type of the problem
     * @param authorID the user who authored the problem
     */
    public Problem(int problemID, int difficulty, String title, String content, String type, int authorID) {
        setProblemID(problemID);
        setDifficulty(difficulty);
        setTitle(title);
        setContent(content);
        setType(type);
        setAuthorID(authorID);
    }

    /**
     * getTitle
     * Getter method for variable title
     * @return variable title
     */
    public String getTitle() {
        return title;
    }

    /**
     * setTitle
     * Setter method for variable title
     * @param title variable title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * getProblemID
     * Getter method for variable problemID
     * @return variable problemID
     */
    public int getProblemID() {
        return problemID;
    }

    /**
     * setProblemID
     * Setter method for variable problemID
     * @param problemID variable problemID
     */
    public void setProblemID(int problemID) {
        this.problemID = problemID;
    }

    /**
     * getContent
     * Getter method for variable content
     * @return variable content
     */
    public String getContent() {
        return content;
    }

    /**
     * setContent
     * Setter method for variable content
     * @param content variable content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * getAuthorID
     * Getter method for variable author
     * @return variable author
     */
    public int getAuthorID() {
        return authorID;
    }

    /**
     * setAuthorID
     * Setter method for variable author
     * @param authorID variable author
     */
    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }

    /**
     * getType
     * Getter method for variable type
     * @return variable type
     */
    public String getType() {
        return type;
    }

    /**
     * setType
     * Setter method for variable type
     * @param type variable type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * getDifficulty
     * Getter method for variable difficulty
     * @return variable difficulty
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * setDifficulty
     * Setter method for variable difficulty
     * @param difficulty variable difficulty
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
