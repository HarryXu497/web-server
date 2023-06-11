package database.model;

import java.time.LocalDateTime;

/**
 * Represents a comment entity made on a problem
 * @author Tommy Shan
 * @version 1.0 - June 1st 2023
 */
public class Comment {
    /** The content of the comment */
    private final String content;

    /** The id of the problem the comment is commenting on */
    private final int problemID;

    /** The id of the user that created this comment */
    private final int authorID;

    /** The date that the comment was posted */
    private final LocalDateTime date;

    /** The amount of up/down votes on this comment*/
    private final int vote;

    /**
     * Constructs this dataclass with its fields
     * @param content the content of the comment
     * @param problemID the id of the problem this comment responds to
     * @param authorID the id of user that authored the comment
     * @param date the date that the comment is posted
     * @param vote the number of vote of the comment
     */
    public Comment(String content, int problemID, int authorID, LocalDateTime date, int vote) {
        this.content = content;
        this.problemID = problemID;
        this.authorID = authorID;
        this.date = date;
        this.vote = vote;
    }

    /**
     * getContent
     * Gets the content of this comment
     * @return the comment content
     */
    public String getContent() {
        return this.content;
    }

    /**
     * getProblemID
     * Gets the id of the problem this comment was posted on
     * @return the comment problem id
     */
    public int getProblemID() {
        return this.problemID;
    }

    /**
     * getVote
     * Gets the votes on this comment
     * @return the comment problem id
     */
    public int getVote() {
        return this.vote;
    }

    /**
     * getDate
     * Gets the {@link LocalDateTime} that this comment was posted
     * @return the comment date
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * getAuthorID
     * Gets the id of the user that posted this comment
     * @return the comment author id
     */
    public int getAuthorID() {
        return authorID;
    }
}
