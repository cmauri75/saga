package net.patterns.saga.common.model.exception;

public class WorkflowException extends RuntimeException {

    public WorkflowException(String message) {
        super(message);
    }

}
