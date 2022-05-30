package net.patterns.saga.orderservice.service;

import io.nats.client.Connection;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.patterns.saga.common.model.inventory.InventoryRequestDTO;
import net.patterns.saga.common.model.inventory.InventoryResponseDTO;
import net.patterns.saga.common.model.order.OrderRequestDTO;
import net.patterns.saga.common.model.order.OrderResponseDTO;
import net.patterns.saga.common.model.payment.PaymentRequestDTO;
import net.patterns.saga.common.model.payment.PaymentResponseDTO;
import net.patterns.saga.common.util.ObjectUtil;
import net.patterns.saga.orderservice.entity.PurchaseOrder;
import net.patterns.saga.orderservice.repository.PurchaseOrderRepository;
import net.patterns.saga.orderservice.support.OrderDtoConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static net.patterns.saga.common.util.Constants.ORCHESTRATOR_NATS_ORDER_SUBJECT;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    @NonNull
    private PurchaseOrderRepository purchaseOrderRepository;
    @NonNull
    private final Connection nats;

    @NonNull
    private RestTemplate restTemplate;

    @Value("${paymentService.url}")
    private String paymentServiceUrl;

    @Value("${inventoryService.url}")
    private String inventoryServiceUrl;

    private PurchaseOrder storeOrder(OrderRequestDTO orderRequestDTO) {
        final PurchaseOrder entity = OrderDtoConverter.dtoToEntity(orderRequestDTO);

        //an external service should be called
        entity.setPrice(orderRequestDTO.getProductId() * 10.0d);

        //save locally
        return purchaseOrderRepository.save(entity);
    }

    public PurchaseOrder createOrderNonTransactional(OrderRequestDTO orderRequestDTO) {
        //----- START FALSE TRANSACTION -------------------------------

        log.info("Starting \"transaction\"");

        //save order locally
        PurchaseOrder purchaseOrder = storeOrder(orderRequestDTO);
        log.info("Order stored: " + purchaseOrder.getId());

        //get money
        PaymentRequestDTO requestP = PaymentRequestDTO.builder()
                .orderId(orderRequestDTO.getOrderId())
                .userId(purchaseOrder.getUserId())
                .amount(purchaseOrder.getPrice())
                .build();
        PaymentResponseDTO resultP = restTemplate.postForObject(paymentServiceUrl + "/debit", requestP, PaymentResponseDTO.class);
        log.info("Payment got: " + resultP);

        //take item from inventory
        InventoryRequestDTO requestI = InventoryRequestDTO.builder()
                .userId(purchaseOrder.getUserId())
                .productId(purchaseOrder.getProductId())
                .orderId(orderRequestDTO.getOrderId())
                .build();
        InventoryResponseDTO result = restTemplate.postForObject(inventoryServiceUrl + "/take", requestI, InventoryResponseDTO.class);
        log.info("Inventory got: " + result);

        // ---- END FALSE TRANSACTION --------------------------------------

        return purchaseOrder;
    }

//----------------SAGA WITH ORCHESTRATION
    public PurchaseOrder createOrderSagaOrchestration(OrderRequestDTO orderRequestDTO) {
        log.info("Starting saga orchestration transaction");

        //save order locally
        PurchaseOrder purchaseOrder = storeOrder(orderRequestDTO);
        log.info("Order stored: ", purchaseOrder.getId());

        //inform orchestrator
        OrderResponseDTO orderResponse = OrderDtoConverter.entityToDto(purchaseOrder);
        sendNatsMessage(orderResponse);
        log.info("Orchestrator informed: {}", orderResponse.getOrderId());

        return purchaseOrder;
    }

    private void sendNatsMessage(OrderResponseDTO order) {
        //just send message
        nats.publish(ORCHESTRATOR_NATS_ORDER_SUBJECT, ObjectUtil.toBytes(order));
    }

    //---SAGA ORCHESTRATION END------------------

    public List<OrderResponseDTO> getAll() {
        return this.purchaseOrderRepository.findAll()
                .stream()
                .map(OrderDtoConverter::entityToDto)
                .toList();
    }

}
