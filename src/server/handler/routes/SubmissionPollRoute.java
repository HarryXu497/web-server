package server.handler.routes;

import coderunner.CodeRunner;
import coderunner.Submission;
import coderunner.TaskCode;
import coderunner.TaskResult;
import coderunner.test.TestCode;
import coderunner.test.TestResult;
import database.Database;
import database.model.Problem;
import database.model.User;
import server.handler.Handler;
import server.handler.methods.Get;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;

import java.sql.SQLException;
import java.util.List;
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

    /** The database used to authenticate and manage users */
    private final Database database;

    /**
     * constructs a SubmissionPollRoute with its dependencies
     */
    public SubmissionPollRoute(CodeRunner codeRunner, Database database) {
        this.codeRunner = codeRunner;
        this.database = database;
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
        String submissionId = req.getCookies().get("password");

        // Username
        String username = req.getCookies().get("username");


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
                    try {
                        // Get user
                        User currentUser = this.database.users().getByUsername(username);

                        List<Integer> solvedProblems = this.database.solvedProblems().getAllSolvedProblems(currentUser.getUserID());

                        boolean alreadySolved = solvedProblems.contains(submission.getProblemId());

                        if (!alreadySolved) {

                            // Get user id
                            int userId = currentUser.getUserID();

                            // Get user information
                            int oldPoints = currentUser.getPoints();
                            int problemDifficulty = this.database.problems()
                                    .getProblemById(submission.getProblemId())
                                    .getDifficulty();
                            int problemsSolved = this.database.solvedProblems().getAllSolvedProblems(userId).size();

                            // Increment if problem solved is one to avoid denominator of 0
                            if (problemsSolved <= 2) {
                                problemsSolved = 2;
                            }

                            // Calculate new points
                            double denominator = Math.log(problemsSolved) / Math.log(2);
                            int newPoints = oldPoints + (int) Math.floor((problemDifficulty * 100) / (denominator));

                            // Add points
                            this.database.users().updatePoints(userId, newPoints);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    try {
                        // Mark this problem as solved by the user
                        this.addUserTransaction(username);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }


                    // Remove submission
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
                // Submission done testing - get finished data and remove from history
                Submission requestedSubmission = submissions.get(submissionId);

                body = testsToJSON(requestedSubmission.getTask().getTestResults());

                try {
                    // Get user
                    User currentUser = this.database.users().getByUsername(username);

                    List<Integer> solvedProblems = this.database.solvedProblems().getAllSolvedProblems(currentUser.getUserID());

                    boolean alreadySolved = solvedProblems.contains(requestedSubmission.getProblemId());

                    if (!alreadySolved) {

                        // Get user id
                        int userId = currentUser.getUserID();

                        // Get user information
                        int oldPoints = currentUser.getPoints();
                        int problemDifficulty = this.database.problems()
                                .getProblemById(requestedSubmission.getProblemId())
                                .getDifficulty();
                        int problemsSolved = this.database.solvedProblems().getAllSolvedProblems(userId).size();

                        // Increment if problem solved is one to avoid denominator of 0
                        if (problemsSolved <= 2) {
                            problemsSolved = 2;
                        }

                        // Calculate new points
                        double denominator = Math.log(problemsSolved) / Math.log(2);
                        int newPoints = oldPoints + (int) Math.floor((problemDifficulty * 100) / (denominator));

                        // Add points
                        this.database.users().updatePoints(userId, newPoints);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    // Mark this problem as solved by the user
                    this.addUserTransaction(username);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                // Remove submission (i.e. end polling)
                submissions.remove(submissionId);
            }
        }

        // Headers
        Map<String, String> headers = Handler.htmlHeaders();

        return new Response(
                new Response.StatusLine(ResponseCode.OK),
                headers,
                body
        );
    }

    private void addUserTransaction(String username) throws SQLException {
        User currentUser;

        try {
            currentUser = this.database.users().getByUsername(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        this.database.solvedProblems().addTransaction(currentUser.getUserID(), this.codeRunner.getCurrentSubmission().getProblemId());
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
