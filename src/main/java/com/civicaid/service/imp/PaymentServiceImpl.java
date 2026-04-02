package com.civicaid.service.impl;

import com.civicaid.dto.request.PaymentRequest;
import com.civicaid.dto.response.PaymentResponse;
import com.civicaid.entity.Disbursement;
import com.civicaid.entity.Notification;
import com.civicaid.entity.Payment;
import com.civicaid.exception.ResourceNotFoundException;
import com.civicaid.repository.DisbursementRepository;
import com.civicaid.repository.PaymentRepository;
import com.civicaid.service.NotificationService;
import com.civicaid.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final DisbursementRepository disbursementRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request) {
        Disbursement disbursement = disbursementRepository.findById(request.getDisbursementId())
                .orElseThrow(() -> new ResourceNotFoundException("Disbursement", request.getDisbursementId()));

        Payment payment = Payment.builder()
                .disbursement(disbursement)
                .method(request.getMethod())
                .amount(request.getAmount())
                .bankAccountNumber(request.getBankAccountNumber())
                .walletId(request.getWalletId())
                .transactionReference(request.getTransactionReference())
                .status(Payment.PaymentStatus.INITIATED)
                .build();

        payment = paymentRepository.save(payment);

        disbursement.setStatus(Disbursement.DisbursementStatus.PROCESSING);
        disbursementRepository.save(disbursement);

        notificationService.sendNotification(
                disbursement.getApplication().getCitizen().getUser().getUserId(),
                payment.getPaymentId(),
                "Payment of ₹" + request.getAmount() + " initiated via " + request.getMethod().name(),
                Notification.NotificationCategory.DISBURSEMENT
        );

        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {
        return mapToResponse(paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id)));
    }

    @Override
    public List<PaymentResponse> getPaymentsByDisbursement(Long disbursementId) {
        return paymentRepository.findByDisbursement_DisbursementId(disbursementId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public Page<PaymentResponse> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public PaymentResponse updatePaymentStatus(Long id, Payment.PaymentStatus status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        payment.setStatus(status);

        if (status == Payment.PaymentStatus.SUCCESS) {
            Disbursement disbursement = payment.getDisbursement();
            disbursement.setStatus(Disbursement.DisbursementStatus.COMPLETED);
            disbursementRepository.save(disbursement);

            notificationService.sendNotification(
                    disbursement.getApplication().getCitizen().getUser().getUserId(),
                    payment.getPaymentId(),
                    "Payment of ₹" + payment.getAmount() + " completed successfully.",
                    Notification.NotificationCategory.DISBURSEMENT
            );
        }

        return mapToResponse(paymentRepository.save(payment));
    }

    private PaymentResponse mapToResponse(Payment p) {
        return PaymentResponse.builder()
                .paymentId(p.getPaymentId())
                .disbursementId(p.getDisbursement().getDisbursementId())
                .method(p.getMethod())
                .amount(p.getAmount())
                .transactionReference(p.getTransactionReference())
                .bankAccountNumber(p.getBankAccountNumber())
                .walletId(p.getWalletId())
                .date(p.getDate())
                .status(p.getStatus())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
