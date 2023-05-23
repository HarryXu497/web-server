package server.handler;

/**
 * This exception should be thrown if there is no handler that handle a request
 * @author Harry Xu
 * @version 1.0 - May 21st 2023
 * */
public class HandlerException extends Exception {
    /** Constructs a default handler exception */
    public HandlerException() {
        super();
    }

    /**
     * Constructs a handler exception with a message
     * @param message the exception message
     */
    public HandlerException(String message) {
        super(message);
    }
}
