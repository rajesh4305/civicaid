package com.civicaid.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "eligibility_checks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EligibilityCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long checkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private WelfareApplication application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id", nullable = false)
    private User officer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckResult result;

    @CreationTimestamp
    private LocalDateTime date;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public enum CheckResult {
        ELIGIBLE, INELIGIBLE, PENDING_DOCUMENTS, FURTHER_REVIEW
    }
}
