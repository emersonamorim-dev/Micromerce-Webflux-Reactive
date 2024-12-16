package com.webflux.micromerce.payment.presentation.exception;

public class PaymentCancellationException extends RuntimeException {
    
    public PaymentCancellationException(String message) {
        super(message);
    }

    public PaymentCancellationException(String message, Throwable cause) {
        super(message, cause);
    }
}
