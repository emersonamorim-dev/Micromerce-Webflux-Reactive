package com.webflux.micromerce.payment.application.usecase;

import com.webflux.micromerce.payment.application.dto.request.ProcessPaymentRequest;
import com.webflux.micromerce.payment.domain.model.*;
import com.webflux.micromerce.payment.infrastructure.gateway.PaymentGatewayService;
import com.webflux.micromerce.payment.infrastructure.repository.PaymentRepository;
import com.webflux.micromerce.payment.infrastructure.messaging.PaymentEventProducer;
import com.webflux.micromerce.payment.infrastructure.cache.PaymentCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Service
@Slf4j
public class ProcessPaymentUseCase {
    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer eventProducer;
    private final PaymentCacheService cacheService;
    private final PaymentGatewayService paymentGatewayService;

    public ProcessPaymentUseCase(
        PaymentRepository paymentRepository,
        PaymentEventProducer eventProducer,
        PaymentCacheService cacheService,
        PaymentGatewayService paymentGatewayService
    ) {
        this.paymentRepository = requireNonNull(paymentRepository);
        this.eventProducer = requireNonNull(eventProducer);
        this.cacheService = requireNonNull(cacheService);
        this.paymentGatewayService = requireNonNull(paymentGatewayService);
    }

    public Mono<PaymentMethod> execute(ProcessPaymentRequest request) {
        log.info("Iniciando processamento de pagamento: {}", request);
        
        return Mono.fromSupplier(() -> convertToDomain(request))
            .flatMap(this::validatePayment)
            .flatMap(this::processPaymentGateway)
            .flatMap(this::savePayment)
            .flatMap(this::publishPaymentEvent)
            .flatMap(this::cachePayment)
            .map(this::updatePaymentStatus)
            .doOnSuccess(payment -> log.info("Pagamento processado com sucesso: {}", payment.id()))
            .doOnError(error -> log.error("Erro no processamento de pagamento: {}", error.getMessage()));
    }

    private PaymentMethod convertToDomain(ProcessPaymentRequest request) {
        var now = LocalDateTime.now();
        return switch (request) {
            case ProcessPaymentRequest.CreditCardPaymentRequest r ->
                    new CreditCardPayment(
                            UUID.randomUUID(),
                            r.cardNumber(),
                            r.cardHolderName(),
                            r.cvv(),
                            r.amount(),
                            now,
                            PaymentStatus.PENDING,
                            r.customerId(),
                            r.orderId()
                    );
            case ProcessPaymentRequest.DebitCardPaymentRequest r ->
                    new DebitCardPayment(
                            UUID.randomUUID(),
                            r.cardNumber(),
                            r.cardHolderName(),
                            r.amount(),
                            now,
                            PaymentStatus.PENDING,
                            r.customerId(),
                            r.orderId()
                    );
            case ProcessPaymentRequest.BoletoPaymentRequest r ->
                    new BoletoPayment(
                            UUID.randomUUID(),
                            r.boletoNumber(),
                            r.amount(),
                            r.beneficiario(),
                            r.dueDate(),
                            now,
                            PaymentStatus.PENDING,
                            r.pagador(),
                            r.customerId(),
                            r.orderId()
                    );
            case ProcessPaymentRequest.PixPaymentRequest r ->
                    new PixPayment(
                            UUID.randomUUID(),
                            r.pixKey(),
                            r.pixKeyType(),
                            r.amount(),
                            now,
                            PaymentStatus.PENDING,
                            r.customerId(),
                            r.orderId()
                    );
        };
    }

    private Mono<PaymentMethod> validatePayment(PaymentMethod payment) {
        // Validação simples de valor
        if (payment.amount().doubleValue() <= 0) {
            return Mono.error(new RuntimeException("Valor do pagamento inválido"));
        }
        return Mono.just(payment);
    }

    private Mono<PaymentMethod> processPaymentGateway(PaymentMethod payment) {
        return paymentGatewayService.processPayment(payment);
    }

    private Mono<PaymentMethod> savePayment(PaymentMethod payment) {
        return paymentRepository.save(payment);
    }

    private Mono<PaymentMethod> publishPaymentEvent(PaymentMethod payment) {
        return eventProducer.publishPaymentEvent(payment)
                .thenReturn(payment);
    }

    private Mono<PaymentMethod> cachePayment(PaymentMethod payment) {
        return cacheService.cachePayment(payment)
                .thenReturn(payment);
    }

    private PaymentMethod updatePaymentStatus(PaymentMethod payment) {
        return switch (payment) {
            case CreditCardPayment p -> new CreditCardPayment(
                    p.id(),
                    p.getCardNumber(),
                    p.getCardHolderName(),
                    p.getCvv(),
                    p.amount(),
                    p.createdAt(),
                    PaymentStatus.PROCESSING,
                    p.customerId(),
                    p.orderId()
            );
            case DebitCardPayment p -> new DebitCardPayment(
                    p.id(),
                    p.getCardNumber(),
                    p.getCardHolderName(),
                    p.amount(),
                    p.createdAt(),
                    PaymentStatus.PROCESSING,
                    p.customerId(),
                    p.orderId()
            );
            case BoletoPayment p -> new BoletoPayment(
                    p.id(),
                    p.getBoletoNumber(),
                    p.amount(),
                    p.beneficiario(),
                    p.getDueDate(),
                    p.createdAt(),
                    PaymentStatus.PROCESSING,
                    p.pagador(),
                    p.customerId(),
                    p.orderId()
            );
            case PixPayment p -> new PixPayment(
                    p.id(),
                    p.getPixKey(),
                    p.getPixKeyType(),
                    p.amount(),
                    p.createdAt(),
                    PaymentStatus.PROCESSING,
                    p.customerId(),
                    p.orderId()
            );
        };
    }
}

