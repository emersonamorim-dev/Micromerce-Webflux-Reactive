package com.webflux.micromerce.payment.application.usecase;

import com.webflux.micromerce.payment.domain.model.*;
import com.webflux.micromerce.payment.infrastructure.repository.PaymentRepository;
import com.webflux.micromerce.payment.infrastructure.messaging.PaymentEventProducer;
import com.webflux.micromerce.payment.presentation.exception.PaymentNotFoundException;
import com.webflux.micromerce.payment.presentation.exception.PaymentProcessingException;
import com.webflux.micromerce.payment.presentation.exception.PaymentValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
public class RefundPaymentUseCase {
    private final PaymentRepository paymentRepository;

    @Value("${payment.retry.max-attempts:3}")
    private int maxRetryAttempts;

    @Value("${payment.retry.initial-delay:100}")
    private long initialRetryDelay;

    public RefundPaymentUseCase(
            PaymentRepository paymentRepository,
            PaymentEventProducer eventProducer
    ) {
        this.paymentRepository = paymentRepository;
    }

    public Mono<PaymentMethodList> execute(UUID paymentId) {
        log.info("Iniciando processo de reembolso para pagamento: {}. Verificando status...", paymentId);
        return findPayment(paymentId)
                .doOnNext(payment -> log.info("Status atual do pagamento {}: {}", paymentId, payment.getStatus()))
                .flatMap(this::validateRefund)
                .flatMap(this::updatePaymentStatus)
                .doOnSuccess(payment -> log.info("Reembolso processado com sucesso para pagamento: {}", payment.getId()))
                .doOnError(error -> log.error("Erro no reembolso do pagamento {}: {}", paymentId, error.getMessage()))
                .retryWhen(Retry.backoff(maxRetryAttempts, Duration.ofMillis(initialRetryDelay))
                        .filter(throwable -> !(throwable instanceof PaymentValidationException ||
                                throwable instanceof PaymentNotFoundException))
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new PaymentProcessingException("Falha ao processar reembolso após " +
                                        maxRetryAttempts + " tentativas. ID: " + paymentId,
                                        retrySignal.failure())));
    }

    private Mono<PaymentMethodList> findPayment(UUID paymentId) {
        return paymentRepository.findByIdRefund(paymentId)
                .doOnSubscribe(s -> log.debug("Buscando pagamento para reembolso: {}", paymentId))
                .switchIfEmpty(Mono.error(new PaymentNotFoundException(
                        "Pagamento não encontrado ou não elegível para reembolso: " + paymentId)));
    }

    private Mono<PaymentMethodList> validateRefund(PaymentMethodList payment) {
        return switch (payment.getStatus()) {
            case PROCESSING, COMPLETED -> {
                log.info("Pagamento {} elegível para reembolso. Status atual: {}",
                        payment.getId(), payment.getStatus());
                yield Mono.just(payment);
            }
            case CANCELLED -> Mono.error(new PaymentValidationException(
                    "Este pagamento já foi cancelado. ID: " + payment.getId()));
            case REFUNDED -> Mono.error(new PaymentValidationException(
                    "Este pagamento já foi reembolsado. ID: " + payment.getId()));
            default -> Mono.error(new PaymentValidationException(
                    "Status inválido para reembolso: " + payment.getStatus() + ". ID: " + payment.getId()));
        };
    }

    private Mono<PaymentMethodList> updatePaymentStatus(PaymentMethodList payment) {
        payment.setStatus(PaymentStatus.REFUNDED);
        return Mono.just(payment);
    }

}