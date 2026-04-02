package com.civicaid.repository;

import com.civicaid.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByDisbursement_DisbursementId(Long disbursementId);
    Page<Payment> findByStatus(Payment.PaymentStatus status, Pageable pageable);
    Optional<Payment> findByTransactionReference(String transactionReference);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS'")
    BigDecimal sumSuccessfulPayments();

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.method = :method")
    long countByMethod(Payment.PaymentMethod method);
}
