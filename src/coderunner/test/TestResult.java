package coderunner.test;

/**
 * represents the result of a test, consisting of a test code and optionally some data
 * @author Harry Xu
 * @version 1.0 - June 4th 2023
 */
public class TestResult {
    /** The status code of the test result */
    private final TestCode statusCode;

    /** The message associated with this test result*/
    private final String message;

    /**
     * Constructs a test result with a status code and message
     * @param statusCode the result status code
     * @param message the result message
     */
    public TestResult(TestCode statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    /**
     * getStatusCode
     * gets the status code associated with this TestResult
     * @return the status code
     */
    public TestCode getStatusCode() {
        return this.statusCode;
    }

    /**
     * getMessage
     * gets the message associated with this TestResult
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }
}
