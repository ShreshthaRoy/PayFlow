package com.payflow.payflow.dto;

import com.payflow.payflow.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdatePaymentStatusRequest(

    @NotNull(message = "Payment status is required")
    PaymentStatus status

) {}