
package com.payflow.payflow.dto;

import com.payflow.payflow.entity.Payment;
import com.payflow.payflow.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
    UUID id,
    String senderAccount,
    String receiverAccount,
    BigDecimal amount,
    String currency,
    PaymentStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
            payment.getId(),
            payment.getSenderAccount(),
            payment.getReceiverAccount(),
            payment.getAmount(),
            payment.getCurrency(),
            payment.getStatus(),
            payment.getCreatedAt(),
            payment.getUpdatedAt()
        );
    }
}
