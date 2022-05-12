package net.patterns.saga.common.model.inventory;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class InventoryRequestDTO {
    private Integer userId;
    private Integer productId;
    private UUID orderId;
}
