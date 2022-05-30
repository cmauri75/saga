package net.patterns.saga.orchestratorservice.service.workflow;

import java.util.List;

public record OrderWorkflow(List<WorkflowStep> steps) implements Workflow {}
