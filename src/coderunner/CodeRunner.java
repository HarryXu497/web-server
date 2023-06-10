package coderunner;

import coderunner.test.TestCode;
import coderunner.test.TestResult;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to run submitted java source code and evaluate the results.
 * @author Harry Xu
 * @version 1.0 - June 4th 2023
 */
public class CodeRunner {
    /** The queue of submissions to be processed */
    private final Deque<Submission> submissionQueue;

    /** A cache of previous submissions so each submission can be completed appropriately */
    private final Map<String, Submission> submissionHistories;

    /** The current submission being processed */
    private Submission currentSubmission;

    /** An object used to block the submission processing thread if there are no entries to process */
    private final Object syncObject;

    /**
     * Constructs a CodeRunner and starts the processing thread.
     */
    public CodeRunner() {
        this.submissionQueue = new ArrayDeque<>();
        this.submissionHistories = new HashMap<>();
        this.syncObject = new Object();

        // Start processing thread
        Thread t = new Thread(new CodeTest());
        t.start();
    }

    /**
     * enqueue
     * queues a submission to be processed.
     * @param submission the submission to be processed
     */
    public void enqueue(Submission submission) {
        // Push onto queue
        this.submissionQueue.addLast(submission);

        // Blocks until syncObject is available
        synchronized (this.syncObject) {
            this.syncObject.notify();
        }
    }

    /**
     * indexInQueue
     * finds the index of the submission with a certain id in the submission queue.
     * @param id the submission id
     * @return the index of the submission or -1 if not found.
     */
    public int indexInQueue(String id) {
        int i = 0;

        for (Submission submission : this.submissionQueue) {
            if (submission.getSubmissionId().equals(id)) {
                return i;
            }

            i++;
        }

        return -1;
    }

    /**
     * getCurrentSubmission
     * gets the current submission being processed.
     * @return the currently processing submission
     */
    public Submission getCurrentSubmission() {
        return this.currentSubmission;
    }

    /**
     * getSubmissionHistories
     * gets the history of submissions.
     * The history is maintained to ensure that all clients connections polling the submission route
     * are closed even if the submission is completed between polling intervals.
     * @return the cached submissions
     */
    public Map<String, Submission> getSubmissionHistories() {
        return this.submissionHistories;
    }

    /**
     * A runnable thread which handles the submission processing and testing.
     * @author Harry Xu
     * @version 1.0 - June 4th 2023
     */
    public class CodeTest implements Runnable {

        /**
         * run
         * processes all submissions submitted to the queue.
         */
        @Override
        public void run() {
            while (true) {
                // Block thread until queue is not empty
                if (submissionQueue.size() == 0) {
                    synchronized (syncObject) {
                        try {
                            syncObject.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            continue;
                        }
                    }
                }

                // Pop submission from queue
                currentSubmission = submissionQueue.removeFirst();

                // Cache submission with id
                submissionHistories.put(currentSubmission.getSubmissionId(), currentSubmission);

                // Get submission task
                Task currentTask = currentSubmission.getTask();

                // Write to source file and compile the source code
                try {
                    currentTask.write();
                    currentTask.compile();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }

                // Run all tests to completion
                while (currentTask.hasNextTest()) {
                    TestResult res = currentTask.nextTest();

                    if (res.getStatusCode() != TestCode.ACCEPTED) {
                        break;
                    }
                }
            }
        }
    }
}
