package net.patterns.saga.common.model.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {
    private Integer userId;
    private UUID orderId;
    private Double amount;
}
