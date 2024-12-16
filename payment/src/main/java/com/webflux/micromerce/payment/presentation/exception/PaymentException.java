package com.webflux.micromerce.payment.presentation.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {
    private final HttpStatus status;
    private final String title;
    private final String details;
    private final ErrorCode errorCode;

    public PaymentException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.title = "Erro no Processamento";
        this.details = message;
        this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.title = "Erro no Processamento";
        this.details = message;
        this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    }

    public PaymentException(String message, HttpStatus status, String title, String details, ErrorCode errorCode) {
        super(message);
        this.status = status;
        this.title = title;
        this.details = details;
        this.errorCode = errorCode;
    }

    public PaymentException(String message, HttpStatus status, String title, String details, ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.title = title;
        this.details = details;
        this.errorCode = errorCode;
    }
}
