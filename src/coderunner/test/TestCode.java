package coderunner.test;

public enum TestCode {
    ACCEPTED("AC"),
    WRONG_ANSWER("WA"),
    INVALID_RETURN("IR"),
    RUNTIME_ERROR("RTE"),
    OUTPUT_LIMIT_EXCEEDED("OLE"),
    MEMORY_LIMIT_EXCEEDED("MLE"),
    TIME_LIMIT_EXCEEDED("TLE"),
    INTERNAL_ERROR("IE");

    private String code;

    TestCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
