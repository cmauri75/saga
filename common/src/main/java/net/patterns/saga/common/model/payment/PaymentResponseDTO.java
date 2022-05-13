package net.patterns.saga.common.model.payment;

import lombok.Data;

import java.util.UUID;

@Data
public class PaymentResponseDTO {
    private Integer userId;
    private UUID orderId;
    private Double amount;

    private PaymentStatus status;
}
