package database.model;

import java.util.Date;

public class Comment {
    private String content;
    private User author;
    private Date date;
    private int vote;
    private boolean isPublic;

    /**
     * Constructor for class database.model.Comment
     * @param content the content of the comment
     * @param author the user that authored the comment
     * @param date the date that the comment is posted
     * @param vote the number of vote of the comment
     * @param isPublic the publicity of the comment
     */
    public Comment(String content, User author, Date date, int vote, boolean isPublic) {
        setContent(content);
        setAuthor(author);
        setDate(date);
        setVote(vote);
        setPublic(isPublic);
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
     * isPublic
     * Getter method for variable isPublic
     * @return variable isPublic
     */
    public boolean isPublic() {
        return isPublic;
    }

    /**
     * setPublic
     * Setter method for variable isPublic
     * @param isPublic variable isPublic
     */
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    /**
     * getVote
     * Getter method for variable vote
     * @return variable vote
     */
    public int getVote() {
        return vote;
    }

    /**
     * setVote
     * Setter method for variable vote
     * @param vote variable vote
     */
    public void setVote(int vote) {
        this.vote = vote;
    }

    /**
     * getDate
     * Getter method for variable date
     * @return variable date
     */
    public Date getDate() {
        return date;
    }

    /**
     * setDate
     * Setter method for variable date
     * @param date variable date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * getAuthor
     * Getter method for variable author
     * @return variable author
     */
    public User getAuthor() {
        return author;
    }

    /**
     * setAuthor
     * Setter method for variable author
     * @param author variable author
     */
    public void setAuthor(User author) {
        this.author = author;
    }
}
