package com.civicaid.repository;

import com.civicaid.entity.EligibilityCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EligibilityCheckRepository extends JpaRepository<EligibilityCheck, Long> {
    List<EligibilityCheck> findByApplication_ApplicationId(Long applicationId);
    List<EligibilityCheck> findByOfficer_UserId(Long officerId);
    Optional<EligibilityCheck> findTopByApplication_ApplicationIdOrderByDateDesc(Long applicationId);
    long countByResult(EligibilityCheck.CheckResult result);
}
