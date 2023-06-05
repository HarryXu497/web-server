package server.handler.routes;

import coderunner.CodeRunner;
import coderunner.Submission;
import server.handler.Handler;
import server.handler.methods.Get;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;
import template.TemplateEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TestsRoute extends Handler implements Get {

    /** The template engine which contains and compiles the templates */
    private final TemplateEngine templateEngine;

    /** The code runner which runs and judges the code */
    private final CodeRunner codeRunner;

    /**
     * constructs a TestsRoute handler
     * @param templateEngine the template engine which holds and compiles the templates
     */
    public TestsRoute(TemplateEngine templateEngine, CodeRunner codeRunner) {
        this.templateEngine = templateEngine;
        this.codeRunner = codeRunner;
    }

    @Override
    public Response get(Request req) {
        String problemId = req.getStatusLine().getRouteParams().get("problemId");
        String submissionId = req.getCookies().get("submissionId");

        Submission currentSubmission = this.codeRunner.getCurrentSubmission();

        System.out.println((currentSubmission != null) + " && " + ((currentSubmission != null) && (currentSubmission.getSubmissionId().equals(submissionId))));
        boolean isAuthorized = (currentSubmission != null) && (currentSubmission.getSubmissionId().equals(submissionId));

        int totalTests = 0;

        // If authorized, make a request to submissions api route to get current test
        if (isAuthorized) {
            try {
                URL url = new URL("http://localhost:5000/problems/" + problemId + "/submissions");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);

                int status = con.getResponseCode();

                if (status == 200) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                        String inputLine;
                        StringBuilder content = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            content.append(inputLine);
                        }

                        // Primitive JSON parser to get length - the tests will be hydrated by JS
                        String JSONString = content.toString();

                        if (JSONString.contains("\"tests\"")) {

                            String testsSubstring = JSONString.substring(JSONString.indexOf("[") + 1, JSONString.indexOf("]"));

                            for (char c : testsSubstring.toCharArray()) {
                                if (c == ',') {
                                    totalTests++;
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Compile template with data
        String body = this.templateEngine.compile("frontend/templates/tests.th", new Data(totalTests, isAuthorized));

        // Headers
        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Type", "text/html; charset=iso-8859-1");
        headers.put("Vary", "Accept-Encoding");
        headers.put("Accept-Ranges", "none");

        return new Response(
                new Response.StatusLine(ResponseCode.OK),
                headers,
                body
        );
    }

    // TODO document these 2 classes
    public static class Data {
        public IntWrapper[] tests;
        public boolean isAuthorized;

        public Data(int totalTests, boolean isAuthorized) {
            this.tests = new IntWrapper[totalTests];

            for (int i = 1; i <= this.tests.length; i++) {
                this.tests[i - 1] = new IntWrapper(i);
            }

            this.isAuthorized = isAuthorized;
        }
    }

    /** Needed for templating :(*/
    public static class IntWrapper {
        public int intValue;

        public IntWrapper(int i) {
            this.intValue = i;
        }
    }
}
