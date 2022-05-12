package net.patterns.saga.common.model.order;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OrderRequestDTO {
    private UUID orderId;
    private Integer userId;
    private Integer productId;
}
