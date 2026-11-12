
package com.payflow.payflow.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreatePaymentRequest(

    @NotBlank(message = "Sender account is required")
    String senderAccount,

    @NotBlank(message = "Receiver account is required")
    String receiverAccount,

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Digits(integer = 17, fraction = 2)
    BigDecimal amount,

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$",
             message = "Currency must be a 3-letter uppercase code")
    String currency

) {}
