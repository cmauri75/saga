package net.patterns.saga.orderservice.controller;

import lombok.AllArgsConstructor;
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

    @PostMapping("/")
    public PurchaseOrder createOrder(@RequestBody OrderRequestDTO requestDTO) {
        requestDTO.setOrderId(UUID.randomUUID());
        return this.service.createOrderNonTransactional(requestDTO);
    }

    @GetMapping("/")
    public List<OrderResponseDTO> getOrders() {
        return this.service.getAll();
    }

}
