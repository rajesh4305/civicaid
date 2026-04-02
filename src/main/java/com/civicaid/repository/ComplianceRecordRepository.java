package com.civicaid.repository;

import com.civicaid.entity.ComplianceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {
    List<ComplianceRecord> findByEntityIdAndType(Long entityId, ComplianceRecord.EntityType type);
    Page<ComplianceRecord> findByResult(ComplianceRecord.ComplianceResult result, Pageable pageable);
    Page<ComplianceRecord> findByType(ComplianceRecord.EntityType type, Pageable pageable);

    @Query("SELECT COUNT(c) FROM ComplianceRecord c WHERE c.result = :result")
    long countByResult(ComplianceRecord.ComplianceResult result);
}
