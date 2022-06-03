package net.patterns.saga.orderservice.controller;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.patterns.saga.common.model.order.OrderRequestDTO;
import net.patterns.saga.common.model.order.OrderResponseDTO;
import net.patterns.saga.orderservice.entity.PurchaseOrder;
import net.patterns.saga.orderservice.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order-service")
@AllArgsConstructor
public class OrderController {
    private final OrderService service;

    public enum TransactType {NOTRANS, ORCHESTRATOR, CHOREOGRAPHY}

    @SneakyThrows
    @PostMapping("/")
    public PurchaseOrder createOrder(@RequestBody OrderRequestDTO requestDTO, @RequestParam TransactType transactType) {
        requestDTO.setOrderId(UUID.randomUUID());
        return switch (transactType) {
            case NOTRANS -> this.service.createOrderNonTransactional(requestDTO);
            case ORCHESTRATOR -> this.service.createOrderSagaOrchestration(requestDTO);
            case CHOREOGRAPHY -> this.service.createOrderSagaChoreography(requestDTO);
        };
    }

    @GetMapping("/")
    public List<OrderResponseDTO> getOrders() {
        return this.service.getAll();
    }
}
