package net.patterns.saga.common.model.orchestrator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrchestratorRequestDTO {

    private Integer userId;
    private Integer productId;
    private UUID orderId;
    private Double amount;


}
