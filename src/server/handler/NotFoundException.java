package server.handler;

/**
 * Thrown to manually indicates to the server to show a 404 page.
 * This can be used to indicate that a specific route with dynamic segment cannot locate the resource
 * i.e. problems/:problemId -> problems/5 -> No problem with id 5
 * @author Harry Xu
 * @version 1.0 - June 6th 2023
 */
public class NotFoundException extends RuntimeException {
    /** Constructs a NotFoundException with no message */
    public NotFoundException() {
        super();
    }

    /** Constructs a NotFoundException with an error message */
    public NotFoundException(String message) {
        super(message);
    }
}
