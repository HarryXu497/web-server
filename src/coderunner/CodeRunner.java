package coderunner;

import coderunner.test.TestCode;
import coderunner.test.TestResult;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class CodeRunner {

    private final Deque<Submission> submissionQueue;
    private final Object syncObject;

    public CodeRunner() {
        this.submissionQueue = new ArrayDeque<>();
        this.syncObject = new Object();
        Thread t = new Thread(new CodeTest());
        t.start();
    }

    public void enqueue(Submission submission) {
        this.submissionQueue.push(submission);

        synchronized (this.syncObject) {
            this.syncObject.notify();
        }

        System.out.println("Queued file " + submission.getTask());
    }

    public class CodeTest implements Runnable {

        @Override
        public void run() {
            while (true) {
                Submission currentSubmission;

                // Wait for queuing if queue is empty
                if (submissionQueue.size() == 0) {
                    synchronized (syncObject) {
                        try {
                            syncObject.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                currentSubmission = submissionQueue.pop();



                Task currentTask = currentSubmission.getTask();

                while (currentTask.hasNextTest()) {
                    TestResult res;

                    try {
                        res = currentTask.nextTest();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        res = new TestResult(TestCode.INTERNAL_ERROR, "Error testing file " + currentTask.getCurrentTest());
                    }

                    System.out.println("ID: " + currentSubmission.getRequestId());
                    System.out.println(res.getStatusCode());

                    if (res.getStatusCode() != TestCode.ACCEPTED) {
                        System.out.println("Not accepted");
                        break;
                    } else {
                        System.out.println("ACCEPTED");
                    }
                }
            }
        }
    }
}
