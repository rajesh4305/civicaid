package com.civicaid.repository;

import com.civicaid.entity.Scheme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchemeRepository extends JpaRepository<Scheme, Long> {
    List<Scheme> findByProgram_ProgramId(Long programId);
    Page<Scheme> findByStatus(Scheme.SchemeStatus status, Pageable pageable);
    List<Scheme> findByProgram_ProgramIdAndStatus(Long programId, Scheme.SchemeStatus status);
}
