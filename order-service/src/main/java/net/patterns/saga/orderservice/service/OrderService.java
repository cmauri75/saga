package net.patterns.saga.orderservice.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.patterns.saga.common.model.inventory.InventoryRequestDTO;
import net.patterns.saga.common.model.inventory.InventoryResponseDTO;
import net.patterns.saga.common.model.order.OrderRequestDTO;
import net.patterns.saga.common.model.order.OrderResponseDTO;
import net.patterns.saga.common.model.payment.PaymentRequestDTO;
import net.patterns.saga.common.model.payment.PaymentResponseDTO;
import net.patterns.saga.orderservice.entity.PurchaseOrder;
import net.patterns.saga.orderservice.repository.PurchaseOrderRepository;
import net.patterns.saga.orderservice.support.OrderDtoConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    @NonNull
    private PurchaseOrderRepository purchaseOrderRepository;

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${paymentService.url}")
    private String paymentServiceUrl;

    @Value("${inventoryService.url}")
    private String inventoryServiceUrl;

    public PurchaseOrder createOrderNonTransactional(OrderRequestDTO orderRequestDTO) {
        final PurchaseOrder entity = OrderDtoConverter.dtoToEntity(orderRequestDTO);

        //an external service should be called
        entity.setPrice(orderRequestDTO.getProductId() * 10.0d);

        ///// ---- TRANSACTION INIT ----------------------------------
        //save locally
        PurchaseOrder purchaseOrder = this.purchaseOrderRepository.save(entity);

        //get money
        PaymentRequestDTO requestP = PaymentRequestDTO.builder()
                .orderId(orderRequestDTO.getOrderId())
                .userId(entity.getUserId())
                .amount(entity.getPrice())
                .build();
        PaymentResponseDTO resultP = restTemplate.postForObject(paymentServiceUrl + "/debit", requestP, PaymentResponseDTO.class);
        log.info("Payment got: " + resultP);

        //take from inventory
        InventoryRequestDTO requestI = InventoryRequestDTO.builder()
                .userId(entity.getUserId())
                .productId(entity.getProductId())
                .orderId(orderRequestDTO.getOrderId())
                .build();
        InventoryResponseDTO result = restTemplate.postForObject(inventoryServiceUrl + "/take", requestI, InventoryResponseDTO.class);
        log.info("Inventory got: " + result);

        // ---- END OF TRANSACTION --------------------------------------

        return purchaseOrder;
    }

    public List<OrderResponseDTO> getAll() {
        return this.purchaseOrderRepository.findAll()
                .stream()
                .map(OrderDtoConverter::entityToDto)
                .toList();
    }

}
