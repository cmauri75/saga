package net.patterns.saga.orderservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.patterns.saga.common.model.order.OrderRequestDTO;
import net.patterns.saga.common.model.order.OrderResponseDTO;
import net.patterns.saga.orderservice.entity.PurchaseOrder;
import net.patterns.saga.orderservice.repository.PurchaseOrderRepository;
import net.patterns.saga.orderservice.support.OrderDtoConverter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class OrderService {
    private PurchaseOrderRepository purchaseOrderRepository;

    public PurchaseOrder createOrder(OrderRequestDTO orderRequestDTO) {
        final PurchaseOrder entity = OrderDtoConverter.dtoToEntity(orderRequestDTO);

        //an external service should be called
        entity.setPrice(orderRequestDTO.getProductId() * 1000.0d);

        PurchaseOrder purchaseOrder = this.purchaseOrderRepository.save(entity);

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
