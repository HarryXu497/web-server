package coderunner;

public class TaskResult {
    private final TaskCode taskCode;
    private final String data;

    public TaskResult(TaskCode taskCode, String data) {
        this.taskCode = taskCode;
        this.data = data;
    }

    public TaskCode getTaskCode() {
        return this.taskCode;
    }

    public String getData() {
        return this.data;
    }
}
