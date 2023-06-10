package coderunner;

/**
 * represents the result of a {@link Task},
 * which wraps a {@link TaskCode} and some optional data around the result.
 * @author Harry Xu
 * @version 1.0 - June 4th 2023
 */
public class TaskResult {
    /** the test code of the result */
    private final TaskCode taskCode;

    /** the data of the result */
    private final String data;

    /**
     * Constructs a TaskResult with a Task code and a message
     * @param taskCode the task code
     * @param data the task data
     * */
    public TaskResult(TaskCode taskCode, String data) {
        this.taskCode = taskCode;
        this.data = data;
    }

    /**
     * getTaskCode
     * gets the {@link TaskCode} of this task result
     * @return the {@link TaskCode}
     */
    public TaskCode getTaskCode() {
        return this.taskCode;
    }

    /**
     * getData
     * gets the data for task result
     * @return the data as a string
     */
    public String getData() {
        return this.data;
    }
}
