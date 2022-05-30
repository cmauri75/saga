package net.patterns.saga.orchestratorservice.service.workflow;

import reactor.core.publisher.Mono;

public interface WorkflowStep {
    WorkflowStepStatus getStatus();
    boolean process();
    boolean revert();
}
