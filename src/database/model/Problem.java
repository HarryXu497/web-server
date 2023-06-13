package database.model;

/**
 * Represents a problem entity in the application
 * @author Tommy Shan
 * @version 1.0 - June 1st 2023
 */
public class Problem {
    /** The unique id of the problem */
    private final int problemID;

    /** The difficulty of the problem as an integer from 1 to 100inclusive */
    private final int difficulty;

    /** The title of the problem */
    private final String title;

    /** The content of the problem */
    private final String content;

    /** The type of the problem (e.g. Dynamic Programming, Graph Theory)*/
    private final String type;

    /** The unique id of the {@link User} who authored this problem */
    private final int authorID;

    /**
     * Constructs a {@link Problem} dataclass
     * @param problemID the id of the problem
     * @param difficulty the difficulty of the problem
     * @param title the title of the problem
     * @param content the content of the problem
     * @param type the type of the problem
     * @param authorID the user who authored the problem
     */
    public Problem(int problemID, int difficulty, String title, String content, String type, int authorID) {
        this.problemID = problemID;
        this.difficulty = difficulty;
        this.title = title;
        this.content = content;
        this.type = type;
        this.authorID = authorID;
    }

    /**
     * getTitle
     * Gets the title of the problem
     * @return the problem title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * getProblemID
     * Gets the id of the problem
     * @return the problem id
     */
    public int getProblemID() {
        return this.problemID;
    }

    /**
     * getContent
     * Gets the content of the problem
     * @return the problem content
     */
    public String getContent() {
        return this.content;
    }

    /**
     * getAuthorID
     * Gets the id of author of the problem
     * @return the problem's author's id
     */
    public int getAuthorID() {
        return this.authorID;
    }

    /**
     * getType
     * Gets the type of the problem
     * @return the problem type
     */
    public String getType() {
        return this.type;
    }

    /**
     * getDifficulty
     * Gets the difficulty of the problem
     * @return the problem difficulty as an integer from 1 to 100inclusive
     */
    public int getDifficulty() {
        return this.difficulty;
    }
}
