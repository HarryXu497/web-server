package template;

/**
 * Represents the type of directive
 * @author Harry XU
 * @version 1.0 - May 20th 2023
 */
public enum DirectiveType {
    /** A foreach directive for iteration in the template */
    FOREACH,
    /** An if directive for conditional rendering in the template*/
    IF,
    /** An include directive to include partials in the template */
    INCLUDE,
}
