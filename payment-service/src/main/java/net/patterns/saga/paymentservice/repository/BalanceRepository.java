package net.patterns.saga.paymentservice.repository;

import net.patterns.saga.paymentservice.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Integer> {
}
