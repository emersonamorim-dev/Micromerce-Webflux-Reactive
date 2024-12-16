package com.webflux.micromerce.payment.presentation.exception;

public class PaymentMapperException extends RuntimeException {
    public PaymentMapperException(String message) {
        super(message);
    }

    public PaymentMapperException(String message, Throwable cause) {
        super(message, cause);
    }
}
