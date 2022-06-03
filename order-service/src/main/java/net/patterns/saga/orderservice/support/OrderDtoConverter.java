package net.patterns.saga.orderservice.support;

import net.patterns.saga.common.model.orchestrator.OrchestratorRequestDTO;
import net.patterns.saga.common.model.order.OrderRequestDTO;
import net.patterns.saga.common.model.order.OrderResponseDTO;
import net.patterns.saga.common.model.order.OrderStatus;
import net.patterns.saga.orderservice.entity.PurchaseOrder;

public class OrderDtoConverter {

    private OrderDtoConverter() {
    }

    public static PurchaseOrder dtoToEntity(final OrderRequestDTO dto) {
        return PurchaseOrder.builder()
                .id(dto.getOrderId())
                .productId(dto.getProductId())
                .userId(dto.getUserId())
                .status(OrderStatus.CREATED)
                .build();
    }

    public static OrderResponseDTO entityToDto(final PurchaseOrder purchaseOrder) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(purchaseOrder.getId());
        dto.setProductId(purchaseOrder.getProductId());
        dto.setUserId(purchaseOrder.getUserId());
        dto.setStatus(purchaseOrder.getStatus());
        dto.setAmount(purchaseOrder.getPrice());
        return dto;
    }

}
