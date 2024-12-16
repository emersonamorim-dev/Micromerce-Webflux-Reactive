package com.webflux.micromerce.payment.application.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRefundRequest(
    PaymentRefundRequest.PaymentType type,
    String identifier,
    BigDecimal amount,
    UUID paymentId,
    String reason
) {
    public enum PaymentType {
        CREDIT_CARD,
        DEBIT_CARD,
        BOLETO,
        PIX
    }
}
