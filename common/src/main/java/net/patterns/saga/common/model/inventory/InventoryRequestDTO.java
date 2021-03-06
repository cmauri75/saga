package net.patterns.saga.common.model.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequestDTO {
    private Integer userId;
    private Integer productId;
    private UUID orderId;
}
