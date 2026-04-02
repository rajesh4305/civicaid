package com.civicaid.repository;

import com.civicaid.entity.Program;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
    Page<Program> findByStatus(Program.ProgramStatus status, Pageable pageable);
    List<Program> findByStatusAndEndDateAfter(Program.ProgramStatus status, LocalDate date);

    @Query("SELECT p FROM Program p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Program> searchByTitle(String keyword, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Program p WHERE p.status = :status")
    long countByStatus(Program.ProgramStatus status);

    @Query("SELECT SUM(p.budget) FROM Program p WHERE p.status = 'ACTIVE'")
    java.math.BigDecimal sumActiveProgramBudgets();
}
