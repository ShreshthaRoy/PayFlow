
package com.payflow.payflow.repository;

import com.payflow.payflow.entity.Payment;
import com.payflow.payflow.entity.PaymentStatus;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PaymentRepositoryIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    // Test 1: Save and retrieve a payment from PostgreSQL
    @Test
    void savePayment_shouldPersistAndRetrievePayment() {

        Payment payment = new Payment();

        payment.setSenderAccount("ACC1001");
        payment.setReceiverAccount("ACC2001");
        payment.setAmount(new BigDecimal("2500.50"));
        payment.setCurrency("INR");
        payment.setStatus(PaymentStatus.PENDING);

        Payment saved = paymentRepository.save(payment);

        assertNotNull(saved.getId());

        Payment retrieved = paymentRepository.findById(saved.getId())
                .orElseThrow();

        assertEquals("ACC1001", retrieved.getSenderAccount());
        assertEquals("ACC2001", retrieved.getReceiverAccount());
        assertEquals(0,
                retrieved.getAmount().compareTo(new BigDecimal("2500.50")));
        assertEquals(PaymentStatus.PENDING, retrieved.getStatus());
    }

    // Test 2: Find a payment using its idempotency key
    @Test
    void findByIdempotencyKey_shouldReturnCorrectPayment() {

        String key = "integration-" + UUID.randomUUID();

        Payment payment = new Payment();

        payment.setSenderAccount("ACC3001");
        payment.setReceiverAccount("ACC4001");
        payment.setAmount(new BigDecimal("1500.00"));
        payment.setCurrency("INR");
        payment.setStatus(PaymentStatus.PENDING);
        payment.setIdempotencyKey(key);

        paymentRepository.save(payment);

        Payment retrieved = paymentRepository
                .findByIdempotencyKey(key)
                .orElseThrow();

        assertEquals(key, retrieved.getIdempotencyKey());
        assertEquals("ACC3001", retrieved.getSenderAccount());
    }
}
