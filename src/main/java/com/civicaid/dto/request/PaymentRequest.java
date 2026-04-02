package com.civicaid.dto.request;

import com.civicaid.entity.Payment;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    @NotNull
    private Long disbursementId;
    @NotNull
    private Payment.PaymentMethod method;
    @NotNull @Positive
    private BigDecimal amount;
    private String bankAccountNumber;
    private String walletId;
    private String transactionReference;
}
