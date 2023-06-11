package database.model;

/**
 * Represents the roles that a user could have,
 * which would determine their privileges in the application
 * @author Tommy Shan
 * @version 1.0 - June 1st 2023
 * */
public enum Role {
    /** Can delete comments and problems */
    ADMIN,
    /** Can delete comments */
    MODERATOR,
    /** No special privileges */
    USER,
}
