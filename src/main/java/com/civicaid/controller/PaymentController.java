package com.civicaid.controller;

import com.civicaid.dto.request.PaymentRequest;
import com.civicaid.dto.response.ApiResponse;
import com.civicaid.dto.response.PaymentResponse;
import com.civicaid.entity.Payment;
import com.civicaid.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('WELFARE_OFFICER','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(paymentService.initiatePayment(request), "Payment initiated"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('WELFARE_OFFICER','PROGRAM_MANAGER','ADMINISTRATOR','COMPLIANCE_OFFICER','GOVERNMENT_AUDITOR')")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getAllPayments(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getAllPayments(pageable)));
    }

    @GetMapping("/disbursement/{disbursementId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByDisbursement(
            @PathVariable Long disbursementId) {
        return ResponseEntity.ok(ApiResponse.success(
                paymentService.getPaymentsByDisbursement(disbursementId)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('WELFARE_OFFICER','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<PaymentResponse>> updatePaymentStatus(
            @PathVariable Long id, @RequestParam Payment.PaymentStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                paymentService.updatePaymentStatus(id, status), "Payment status updated"));
    }
}
