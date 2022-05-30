package net.patterns.saga.common.model.orchestrator;

import lombok.Data;
import net.patterns.saga.common.model.order.OrderStatus;

import java.util.UUID;

@Data
public class OrchestratorResponseDTO {

    private Integer userId;
    private Integer productId;
    private UUID orderId;
    private Double amount;
    private OrderStatus status;

}
