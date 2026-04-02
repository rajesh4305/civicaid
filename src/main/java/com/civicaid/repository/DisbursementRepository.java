package com.civicaid.repository;

import com.civicaid.entity.Disbursement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DisbursementRepository extends JpaRepository<Disbursement, Long> {
    List<Disbursement> findByApplication_ApplicationId(Long applicationId);
    Page<Disbursement> findByStatus(Disbursement.DisbursementStatus status, Pageable pageable);

    @Query("SELECT SUM(d.amount) FROM Disbursement d WHERE d.status = 'COMPLETED'")
    BigDecimal sumCompletedDisbursements();

    @Query("SELECT SUM(d.amount) FROM Disbursement d WHERE d.application.program.programId = :programId AND d.status = 'COMPLETED'")
    BigDecimal sumDisbursedAmountByProgram(Long programId);

    @Query("SELECT COUNT(d) FROM Disbursement d WHERE d.status = :status")
    long countByStatus(Disbursement.DisbursementStatus status);
}
