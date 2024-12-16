package com.webflux.micromerce.payment.infrastructure.client;

import com.webflux.micromerce.payment.application.dto.request.PaymentRefundRequest;
import com.webflux.micromerce.payment.application.dto.response.RefundResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class RefundProcessingClient {
    
    public Mono<RefundResponse> processCreditCardRefund(PaymentRefundRequest request) {
        return processRefund(request, "Reembolso de cartão de crédito");
    }

    public Mono<RefundResponse> processDebitCardRefund(PaymentRefundRequest request) {
        return processRefund(request, "Reembolso de cartão de débito");
    }

    public Mono<RefundResponse> processBoletoRefund(PaymentRefundRequest request) {
        return processRefund(request, "Reembolso de boleto");
    }

    public Mono<RefundResponse> processPixRefund(PaymentRefundRequest request) {
        return processRefund(request, "Reembolso PIX");
    }

    private Mono<RefundResponse> processRefund(PaymentRefundRequest request, String refundType) {
        return Mono.fromSupplier(() -> {
            // Simula processamento com possível falha
            if (Math.random() < 0.1) {
                throw new RuntimeException("Erro simulado no processamento de reembolso");
            }
            return RefundResponse.successfulRefund(
                request.paymentId(),
                request.amount(),
                "REFUND_" + UUID.randomUUID()
            );
        }).onErrorResume(ex -> 
            Mono.just(RefundResponse.failedRefund(
                request.paymentId(), 
                request.amount(), 
                refundType + " failed: " + ex.getMessage()
            ))
        );
    }
}
