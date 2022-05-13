package net.patterns.saga.paymentservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.patterns.saga.common.model.payment.PaymentRequestDTO;
import net.patterns.saga.common.model.payment.PaymentResponseDTO;
import net.patterns.saga.common.model.payment.PaymentStatus;
import net.patterns.saga.paymentservice.entity.Balance;
import net.patterns.saga.paymentservice.entity.Payment;
import net.patterns.saga.paymentservice.repository.BalanceRepository;
import net.patterns.saga.paymentservice.repository.PaymentRepository;
import net.patterns.saga.paymentservice.support.BalanceNotFoundException;
import net.patterns.saga.paymentservice.support.DtoConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class PaymentService {
    private BalanceRepository balanceRepository;
    private PaymentRepository paymentRepository;

    @Transactional
    public PaymentResponseDTO debit(final PaymentRequestDTO requestDTO) throws BalanceNotFoundException {
        log.info("Going to debit: {}",requestDTO);
        Optional<Balance> oBalance = balanceRepository.findById(requestDTO.getUserId());
        if (oBalance.isEmpty())
            throw new BalanceNotFoundException();

        Balance userBalance = oBalance.get();

        PaymentResponseDTO responseDTO = DtoConverter.requestToResponse(requestDTO);

        if (userBalance.getTotalBalance() >= requestDTO.getAmount()) {
            responseDTO.setStatus(PaymentStatus.APPROVED);
            userBalance.setTotalBalance(userBalance.getTotalBalance() - requestDTO.getAmount());

            Payment paymentLog = Payment.builder()
                    .id(UUID.randomUUID())
                    .amount(requestDTO.getAmount())
                    .userId(requestDTO.getUserId())
                    .transactionRef(requestDTO.getOrderId())
                    .build();
            paymentRepository.save(paymentLog);

        } else responseDTO.setStatus(PaymentStatus.REJECTED);

        return responseDTO;
    }

    @Transactional
    public void credit(final PaymentRequestDTO requestDTO) throws BalanceNotFoundException {
        log.info("Going to credit: {}",requestDTO);

        Optional<Balance> oBalance = balanceRepository.findById(requestDTO.getUserId());
        if (oBalance.isEmpty())
            throw new BalanceNotFoundException();
        Balance userBalance = oBalance.get();

        userBalance.setTotalBalance(userBalance.getTotalBalance() + requestDTO.getAmount());
    }

    public double getCredit(Integer userId) {
        return balanceRepository
                .findById(userId)
                .orElse(Balance.builder().totalBalance(-1d).build())
                .getTotalBalance();
    }

    public List<Payment> getPayments() {
        return paymentRepository.findAll();
    }
}
