package server.handler.routes;

import coderunner.CodeRunner;
import coderunner.Submission;
import server.handler.Handler;
import server.handler.methods.Get;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;

import java.util.HashMap;
import java.util.Map;

public class SubmissionPollRoute extends Handler implements Get {

    private final CodeRunner codeRunner;

    public SubmissionPollRoute(CodeRunner codeRunner) {
        this.codeRunner = codeRunner;
    }

    @Override
    public Response get(Request req) {
        // Compile template with data
        Submission submission = this.codeRunner.getCurrentSubmission();
        String body;

        if (submission == null) {
            body = "{}";
        } else if (submission.getStatus() == Submission.Status.PENDING) {
            body = "{ \"totalTests\": " + submission.getTask().getTestCount()  + ", \"currentTest\": " + submission.getTask().getTestIndex() + "}";
        } else {
            throw new RuntimeException("This should never happen");
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
}
