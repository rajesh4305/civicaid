package com.civicaid.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "compliance_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ComplianceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complianceId;

    @Column(nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplianceResult result;

    @CreationTimestamp
    private LocalDateTime date;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    public enum EntityType {
        APPLICATION, PROGRAM, CITIZEN, DISBURSEMENT
    }

    public enum ComplianceResult {
        COMPLIANT, NON_COMPLIANT, UNDER_REVIEW, PENDING
    }
}
