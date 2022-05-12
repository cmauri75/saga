package net.patterns.saga.orderservice.entity;

import lombok.Data;
import lombok.ToString;
import net.patterns.saga.common.model.order.OrderStatus;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Data
@Entity
@ToString
public class PurchaseOrder {
    @Id
    private UUID id;
    private Integer userId;
    private Integer productId;
    private Double price;
    private OrderStatus status;
}
