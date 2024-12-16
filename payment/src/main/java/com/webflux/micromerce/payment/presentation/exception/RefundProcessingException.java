package com.webflux.micromerce.payment.presentation.exception;

public class RefundProcessingException extends RuntimeException {
    
    public RefundProcessingException(String message) {
        super(message);
    }

    public RefundProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
