package server.handler.routes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: TEMPORARY
public class ProblemsDB {

    public static ProblemsDB db;

    static {
        db = new ProblemsDB(
            new ProblemsDB.Problem("CCC '23 S1 - Trianglane",
             "Tommy feels sleepy during geography class, so he decides to do his math homework. He is shocked when he opened his homework packaged that's assigned my Mr. Choi...\n" +
                    "\n" +
                    "It's too hard for him, a grade 9 student, to solve the problem. So he asked you to help him solve the problem for him.\n" +
                    "\n" +
                    "Given two numbers A and B, determine the value of A + B.", "Simple Math", 120, 37)
        );
    }

    private final Map<Integer, Problem> problems;

    public ProblemsDB(Problem... problems) {
        this.problems = new HashMap<>();

        for (Problem problem : problems) {
            this.problems.put(problem.id, problem);
        }
    }

    public Problem getById(int id) {
        return this.problems.get(id);
    }

    public List<Problem> getAll() {
        return new ArrayList<>(this.problems.values());
    }

    public static class Problem {
        private static int autoId = 0;

        public int id;
        public String name;
        public String content;
        public String type;
        public int submissionCount;
        public String successRateAsString;

        public Problem(String name, String content, String type, int submissionCount, int successfulSubmissionCount) {
            this.id = autoId;
            this.name = name;
            this.content = content;
            this.type = type;
            this.submissionCount = submissionCount;
            this.successRateAsString = Double.toString(Math.round((successfulSubmissionCount / (double) this.submissionCount) * 100.0) / 100.0);

            autoId++;
        }
    }
}
