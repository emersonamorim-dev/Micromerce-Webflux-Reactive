package com.webflux.micromerce.payment.presentation.exception;

public class PaymentConversionException extends RuntimeException {
    public PaymentConversionException(String message) {
        super(message);
    }

    public PaymentConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}