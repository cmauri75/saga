package net.patterns.saga.orchestratorservice.service.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.patterns.saga.common.model.inventory.InventoryRequestDTO;
import net.patterns.saga.common.model.inventory.InventoryResponseDTO;
import net.patterns.saga.common.model.inventory.InventoryStatus;
import net.patterns.saga.orchestratorservice.service.workflow.WorkflowStep;
import net.patterns.saga.orchestratorservice.service.workflow.WorkflowStepStatus;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Slf4j
public class InventoryStep implements WorkflowStep {

    private final String inventoryServiceUrl;

    private final InventoryRequestDTO requestDTO;

    private RestTemplate restTemplate = new RestTemplate();

    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;

    @Override
    public WorkflowStepStatus getStatus() {
        return this.stepStatus;
    }

    /**
     * Calls inventory item take, if success returns true and updates internal state with COMPLETE, else FAILS
     *
     * @return true if everything is ok, false otherwise
     */
    @Override
    public boolean process() {
        log.info("Processing inventory order {} ", requestDTO);
        InventoryResponseDTO result = restTemplate.postForObject(inventoryServiceUrl + "/take", requestDTO, InventoryResponseDTO.class);
        stepStatus = result.getStatus().equals(InventoryStatus.AVAILABLE) ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED;
        return result.getStatus().equals(InventoryStatus.AVAILABLE);
    }

    /**
     * Opposite of previous, item is backed to inventory.
     *
     * @return true if ok, false if fails
     */
    @Override
    public boolean revert() {
        log.info("Reverting inventory order {} ", requestDTO);
        restTemplate.postForObject(inventoryServiceUrl + "/put", requestDTO, Object.class);
        return true;
    }
}
