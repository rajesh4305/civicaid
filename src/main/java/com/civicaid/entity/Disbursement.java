package com.civicaid.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "disbursements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Disbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long disbursementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private WelfareApplication application;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @CreationTimestamp
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DisbursementStatus status = DisbursementStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @OneToMany(mappedBy = "disbursement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum DisbursementStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED
    }
}
