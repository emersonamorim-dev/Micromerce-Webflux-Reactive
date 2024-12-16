package com.webflux.micromerce.payment.presentation.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class PaymentValidationException extends PaymentException {

    public PaymentValidationException(String message) {
        super(message,
              HttpStatus.BAD_REQUEST,
              "Erro de Validação",
              message,
              ErrorCode.VALIDATION_ERROR);
    }

    public PaymentValidationException(String message, Throwable cause) {
        super(message,
              HttpStatus.BAD_REQUEST,
              "Erro de Validação",
              message,
              ErrorCode.VALIDATION_ERROR,
              cause);
    }

    public PaymentValidationException(String message, String details, ErrorCode errorCode) {
        super(message,
              HttpStatus.BAD_REQUEST,
              message,
              details,
              errorCode);
    }

    public PaymentValidationException(String message, String details, ErrorCode errorCode, Throwable cause) {
        super(message,
              HttpStatus.BAD_REQUEST,
              message,
              details,
              errorCode,
              cause);
    }
}