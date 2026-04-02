package com.civicaid.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audits")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id", nullable = false)
    private User officer;

    @Column(nullable = false)
    private String scope;

    @Column(columnDefinition = "TEXT")
    private String findings;

    @CreationTimestamp
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AuditStatus status = AuditStatus.IN_PROGRESS;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum AuditStatus {
        SCHEDULED, IN_PROGRESS, COMPLETED, PENDING_REVIEW, CLOSED
    }
}
