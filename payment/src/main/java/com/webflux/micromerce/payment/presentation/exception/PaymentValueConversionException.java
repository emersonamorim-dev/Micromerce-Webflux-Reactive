package com.webflux.micromerce.payment.presentation.exception;

public class PaymentValueConversionException extends PaymentPresentationException {
    
    public PaymentValueConversionException(String message) {
        super(message);
    }

    public PaymentValueConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
