package com.webflux.micromerce.payment.infrastructure.service;

import com.webflux.micromerce.payment.application.dto.request.PaymentRefundRequest;
import com.webflux.micromerce.payment.presentation.exception.PaymentValidationException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class PaymentValidationService {
    
    public Mono<PaymentRefundRequest> validate(PaymentRefundRequest request) {
        return Mono.fromSupplier(() -> {
            validateAmount(request.amount());
            validatePaymentType(request.type());
            validateIdentifier(request);
            return request;
        });
    }

    private void validateAmount(BigDecimal amount) {
        Optional.ofNullable(amount)
            .filter(amt -> amt.compareTo(BigDecimal.ZERO) > 0)
            .orElseThrow(() -> new PaymentValidationException("Valor de reembolso inválido"));
    }

    private void validatePaymentType(PaymentRefundRequest.PaymentType type) {
        Optional.ofNullable(type)
            .orElseThrow(() -> new PaymentValidationException("Tipo de pagamento não especificado"));
    }

    private void validateIdentifier(PaymentRefundRequest request) {
        switch (request.type()) {
            case CREDIT_CARD, DEBIT_CARD -> validateCardIdentifier(request.identifier());
            case BOLETO -> validateBoletoIdentifier(request.identifier());
            case PIX -> validatePixIdentifier(request.identifier());
        }
    }

    private void validateCardIdentifier(String identifier) {
        Optional.ofNullable(identifier)
            .filter(id -> id.length() >= 16)
            .orElseThrow(() -> new PaymentValidationException("Identificador de cartão inválido"));
    }

    private void validateBoletoIdentifier(String identifier) {
        Optional.ofNullable(identifier)
            .filter(id -> id.length() >= 10)
            .orElseThrow(() -> new PaymentValidationException("Identificador de boleto inválido"));
    }

    private void validatePixIdentifier(String identifier) {
        Optional.ofNullable(identifier)
            .filter(id -> id.length() >= 32)
            .orElseThrow(() -> new PaymentValidationException("Identificador PIX inválido"));
    }
}
