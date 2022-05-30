package net.patterns.saga.orchestratorservice.service.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.patterns.saga.common.model.payment.PaymentRequestDTO;
import net.patterns.saga.common.model.payment.PaymentResponseDTO;
import net.patterns.saga.common.model.payment.PaymentStatus;
import net.patterns.saga.orchestratorservice.service.workflow.WorkflowStep;
import net.patterns.saga.orchestratorservice.service.workflow.WorkflowStepStatus;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Slf4j
public class PaymentStep implements WorkflowStep {

    private final String paymentServiceUrl;
    private final PaymentRequestDTO requestDTO;

    private RestTemplate restTemplate = new RestTemplate();
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;


    @Override
    public WorkflowStepStatus getStatus() {
        return this.stepStatus;
    }

    @Override
    public boolean process() {
        log.info("Processing payment order {} ", requestDTO);
        PaymentResponseDTO result = restTemplate.postForObject(paymentServiceUrl + "/debit", requestDTO, PaymentResponseDTO.class);
        log.debug("Result of payment is: {}",result);
        stepStatus = result.getStatus().equals(PaymentStatus.APPROVED) ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED;
        return result.getStatus().equals(PaymentStatus.APPROVED);
    }

    @Override
    public boolean revert() {
        log.info("Reverting payment order {} ", requestDTO);
        restTemplate.postForObject(paymentServiceUrl + "/credit", requestDTO, Object.class);
        return true;
    }

}
