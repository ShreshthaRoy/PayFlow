
package com.payflow.payflow.controller;

import com.payflow.payflow.dto.CreatePaymentRequest;
import com.payflow.payflow.dto.PaymentResponse;
import com.payflow.payflow.entity.Payment;
import com.payflow.payflow.service.PaymentService;
import com.payflow.payflow.dto.UpdatePaymentStatusRequest;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(
    value = "/api/payments",
    produces = MediaType.APPLICATION_JSON_VALUE
)
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Create a payment
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreatePaymentRequest request) {

        Payment payment = paymentService.createPayment(request, idempotencyKey);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PaymentResponse.from(payment));
    }

    // Get payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable UUID id) {

        Payment payment = paymentService.getPaymentById(id);

        return ResponseEntity.ok(PaymentResponse.from(payment));
    }

    // Get all payments
    @GetMapping
    public ResponseEntity<Page<PaymentResponse>> getAllPayments(
            Pageable pageable) {
    
        Page<PaymentResponse> payments =
                paymentService.getAllPayments(pageable)
                        .map(PaymentResponse::from);
    
        return ResponseEntity.ok(payments);
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePaymentStatusRequest request) {

        Payment payment = paymentService.updatePaymentStatus(id, request);

        return ResponseEntity.ok(PaymentResponse.from(payment));
    }
}
