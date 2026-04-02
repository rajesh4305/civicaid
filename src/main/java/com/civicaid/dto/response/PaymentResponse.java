package com.civicaid.dto.response;

import com.civicaid.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private Long disbursementId;
    private Payment.PaymentMethod method;
    private BigDecimal amount;
    private String transactionReference;
    private String bankAccountNumber;
    private String walletId;
    private LocalDateTime date;
    private Payment.PaymentStatus status;
    private LocalDateTime updatedAt;
}
