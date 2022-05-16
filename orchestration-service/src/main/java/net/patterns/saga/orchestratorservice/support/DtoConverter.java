package net.patterns.saga.orchestratorservice.support;


import net.patterns.saga.common.model.inventory.InventoryRequestDTO;
import net.patterns.saga.common.model.inventory.InventoryResponseDTO;

public class DtoConverter {

    private DtoConverter() {
    }
    public static InventoryResponseDTO requestToResponse(InventoryRequestDTO requestDTO) {
        return InventoryResponseDTO.builder()
                .orderId(requestDTO.getOrderId())
                .userId(requestDTO.getUserId())
                .productId(requestDTO.getProductId())
                .build();
    }
}
