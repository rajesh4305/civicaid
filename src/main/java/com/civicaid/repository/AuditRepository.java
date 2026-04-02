package com.civicaid.repository;

import com.civicaid.entity.Audit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {
    Page<Audit> findByOfficer_UserId(Long officerId, Pageable pageable);
    Page<Audit> findByStatus(Audit.AuditStatus status, Pageable pageable);
    long countByStatus(Audit.AuditStatus status);
}
