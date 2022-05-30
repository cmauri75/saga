package net.patterns.saga.orchestratorservice.service.workflow;

import java.util.List;

public interface Workflow {
    List<WorkflowStep> steps();
}
