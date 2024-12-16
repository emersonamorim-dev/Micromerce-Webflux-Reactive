package com.webflux.micromerce.payment.application.dto.response;

import com.webflux.micromerce.payment.domain.model.PaymentMethod;
import com.webflux.micromerce.payment.domain.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record RefundResponse(
    UUID id,
    BigDecimal amount,
    LocalDateTime createdAt,
    PaymentStatus status,
    boolean success, 
    String transactionId, 
    String message
) implements PaymentResponse {

    // Construtor canônico com validações
    public RefundResponse {
        Objects.requireNonNull(id, "ID do reembolso não pode ser nulo");
        Objects.requireNonNull(amount, "Valor do reembolso não pode ser nulo");
        Objects.requireNonNull(createdAt, "Data de criação não pode ser nula");
        Objects.requireNonNull(status, "Status do pagamento não pode ser nulo");
        
        // Validações adicionais
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor do reembolso não pode ser negativo");
        }
    }

    public static RefundResponse successfulRefund(
        UUID paymentId, 
        BigDecimal amount, 
        String transactionId
    ) {
        return new RefundResponse(
            paymentId,
            amount,
            LocalDateTime.now(),
            PaymentStatus.CANCELLED,
            true,
            transactionId,
            "Reembolso processado com sucesso"
        );
    }

    public static RefundResponse failedRefund(
        UUID paymentId, 
        BigDecimal amount, 
        String reason
    ) {
        return new RefundResponse(
            paymentId,
            amount,
            LocalDateTime.now(),
            PaymentStatus.FAILED,
            false,
            null,
            reason
        );
    }

    public boolean isSuccessful() {
        return success;
    }

    public boolean isFailed() {
        return !success;
    }

    public Throwable failureReason() {
        return isFailed() ? new RuntimeException(message) : null;
    }
}
