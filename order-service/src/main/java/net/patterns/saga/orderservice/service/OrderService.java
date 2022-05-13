package net.patterns.saga.orderservice.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.patterns.saga.common.model.order.OrderRequestDTO;
import net.patterns.saga.common.model.order.OrderResponseDTO;
import net.patterns.saga.common.model.payment.PaymentRequestDTO;
import net.patterns.saga.common.model.payment.PaymentResponseDTO;
import net.patterns.saga.common.model.storing.StoreCounter;
import net.patterns.saga.orderservice.entity.PurchaseOrder;
import net.patterns.saga.orderservice.repository.PurchaseOrderRepository;
import net.patterns.saga.orderservice.support.OrderDtoConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    @NonNull
    private PurchaseOrderRepository purchaseOrderRepository;

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${paymentService.url}")
    private String paymentServiceUrl;

    public PurchaseOrder createOrder(OrderRequestDTO orderRequestDTO) {
        final PurchaseOrder entity = OrderDtoConverter.dtoToEntity(orderRequestDTO);

        //an external service should be called
        entity.setPrice(orderRequestDTO.getProductId() * 10.0d);

        PurchaseOrder purchaseOrder = this.purchaseOrderRepository.save(entity);

        PaymentRequestDTO payment = PaymentRequestDTO.builder()
                .orderId(UUID.randomUUID())
                .userId(entity.getUserId())
                .amount(entity.getPrice())
                .build();
        PaymentResponseDTO result = restTemplate.postForObject(paymentServiceUrl + "/debit", payment, PaymentResponseDTO.class);
        log.info("Payment got: " + result);

        //inform orchestrator?
        //this.sink.next(this.getOrchestratorRequestDTO(orderRequestDTO));

        return purchaseOrder;
    }

    public List<OrderResponseDTO> getAll() {
        return this.purchaseOrderRepository.findAll()
                .stream()
                .map(OrderDtoConverter::entityToDto)
                .toList();
    }

}
