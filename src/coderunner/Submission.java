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

    /** The id of the problem being submitted to */
    private final int problemId;

    /**
     * constructs a submission with a task and submission id
     * @param task the submission task
     * @param submissionId the unique id identifier
     */
    public Submission(Task task, String submissionId, int problemId) {
        this.task = task;
        this.submissionId = submissionId;
        this.problemId = problemId;
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

    /**
     * getProblemId
     * gets the submission's problem id
     * @return the submission problem id
     */
    public int getProblemId() {
        return this.problemId;
    }
}
