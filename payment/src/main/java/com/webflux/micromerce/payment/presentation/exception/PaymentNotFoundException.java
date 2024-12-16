package com.webflux.micromerce.payment.presentation.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class PaymentNotFoundException extends PaymentException {
    
    public PaymentNotFoundException(String message) {
        super(message,
              HttpStatus.NOT_FOUND,
              "Pagamento não encontrado",
              message,
              ErrorCode.PAYMENT_NOT_FOUND);
    }

    public PaymentNotFoundException(String message, String title, ErrorCode errorCode) {
        super(message,
              HttpStatus.NOT_FOUND,
              title,
              message,
              errorCode);
    }

    public PaymentNotFoundException(String message, Throwable cause) {
        super(message,
              HttpStatus.NOT_FOUND,
              "Pagamento não encontrado",
              message,
              ErrorCode.PAYMENT_NOT_FOUND,
              cause);
    }
}
