
package com.payflow.payflow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.payflow.payflow.exception.IdempotencyKeyConflictException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePaymentNotFound(
            PaymentNotFoundException ex) {

        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", "Payment Not Found");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
            errors.put(fieldError.getField(), fieldError.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(InvalidPaymentStatusException.class)
    public ResponseEntity<Map<String, String>> handleInvalidPaymentStatus(
            InvalidPaymentStatusException ex) {

        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", "Invalid Payment Status");
        error.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(error);
    }


    @ExceptionHandler(IdempotencyKeyConflictException.class)
    public ResponseEntity<Map<String, String>> handleIdempotencyConflict(
            IdempotencyKeyConflictException ex) {

        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", "Idempotency Key Conflict");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDatabaseConflict(
            DataIntegrityViolationException ex) {

        Map<String, String> response = new HashMap<>();

        response.put("error", "Conflict");
        response.put(
            "message",
            "A database constraint was violated. Check whether the idempotency key has already been used."
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
