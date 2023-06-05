package coderunner.test;

/**
 * represents the possible test codes for an online judge
 * @author Harry Xu
 * @version 1.0 - June 4th 2023
 */
public enum TestCode {
    ACCEPTED("AC"),
    WRONG_ANSWER("WA"),
    INVALID_RETURN("IR"),
    RUNTIME_ERROR("RTE"),
    OUTPUT_LIMIT_EXCEEDED("OLE"),
    MEMORY_LIMIT_EXCEEDED("MLE"),
    TIME_LIMIT_EXCEEDED("TLE"),
    INTERNAL_ERROR("IE");

    /** The letter code associated with each response */
    private final String code;

    /**
     * constructs an enum entry with a test code
     * @param code the letter code
     */
    TestCode(String code) {
        this.code = code;
    }

    /**
     * getCode
     * gets the letter code associated with each enum value
     * @return the code as a string
     */
    public String getCode() {
        return this.code;
    }
}
