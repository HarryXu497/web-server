package coderunner;

import coderunner.test.Test;
import coderunner.test.TestCode;
import coderunner.test.TestResult;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Represents a series of tests for a problem submission
 * contains the source code file and a list of {@link Test Tests}
 * @author Harry XU
 * @version 1.0 - May 28th 2023
 */
public class Task {
    /** the source code sent from the client */
    private final String sourceCode;

    /** The path to the source file */
    private final String sourceFile;

    /** The list of tests to run on the source code */
    private final List<Test> tests;

    /** The results of the tests*/
    private final TestResult[] testResults;

    /** Compilation Result */
    private TaskResult compilationResult;

    /** The current test being run*/
    private int testIndex;

    /**
     * Constructs a Task
     * @param sourceCode the source code to be tested by this Task
     * @param sourceFile the path to the file to write the source code to for compilation
     * @param inputFiles a list of file paths to the input files for the tests
     * @param answerFiles a list of file paths to the output files for the tests
     * @throws IOException if an IO error occurs while reading
     * */
    public Task(String sourceCode, String sourceFile, List<String> inputFiles, List<String> outputFiles, List<String> answerFiles) throws IOException {
        if (inputFiles.size() != answerFiles.size()) {
            throw new IllegalArgumentException("The two input lists should be the same length");
        }

        this.sourceCode = sourceCode;
        this.sourceFile = sourceFile;
        this.tests = new ArrayList<>();

        // Derive path for class file
        String compiledFilePath = this.sourceFile.substring(this.sourceFile.lastIndexOf("/") + 1, this.sourceFile.lastIndexOf("."));

        // Add tests
        for (int i = 0; i < inputFiles.size(); i++) {
            this.tests.add(new Test(
                    compiledFilePath,
                    inputFiles.get(i),
                    outputFiles.get(i),
                    answerFiles.get(i)
            ));
        }

        this.testIndex = 0;
        this.testResults = new TestResult[this.tests.size()];
    }

    private void filter() {

    }

    /**
     * write
     * opens and writes the source code to the specified source code file
     * @throws IOException if an IO error occurs while writing to the file
     */
    public void write() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.sourceFile))) {
            bw.write(this.sourceCode);
        }
    }

    /**
     * compile
     * reads and compiles the source code to a class file at the root of the project directory
     * @throws IOException if an IO error occurs while reading to the file
     */
    public TaskResult compile() throws IOException, InterruptedException {
        // Get working directory
        String workingDirectory = System.getProperty("user.dir");

        Process compilation = Runtime.getRuntime().exec("javac " + (workingDirectory + "\\") + this.sourceFile + " -d " + (workingDirectory + "\\"));

        // wait until compilation done or timeout
        compilation.waitFor(5000, TimeUnit.MILLISECONDS);

        // Store success output
        StringBuilder successfulOutput = new StringBuilder();

        try (
            BufferedReader compilationIn = new BufferedReader(new InputStreamReader(compilation.getInputStream()));
            BufferedReader compilationErr = new BufferedReader(new InputStreamReader(compilation.getErrorStream()))
        ) {
            StringBuilder fullErrorText = new StringBuilder();

            // Error output -> compilation error
            // Read error stream
            int errorChar;

            while ((errorChar = compilationErr.read()) != -1) {
                fullErrorText.append((char) errorChar);
            }

            String errorText = fullErrorText.toString();

            // Error occurred
            if (errorText.length() != 0) {
                this.compilationResult = new TaskResult(TaskCode.COMPILE_ERROR, errorText);
                return this.compilationResult;
            }

            // Standard out -> successful compile
            // read output stream
            int successChar;

            while ((successChar = compilationIn.read()) != -1) {
                successfulOutput.append((char) successChar);
            }
        }

        this.compilationResult = new TaskResult(TaskCode.SUCCESSFUL, successfulOutput.toString());
        return this.compilationResult;
    }

    /**
     * nextTest
     * runs the next set of test files on the compiled code
     * @return the result of the test
     */
    public TestResult nextTest() {
        // Gets the current test
        Test currentTest = this.tests.get(this.testIndex);

        TestResult res;

        // Executes and runs the test
        try {
            currentTest.execute();
            res = currentTest.test();
        } catch (IOException | InterruptedException e) {
            res = new TestResult(TestCode.INTERNAL_ERROR, "Error running test file " + this.testIndex);
        }

        // Caches the test result
        this.testResults[this.testIndex] = res;

        this.testIndex++;

        return res;
    }

    /**
     * hasNextTest
     * checks if there is another test available to execute
     * @return if there is another test to run
     */
    public boolean hasNextTest() {
        return this.testIndex < this.tests.size();
    }

    /**
     * getTestResults
     * gets the cached test results
     * @return the test results as an array
     */
    public TestResult[] getTestResults() {
        return this.testResults;
    }

    /**
     * getCompilationResult
     * gets the cached compilation result
     * @return the compilation result
     */
    public TaskResult getCompilationResult() {
        return this.compilationResult;
    }
}
