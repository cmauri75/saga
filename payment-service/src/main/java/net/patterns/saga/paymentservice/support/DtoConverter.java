package net.patterns.saga.paymentservice.support;

import net.patterns.saga.common.model.payment.PaymentRequestDTO;
import net.patterns.saga.common.model.payment.PaymentResponseDTO;

public class DtoConverter {

    private DtoConverter() {
    }


    public static PaymentResponseDTO requestToResponse(final PaymentRequestDTO dto) {
        PaymentResponseDTO responseDTO = new PaymentResponseDTO();
        responseDTO.setAmount(dto.getAmount());
        responseDTO.setUserId(dto.getUserId());
        responseDTO.setOrderId(dto.getOrderId());
        return responseDTO;
    }


}
