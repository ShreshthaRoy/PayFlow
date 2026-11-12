
package com.payflow.payflow.service;

import com.payflow.payflow.dto.CreatePaymentRequest;
import com.payflow.payflow.dto.UpdatePaymentStatusRequest;
import com.payflow.payflow.entity.Payment;
import com.payflow.payflow.entity.PaymentStatus;
import com.payflow.payflow.exception.IdempotencyKeyConflictException;
import com.payflow.payflow.exception.InvalidPaymentStatusException;
import com.payflow.payflow.exception.PaymentNotFoundException;
import com.payflow.payflow.repository.PaymentRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private CreatePaymentRequest createRequest() {
        return new CreatePaymentRequest(
                "ACC1001",
                "ACC2001",
                new BigDecimal("2500.50"),
                "INR"
        );
    }

    // Test 1: Create a new payment
    @Test
    void createPayment_shouldSaveNewPayment() {

        when(paymentRepository.findByIdempotencyKey("key-001"))
                .thenReturn(Optional.empty());

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.createPayment(
                createRequest(), "key-001"
        );

        assertNotNull(result);
        assertEquals("ACC1001", result.getSenderAccount());
        assertEquals("ACC2001", result.getReceiverAccount());
        assertEquals(new BigDecimal("2500.50"), result.getAmount());
        assertEquals(PaymentStatus.PENDING, result.getStatus());

        verify(paymentRepository).save(any(Payment.class));
    }

    // Test 2: Return existing payment for the same key and request
    @Test
    void createPayment_shouldReturnExistingPaymentForDuplicateRequest() {

        Payment existing = new Payment();
        existing.setSenderAccount("ACC1001");
        existing.setReceiverAccount("ACC2001");
        existing.setAmount(new BigDecimal("2500.50"));
        existing.setCurrency("INR");
        existing.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findByIdempotencyKey("key-001"))
                .thenReturn(Optional.of(existing));

        Payment result = paymentService.createPayment(
                createRequest(), "key-001"
        );

        assertSame(existing, result);

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    // Test 3: Reject reused key with different payment details
    @Test
    void createPayment_shouldRejectDifferentRequestWithSameKey() {

        Payment existing = new Payment();
        existing.setSenderAccount("ACC1001");
        existing.setReceiverAccount("ACC2001");
        existing.setAmount(new BigDecimal("2500.50"));
        existing.setCurrency("INR");

        when(paymentRepository.findByIdempotencyKey("key-001"))
                .thenReturn(Optional.of(existing));

        CreatePaymentRequest differentRequest =
                new CreatePaymentRequest(
                        "ACC1001",
                        "ACC2001",
                        new BigDecimal("5000"),
                        "INR"
                );

        assertThrows(
                IdempotencyKeyConflictException.class,
                () -> paymentService.createPayment(
                        differentRequest, "key-001"
                )
        );

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    // Test 4: Retrieve an existing payment
    @Test
    void getPaymentById_shouldReturnPayment() {

        UUID id = UUID.randomUUID();

        Payment payment = new Payment();

        when(paymentRepository.findById(id))
                .thenReturn(Optional.of(payment));

        Payment result = paymentService.getPaymentById(id);

        assertSame(payment, result);
    }

    // Test 5: Throw exception if payment does not exist
    @Test
    void getPaymentById_shouldThrowExceptionWhenNotFound() {

        UUID id = UUID.randomUUID();

        when(paymentRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.getPaymentById(id)
        );
    }

    // Test 6: Update payment status successfully
    @Test
    void updatePaymentStatus_shouldUpdatePendingPayment() {

        UUID id = UUID.randomUUID();

        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(id))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdatePaymentStatusRequest request =
                new UpdatePaymentStatusRequest(PaymentStatus.SUCCESS);

        Payment result = paymentService.updatePaymentStatus(id, request);

        assertEquals(PaymentStatus.SUCCESS, result.getStatus());

        verify(paymentRepository).save(payment);
    }

    // Test 7: Reject updating a completed payment
    @Test
    void updatePaymentStatus_shouldRejectCompletedPayment() {

        UUID id = UUID.randomUUID();

        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findById(id))
                .thenReturn(Optional.of(payment));

        UpdatePaymentStatusRequest request =
                new UpdatePaymentStatusRequest(PaymentStatus.FAILED);

        assertThrows(
                InvalidPaymentStatusException.class,
                () -> paymentService.updatePaymentStatus(id, request)
        );

        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
