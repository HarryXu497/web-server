package coderunner.test;

public class TestResult {
    private TestCode statusCode;
    private String message;

    public TestResult(TestCode statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public TestCode getStatusCode() {
        return this.statusCode;
    }

    public String getMessage() {
        return this.message;
    }
}
