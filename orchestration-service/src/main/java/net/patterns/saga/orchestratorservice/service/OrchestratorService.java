package net.patterns.saga.orchestratorservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.patterns.saga.common.model.orchestrator.OrchestratorRequestDTO;
import net.patterns.saga.common.model.orchestrator.OrchestratorResponseDTO;
import net.patterns.saga.common.model.order.OrderStatus;
import net.patterns.saga.orchestratorservice.service.external.InventoryStep;
import net.patterns.saga.orchestratorservice.service.external.PaymentStep;
import net.patterns.saga.orchestratorservice.service.workflow.OrderWorkflow;
import net.patterns.saga.orchestratorservice.service.workflow.Workflow;
import net.patterns.saga.orchestratorservice.service.workflow.WorkflowStep;
import net.patterns.saga.orchestratorservice.service.workflow.WorkflowStepStatus;
import net.patterns.saga.orchestratorservice.support.DtoConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrchestratorService {

    @Value("${service.endpoints.inventory}")
    private String inventoryServiceUrl;

    @Value("${service.endpoints.payment}")
    private String paymentServiceUrl;

    public OrchestratorResponseDTO orderProduct(final OrchestratorRequestDTO requestDTO) {
        try {
            Workflow orderWorkflow = this.getOrderWorkflow(requestDTO);
            log.info("------------------------ Starting order wf: {}", orderWorkflow.steps());

            List<Boolean> results = orderWorkflow.steps().stream()
                    .map(workflowStep -> workflowStep.process()).toList();
            log.info("Steps finished with: {}", results);

            if (results.stream().filter(res -> !res.booleanValue()).count() > 0) {
                log.debug("At least one step failed: reverting");
                this.revertOrder(orderWorkflow, requestDTO);
                return DtoConverter.getResponseDTO(requestDTO, OrderStatus.CANCELLED);
            }
            return DtoConverter.getResponseDTO(requestDTO, OrderStatus.COMPLETED);
        } finally {
            log.info("---------------------- Process complete");
        }
    }

    private OrchestratorResponseDTO revertOrder(final Workflow workflow, final OrchestratorRequestDTO requestDTO) {
        log.info("Reverting order wf: {}", workflow.steps());
        for (WorkflowStep orderStep : workflow.steps()) {
            if (orderStep.getStatus().equals(WorkflowStepStatus.COMPLETE)) {
                log.debug("Reverting:{} ", orderStep);
                orderStep.revert();
                //here a retry should take place
            }
        }
        return DtoConverter.getResponseDTO(requestDTO, OrderStatus.CANCELLED);
    }

    private Workflow getOrderWorkflow(OrchestratorRequestDTO requestDTO) {
        WorkflowStep paymentStep = new PaymentStep(paymentServiceUrl, DtoConverter.getPaymentRequestDTO(requestDTO));
        WorkflowStep inventoryStep = new InventoryStep(inventoryServiceUrl, DtoConverter.getInventoryRequestDTO(requestDTO));
        return new OrderWorkflow(List.of(paymentStep, inventoryStep));
    }


}
