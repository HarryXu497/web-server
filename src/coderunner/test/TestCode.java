package coderunner.test;

/**
 * Represents the possible test codes for an online judge
 * @author Harry Xu
 * @version 1.0 - June 4th 2023
 */
public enum TestCode {
    /** Program passed testing */
    ACCEPTED("AC"),
    /** Program ran successfully but did not output the correct answer */
    WRONG_ANSWER("WA"),
    /** Program returned with a nonzero exit code (i.e. it crashed) */
    INVALID_RETURN("IR"),
    /** Program stopped execution with a runtime error */
    RUNTIME_ERROR("RTE"),
    /** Program output too much data */
    OUTPUT_LIMIT_EXCEEDED("OLE"),
    /** Program ran out of memory */
    MEMORY_LIMIT_EXCEEDED("MLE"),
    /** Program took too long to execute */
    TIME_LIMIT_EXCEEDED("TLE"),
    /** Internal error in the judge */
    INTERNAL_ERROR("IE");

    /** The letter code associated with each response */
    private final String code;

    /**
     * Constructs an enum entry with a test code
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
