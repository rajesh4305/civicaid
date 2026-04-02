package com.civicaid.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disbursement_id", nullable = false)
    private Disbursement disbursement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    private String transactionReference;
    private String bankAccountNumber;
    private String walletId;

    @CreationTimestamp
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.INITIATED;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum PaymentMethod {
        BANK_TRANSFER, WALLET, CASH, CHEQUE
    }

    public enum PaymentStatus {
        INITIATED, PROCESSING, SUCCESS, FAILED, REVERSED
    }
}
