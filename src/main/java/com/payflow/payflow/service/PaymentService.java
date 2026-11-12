
package com.payflow.payflow.service;

import com.payflow.payflow.dto.CreatePaymentRequest;
import com.payflow.payflow.entity.Payment;
import com.payflow.payflow.entity.PaymentStatus;
import com.payflow.payflow.repository.PaymentRepository;
import com.payflow.payflow.exception.PaymentNotFoundException;
import com.payflow.payflow.dto.UpdatePaymentStatusRequest;
import com.payflow.payflow.exception.InvalidPaymentStatusException;
import com.payflow.payflow.exception.IdempotencyKeyConflictException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment createPayment(
            CreatePaymentRequest request,
            String idempotencyKey) {

        var existingPayment = paymentRepository
                .findByIdempotencyKey(idempotencyKey);

        if (existingPayment.isPresent()) {

            Payment existing = existingPayment.get();

            boolean sameRequest =
                    existing.getSenderAccount().equals(request.senderAccount())
                    && existing.getReceiverAccount().equals(request.receiverAccount())
                    && existing.getAmount().compareTo(request.amount()) == 0
                    && existing.getCurrency().equals(request.currency());

            if (!sameRequest) {
                throw new IdempotencyKeyConflictException(
                        "This idempotency key has already been used for a different payment."
                );
            }

            return existing;
        }

        Payment payment = new Payment();

        payment.setIdempotencyKey(idempotencyKey);
        payment.setSenderAccount(request.senderAccount());
        payment.setReceiverAccount(request.receiverAccount());
        payment.setAmount(request.amount());
        payment.setCurrency(request.currency());
        payment.setStatus(PaymentStatus.PENDING);

        return paymentRepository.save(payment);
    }

    
    public Payment getPaymentById(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() ->
                    new PaymentNotFoundException(
                        "Payment not found with ID: " + id
                    ));
    }


    public Page<Payment> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }


    public Payment updatePaymentStatus(
            UUID id,
            UpdatePaymentStatusRequest request) {

        Payment payment = getPaymentById(id);

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidPaymentStatusException(
                "Only PENDING payments can be updated."
            );
        }

        if (request.status() == null ||
                request.status() == PaymentStatus.PENDING) {
            throw new InvalidPaymentStatusException(
                "Payment can only be updated to SUCCESS or FAILED."
            );
        }

        payment.setStatus(request.status());

        return paymentRepository.save(payment);
    }
}
