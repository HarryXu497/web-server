package coderunner;

public class Submission {
    private final Task task;
    private final String requestId;
    private Status status;

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

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        QUEUED,
        PENDING,
        COMPLETED,
    }
}
