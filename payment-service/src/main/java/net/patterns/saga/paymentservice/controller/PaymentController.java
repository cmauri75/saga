package net.patterns.saga.paymentservice.controller;

import lombok.AllArgsConstructor;
import net.patterns.saga.common.model.payment.PaymentRequestDTO;
import net.patterns.saga.common.model.payment.PaymentResponseDTO;
import net.patterns.saga.paymentservice.entity.Payment;
import net.patterns.saga.paymentservice.service.PaymentService;
import net.patterns.saga.paymentservice.support.BalanceNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment-service")
@AllArgsConstructor
public class PaymentController {
    private final PaymentService service;

    @PostMapping("/debit")
    public PaymentResponseDTO debit(@RequestBody PaymentRequestDTO requestDTO) throws BalanceNotFoundException {
        return service.debit(requestDTO);
    }

    @GetMapping("/debit")
    public List<Payment> debit()  {
        return service.getPayments();
    }

    @PostMapping("/credit")
    public void credit(@RequestBody PaymentRequestDTO requestDTO) throws BalanceNotFoundException {
        service.credit(requestDTO);
    }

    @GetMapping("/credit/{userId}")
    public Double credit(@PathVariable("userId") Integer userId) {
        return service.getCredit(userId);
    }

}
