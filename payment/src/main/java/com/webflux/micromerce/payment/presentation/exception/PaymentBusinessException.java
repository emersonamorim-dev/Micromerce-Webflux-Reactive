package com.webflux.micromerce.payment.presentation.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class PaymentBusinessException extends PaymentException {

    @Serial
    private static final long serialVersionUID = 1L;

    public PaymentBusinessException(String message) {
        super(message, 
              HttpStatus.UNPROCESSABLE_ENTITY,
              "Erro de Negócio",
              message,
              ErrorCode.INTERNAL_SERVER_ERROR);
    }

    public PaymentBusinessException(String message, Throwable cause) {
        super(message,
              HttpStatus.UNPROCESSABLE_ENTITY,
              "Erro de Negócio",
              message,
              ErrorCode.INTERNAL_SERVER_ERROR,
              cause);
    }

    public PaymentBusinessException(String message, ErrorCode errorCode) {
        super(message,
              HttpStatus.UNPROCESSABLE_ENTITY,
              "Erro de Negócio",
              message,
              errorCode);
    }

    public PaymentBusinessException(String message, ErrorCode errorCode, Throwable cause) {
        super(message,
              HttpStatus.UNPROCESSABLE_ENTITY,
              "Erro de Negócio",
              message,
              errorCode,
              cause);
    }

    public PaymentBusinessException(String details, String title, ErrorCode errorCode) {
        super(details,
              HttpStatus.UNPROCESSABLE_ENTITY,
              title,
              details,
              errorCode);
    }

    public PaymentBusinessException(String details, String title, ErrorCode errorCode, Throwable cause) {
        super(details,
              HttpStatus.UNPROCESSABLE_ENTITY,
              title,
              details,
              errorCode,
              cause);
    }
}
