package coderunner;

/**
 * Represents the possible results of a task compilation
 * @author Harry Xu
 * @version 1.0 - June 4th 2023
 */
public enum TaskCode {
    /** The code was compiled successfully */
    SUCCESSFUL,
    /** There was an error compiling the source code */
    COMPILE_ERROR,
}
