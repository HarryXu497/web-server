package server.handler.routes;

import coderunner.CodeRunner;
import coderunner.Submission;
import coderunner.TaskCode;
import coderunner.TaskResult;
import coderunner.test.TestCode;
import coderunner.test.TestResult;
import server.handler.Handler;
import server.handler.methods.Get;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;

import java.util.HashMap;
import java.util.Map;

/**
 * A handler for the /problems/:problemId/submissions url, where the /problems/:problemId/tests page
 * polls for data about the problem submissions to update the webpage programmatically
 * @author Harry Xu
 * @version 1.0 - June 4th 2023
 */
public class SubmissionPollRoute extends Handler implements Get {

    /** The code runner instance running the submitted code */
    private final CodeRunner codeRunner;

    /**
     * constructs a SubmissionPollRoute with its dependencies
     */
    public SubmissionPollRoute(CodeRunner codeRunner) {
        this.codeRunner = codeRunner;
    }

    /**
     * handles the get request to this route
     * @param req the HTTP request to handle
     * @return the HTTP response to the request
     */
    @Override
    public Response get(Request req) {
        // Current processing submission
        Submission submission = this.codeRunner.getCurrentSubmission();

        // Stored submissions
        Map<String, Submission> submissions = this.codeRunner.getSubmissionHistories();

        // Default response body
        // the `completed` flag tells the client to stop polling
        String body = "{ \"completed\": true }";

        // Get submission id from the client request
        String submissionId = req.getCookies().get("submissionId");


        // Currently processing a submission
        if (submission != null) {
            // Get compilation result of current
            TaskResult compilationResult = submission.getTask().getCompilationResult();

            int queuedPosition = this.codeRunner.indexInQueue(submissionId);

            if ((compilationResult != null) && (compilationResult.getTaskCode() == TaskCode.COMPILE_ERROR)) {

                String escapedData = compilationResult.getData()
                        .replace("\\", "\\\\")
                        .replace("\t", "\\t")
                        .replace("\b", "\\b")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
                        .replace("\f", "\\f")
                        .replace("\"", "\\\"");

                escapedData = escapedData.substring(escapedData.lastIndexOf("\\\\") + 2);

                // Compilation error
                body = "{\"error\": \"" + escapedData + "\", \"completed\": true }";
            } else if (submission.getSubmissionId().equals(submissionId)) {
                TestResult[] testResults = submission.getTask().getTestResults();

                // Get current submission
                body = testsToJSON(testResults);

                if (areTestsCompleted(testResults)) {
                    System.out.println("Complete 1" +
                            "");
                    submissions.remove(submissionId);
                }

            } else if (queuedPosition != -1) {
                // Your submission is queued
                body = "{ \"queued\": true, \"position\": " + queuedPosition + " }";
            } else {
                // Current submission is not yours
                body = "{}";
            }
        }

        if ((submission == null) || (!submission.getSubmissionId().equals(submissionId))) {
            if (submissions.containsKey(submissionId)){
                // Checks cache for previous submissions and removes it requests
                // guarantees that all submissions are completed
                System.out.println("Complete 2");
                // Submission done testing - get finished data and remove from history
                body = testsToJSON(submissions.get(submissionId).getTask().getTestResults());

                // Remove submission (i.e. end polling)
                submissions.remove(submissionId);
            }
        }

        System.out.println(body);

        // Headers
        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Type", "text/json");
        headers.put("Vary", "Accept-Encoding");
        headers.put("Accept-Ranges", "none");

        return new Response(
                new Response.StatusLine(ResponseCode.OK),
                headers,
                body
        );
    }

    private static String testsToJSON(TestResult[] testResults) {
        StringBuilder body = new StringBuilder("{ ");

        body.append("\"tests\": [");

        for (int i = 0; i < testResults.length - 1; i++) {
            TestResult currentResult = testResults[i];
            String resCode = "\"Pending\"";

            if (currentResult != null) {
                TestCode code = currentResult.getStatusCode();
                resCode = "\"" + code.getCode() + "\"";
            }

            body.append(resCode).append(", ");
        }

        if (testResults[testResults.length - 1] == null) {
            body.append("\"Pending\"");
        } else {
            TestCode code = testResults[testResults.length - 1].getStatusCode();

            body.append("\"").append(code.getCode()).append("\"");
        }

        // Include completed flag to inform client to stop polling
        boolean completed = areTestsCompleted(testResults);

        body.append("], ");
        body.append("\"completed\": ");
        body.append(completed);
        body.append("}");

        return body.toString();
    }

    private static boolean areTestsCompleted(TestResult[] testResults) {
        boolean completed = false;

        for (int i = 0; i < testResults.length; i++) {
            TestResult testResult = testResults[i];

            if ((testResult != null) && (testResult.getStatusCode() != TestCode.ACCEPTED)) {
                completed = true;
            }

            if ((i == testResults.length - 1) && (testResults[i] != null)) {
                completed = true;
            }
        }

        return completed;
    }
}
