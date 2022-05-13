package net.patterns.saga.paymentservice.support;

import net.patterns.saga.common.model.payment.PaymentRequestDTO;
import net.patterns.saga.common.model.payment.PaymentResponseDTO;

public class DtoConverter {
    private DtoConverter() {
    }

    public static PaymentResponseDTO requestToResponse(final PaymentRequestDTO dto) {
        return PaymentResponseDTO.builder()
                .amount(dto.getAmount())
                .userId(dto.getUserId())
                .orderId(dto.getOrderId())
                .build();
    }
}
