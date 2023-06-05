package coderunner;

/**
 * Wraps a {@link Task} with a unique submission id.
 * @author Harry Xu
 * @version 1.0 - June 4th 2023
 */
public class Submission {
    /** The task containing the tests for the problem */
    private final Task task;

    /** the submission id */
    private final String submissionId;

    /**
     * constructs a submission with a task and submission id
     * @param task the submission task
     * @param submissionId the unique id identifier
     */
    public Submission(Task task, String submissionId) {
        this.task = task;
        this.submissionId = submissionId;
    }

    /**
     * getTask
     * gets the task wrapped by this submission
     * @return the submission task
     */
    public Task getTask() {
        return this.task;
    }

    /**
     * getSubmissionId
     * gets the submission's id
     * @return the submission id
     */
    public String getSubmissionId() {
        return this.submissionId;
    }
}
