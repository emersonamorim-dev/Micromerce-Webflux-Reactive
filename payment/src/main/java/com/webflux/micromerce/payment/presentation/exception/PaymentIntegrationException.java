package com.webflux.micromerce.payment.presentation.exception;

import org.springframework.http.HttpStatus;

public class PaymentIntegrationException extends PaymentException {
    
    public PaymentIntegrationException(String message, String detail, ErrorCode errorCode) {
        super(message, HttpStatus.BAD_GATEWAY, "Erro de Integração", detail, errorCode);
    }

    public PaymentIntegrationException(String message, String detail, ErrorCode errorCode, Throwable cause) {
        super(message, HttpStatus.BAD_GATEWAY, "Erro de Integração", detail, errorCode, cause);
    }
}
