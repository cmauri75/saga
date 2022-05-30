package net.patterns.saga.orchestratorservice.support;


import net.patterns.saga.common.model.inventory.InventoryRequestDTO;
import net.patterns.saga.common.model.inventory.InventoryResponseDTO;
import net.patterns.saga.common.model.orchestrator.OrchestratorRequestDTO;
import net.patterns.saga.common.model.orchestrator.OrchestratorResponseDTO;
import net.patterns.saga.common.model.order.OrderResponseDTO;
import net.patterns.saga.common.model.order.OrderStatus;
import net.patterns.saga.common.model.payment.PaymentRequestDTO;

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

    public static OrchestratorRequestDTO orderToOrchestratorRequest(OrderResponseDTO order) {
        OrchestratorRequestDTO requestDTO = new OrchestratorRequestDTO();
        requestDTO.setUserId(order.getUserId());
        requestDTO.setAmount(order.getAmount());
        requestDTO.setOrderId(order.getOrderId());
        requestDTO.setProductId(order.getProductId());
        return requestDTO;
    }

    public static OrchestratorResponseDTO getResponseDTO(OrchestratorRequestDTO requestDTO, OrderStatus status){
        OrchestratorResponseDTO responseDTO = new OrchestratorResponseDTO();
        responseDTO.setOrderId(requestDTO.getOrderId());
        responseDTO.setAmount(requestDTO.getAmount());
        responseDTO.setProductId(requestDTO.getProductId());
        responseDTO.setUserId(requestDTO.getUserId());
        responseDTO.setStatus(status);
        return responseDTO;
    }

    public static PaymentRequestDTO getPaymentRequestDTO(OrchestratorRequestDTO requestDTO){
        PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO();
        paymentRequestDTO.setUserId(requestDTO.getUserId());
        paymentRequestDTO.setAmount(requestDTO.getAmount());
        paymentRequestDTO.setOrderId(requestDTO.getOrderId());
        return paymentRequestDTO;
    }

    public static InventoryRequestDTO getInventoryRequestDTO(OrchestratorRequestDTO requestDTO){
        InventoryRequestDTO inventoryRequestDTO = new InventoryRequestDTO();
        inventoryRequestDTO.setUserId(requestDTO.getUserId());
        inventoryRequestDTO.setProductId(requestDTO.getProductId());
        inventoryRequestDTO.setOrderId(requestDTO.getOrderId());
        return inventoryRequestDTO;
    }
}
