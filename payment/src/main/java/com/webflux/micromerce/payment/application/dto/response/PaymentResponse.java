package com.webflux.micromerce.payment.application.dto.response;

import com.webflux.micromerce.payment.domain.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public sealed interface PaymentResponse permits PaymentResponse.BoletoPaymentResponse, PaymentResponse.CreditCardPaymentResponse, PaymentResponse.DebitCardPaymentResponse, PaymentResponse.PixPaymentResponse, RefundResponse {
    UUID id();
    BigDecimal amount();
    LocalDateTime createdAt();
    PaymentStatus status();

    record CreditCardPaymentResponse(
            UUID id,
            String cardNumber,
            String cardHolderName,
            BigDecimal amount,
            LocalDateTime createdAt,
            PaymentStatus status
    ) implements PaymentResponse {}

    record DebitCardPaymentResponse(
            UUID id,
            String cardNumber,
            String cardHolderName,
            BigDecimal amount,
            LocalDateTime createdAt,
            PaymentStatus status
    ) implements PaymentResponse {}

    record BoletoPaymentResponse(
            UUID id,
            String boletoNumber,
            BigDecimal amount,
            String beneficiario,
            LocalDateTime dueDate,
            LocalDateTime createdAt,
            PaymentStatus status,
            String pagador
    ) implements PaymentResponse {}

    record PixPaymentResponse(
            UUID id,
            String pixKey,
            String pixKeyType,
            BigDecimal amount,
            LocalDateTime createdAt,
            PaymentStatus status
    ) implements PaymentResponse {}

    static PaymentResponse fromPaymentMethod(PaymentMethod paymentMethod) {
        return switch (paymentMethod) {
            case CreditCardPayment p -> new CreditCardPaymentResponse(
                    p.id(),
                    p.getCardNumber(),
                    p.getCardHolderName(),
                    p.amount(),
                    p.createdAt(),
                    p.status()
            );
            case DebitCardPayment p -> new DebitCardPaymentResponse(
                    p.id(),
                    p.getCardNumber(),
                    p.getCardHolderName(),
                    p.amount(),
                    p.createdAt(),
                    p.status()
            );
            case BoletoPayment p -> new BoletoPaymentResponse(
                    p.id(),
                    p.getBoletoNumber(),
                    p.amount(),
                    p.beneficiario(),
                    p.getDueDate(),
                    p.createdAt(),
                    p.status(),
                    p.pagador()
            );
            case PixPayment p -> new PixPaymentResponse(
                    p.id(),
                    p.getPixKey(),
                    p.getPixKeyType(),
                    p.amount(),
                    p.createdAt(),
                    p.status()
            );
        };
    }
}
