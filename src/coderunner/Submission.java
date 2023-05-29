package coderunner;

public class Submission {
    private final Task task;
    private final String requestId;

    public Submission(Task task, String requestId) {
        this.task = task;
        this.requestId = requestId;
    }

    public Task getTask() {
        return this.task;
    }

    public String getRequestId() {
        return this.requestId;
    }
}
