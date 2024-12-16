package com.webflux.micromerce.payment.presentation.exception;

public class PaymentPresentationException extends RuntimeException {
    
    public PaymentPresentationException(String message) {
        super(message);
    }

    public PaymentPresentationException(String message, Throwable cause) {
        super(message, cause);
    }
}
