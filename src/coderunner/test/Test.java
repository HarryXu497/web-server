package coderunner.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Test {
    private final String compiledFilePath;
    private final String inputFilePath;
    private final String outputFilePath;
    private final String answerFilePath;
    private TestResult result;

    public Test(String compiledFilePath, String inputFilePath, String outputFilePath, String answerFilePath) {
        this.compiledFilePath = compiledFilePath;
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
        this.answerFilePath = answerFilePath;
        this.result = null;
    }

    public void execute() throws IOException, InterruptedException {

        String[] commands = { "java ", this.compiledFilePath };

        String dir = System.getProperty("user.dir") + "\\";


        ProcessBuilder process = new ProcessBuilder(commands);

//        process.directory(new File(dir));
        process.redirectInput(new File(dir + this.inputFilePath));
        process.redirectOutput(new File(dir + this.outputFilePath));





        Process executionProcess = process.start();

        if (!executionProcess.waitFor(5000, TimeUnit.MILLISECONDS)) {
            executionProcess.destroy();
            System.out.println("Timeout");
            this.result = new TestResult(TestCode.TIME_LIMIT_EXCEEDED, null);
        }
    }

    public TestResult test() throws IOException {
        if (this.result != null) {
            return this.result;
        }

        StringBuilder answerFileContents = new StringBuilder();

        String workingDirectory = System.getProperty("user.dir") + "\\";

        try (BufferedReader br = new BufferedReader(new FileReader(workingDirectory + this.answerFilePath))) {
            String s;

            while ((s = br.readLine()) != null) {
                answerFileContents.append(s);
            }
        }

        StringBuilder outputFileContents = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(workingDirectory + this.outputFilePath))) {
            String s;

            while ((s = br.readLine()) != null) {
                outputFileContents.append(s);
            }
        }

        if (outputFileContents.toString().equals(answerFileContents.toString())) {
            return new TestResult(TestCode.ACCEPTED, null);
        } else {
            return new TestResult(TestCode.WRONG_ANSWER, null);
        }
    }
}
