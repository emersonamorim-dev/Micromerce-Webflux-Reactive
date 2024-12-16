package com.webflux.micromerce.payment.infrastructure.gateway;

import com.webflux.micromerce.payment.application.dto.request.PaymentRefundRequest;
import com.webflux.micromerce.payment.application.dto.response.RefundResponse;
import com.webflux.micromerce.payment.domain.model.*;
import com.webflux.micromerce.payment.presentation.exception.PaymentCancellationException;
import com.webflux.micromerce.payment.presentation.exception.PaymentProcessingException;
import com.webflux.micromerce.payment.presentation.exception.PaymentValidationException;
import com.webflux.micromerce.payment.infrastructure.service.PaymentValidationService;
import com.webflux.micromerce.payment.infrastructure.client.RefundProcessingClient;
import com.webflux.micromerce.payment.presentation.exception.RefundProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Supplier;

import static reactor.util.retry.Retry.backoff;

public interface PaymentGatewayService {
    Mono<RefundResponse> refundPayment(PaymentRefundRequest request);
    Mono<PaymentMethod> cancelPayment(PaymentMethod payment);
    Mono<PaymentMethod> processPayment(PaymentMethod payment);

    @Service
    @RequiredArgsConstructor
    @Slf4j
    class PaymentGatewayServiceImpl implements PaymentGatewayService {
        private final PaymentValidationService validationService;
        private final RefundProcessingClient refundClient;


        @Value("${payment.gateway.timeout:10}")
        private int paymentTimeoutSeconds;

        @Value("${payment.gateway.retry.max-attempts:3}")
        private int maxRetryAttempts;

        @Value("${payment.gateway.retry.delay:100}")
        private long retryDelayMillis;

        @Override
        public Mono<RefundResponse> refundPayment(PaymentRefundRequest request) {
            return validationService.validate(request)
                    .flatMap(this::executeRefund)
                    .doOnSuccess(response -> log.info("Reembolso processado com sucesso: {}", response))
                    .doOnError(ex -> log.error("Erro de processamento de reembolso para pagamento {}: {}",
                            request.paymentId(), ex.getMessage()))
                    .retryWhen(backoff(maxRetryAttempts, Duration.ofMillis(retryDelayMillis))
                            .filter(this::isRetryableException));
        }

        @Override
        public Mono<PaymentMethod> cancelPayment(PaymentMethod payment) {
            log.info("Iniciando o cancelamento do pagamento para ID: {}", payment.id());

            return Mono.fromSupplier(() -> cancelPaymentInternal(payment))
                    .publishOn(Schedulers.boundedElastic())
                    .timeout(Duration.ofSeconds(paymentTimeoutSeconds))
                    .doOnSuccess(p -> log.info("Pagamento cancelado com sucesso: {}", p.id()))
                    .doOnError(ex -> log.error("O cancelamento do pagamento falhou para ID {}: {}",
                            payment.id(), ex.getMessage()))
                    .retryWhen(backoff(maxRetryAttempts, Duration.ofMillis(retryDelayMillis))
                            .filter(this::isRetryableException));
        }

        @Override
        public Mono<PaymentMethod> processPayment(PaymentMethod payment) {
            log.info("Processando pagamento: {}", payment.id());

            return validatePayment(payment)
                    .flatMap(this::processPaymentWithGateway)
                    .publishOn(Schedulers.boundedElastic())
                    .timeout(Duration.ofSeconds(paymentTimeoutSeconds))
                    .doOnSuccess(p -> log.info("Pagamento processado com sucesso: {}", p.id()))
                    .doOnError(ex -> log.error("O processamento do pagamento falhou para ID {}: {}",
                            payment.id(), ex.getMessage()))
                    .retryWhen(backoff(maxRetryAttempts, Duration.ofMillis(retryDelayMillis))
                            .filter(this::isRetryableException));
        }

        private Mono<PaymentMethod> validatePayment(PaymentMethod payment) {
            return Mono.fromSupplier(() -> {
                switch (payment) {
                    case CreditCardPayment p -> validateCreditCardPayment(p);
                    case DebitCardPayment p -> validateDebitCardPayment(p);
                    case BoletoPayment p -> validateBoletoPayment(p);
                    case PixPayment p -> validatePixPayment(p);
                }
                return payment;
            });
        }

        private Mono<PaymentMethod> processPaymentWithGateway(PaymentMethod payment) {
            return Mono.fromSupplier(() -> {
                try {
                    Thread.sleep(100); // Simula latência de processamento
                    return switch (payment) {
                        case CreditCardPayment p -> processCreditCardPayment(p);
                        case DebitCardPayment p -> processDebitCardPayment(p);
                        case BoletoPayment p -> processBoletoPayment(p);
                        case PixPayment p -> processPixPayment(p);
                    };
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new PaymentProcessingException("Processamento de pagamento interrompido", e);
                }
            });
        }



        private PaymentMethod cancelPaymentInternal(PaymentMethod payment) {
            try {
                Thread.sleep(100); // Simula latência de processamento
                return switch (payment) {
                    case CreditCardPayment p -> new CreditCardPayment(
                            p.id(),
                            p.getCardNumber(),
                            p.getCardHolderName(),
                            p.getCvv(),
                            p.amount(),
                            p.createdAt(),
                            PaymentStatus.CANCELLED,
                            p.customerId(),
                            p.orderId()
                    );
                    case DebitCardPayment p -> new DebitCardPayment(
                            p.id(),
                            p.getCardNumber(),
                            p.getCardHolderName(),
                            p.amount(),
                            p.createdAt(),
                            PaymentStatus.CANCELLED,
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
                            PaymentStatus.CANCELLED,
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
                            PaymentStatus.CANCELLED,
                            p.customerId(),
                            p.orderId()
                    );
                };
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new PaymentCancellationException("Cancelamento de pagamento interrompido", e);
            }
        }

        private boolean isRetryableException(Throwable throwable) {
            return throwable instanceof RuntimeException &&
                    !(throwable instanceof PaymentValidationException);
        }

        private void validateCreditCardPayment(CreditCardPayment payment) {
            if (payment.getCardNumber() == null || payment.getCardNumber().length() < 16) {
                throw new PaymentValidationException("Número de cartão de crédito inválido");
            }
            if (payment.getCvv() == null || payment.getCvv().length() != 3) {
                throw new PaymentValidationException("Inválido CVV");
            }
            if (payment.getCardHolderName() == null || payment.getCardHolderName().trim().isEmpty()) {
                throw new PaymentValidationException("Nome do titular do cartão inválido");
            }
            validateCommonFields(payment);
        }

        private void validateDebitCardPayment(DebitCardPayment payment) {
            if (payment.getCardNumber() == null || payment.getCardNumber().length() < 16) {
                throw new PaymentValidationException("Número de cartão de débito inválido");
            }
            if (payment.getCardHolderName() == null || payment.getCardHolderName().trim().isEmpty()) {
                throw new PaymentValidationException("Nome do titular do cartão inválido");
            }
            validateCommonFields(payment);
        }

        private void validateBoletoPayment(BoletoPayment payment) {
            if (payment.getBoletoNumber() == null || payment.getBoletoNumber().length() < 10) {
                throw new PaymentValidationException("Número de boleto inválido");
            }
            if (payment.getDueDate() == null || payment.getDueDate().isBefore(LocalDateTime.now())) {
                throw new PaymentValidationException("Data de vencimento inválida");
            }
            validateCommonFields(payment);
        }

        private void validatePixPayment(PixPayment payment) {
            if (payment.getPixKey() == null || payment.getPixKey().trim().isEmpty()) {
                throw new PaymentValidationException("Chave PIX inválida");
            }
            validateCommonFields(payment);
        }

        private void validateCommonFields(PaymentMethod payment) {
            if (payment.amount() == null || payment.amount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new PaymentValidationException("Valor de pagamento inválido");
            }
            if (payment.customerId() == null) {
                throw new PaymentValidationException("O ID do cliente é obrigatório");
            }
            if (payment.orderId() == null || payment.orderId().trim().isEmpty()) {
                throw new PaymentValidationException("O ID do pedido é obrigatório");
            }
        }

        private PaymentMethod processCreditCardPayment(CreditCardPayment payment) {
            return new CreditCardPayment(
                    payment.id(),
                    payment.getCardNumber(),
                    payment.getCardHolderName(),
                    payment.getCvv(),
                    payment.amount(),
                    payment.createdAt(),
                    simulatePaymentProcessing(),
                    payment.customerId(),
                    payment.orderId()
            );
        }

        private PaymentMethod processDebitCardPayment(DebitCardPayment payment) {
            return new DebitCardPayment(
                    payment.id(),
                    payment.getCardNumber(),
                    payment.getCardHolderName(),
                    payment.amount(),
                    payment.createdAt(),
                    simulatePaymentProcessing(),
                    payment.customerId(),
                    payment.orderId()
            );
        }

        private PaymentMethod processBoletoPayment(BoletoPayment payment) {
            return new BoletoPayment(
                    payment.id(),
                    payment.getBoletoNumber(),
                    payment.amount(),
                    payment.beneficiario(),
                    payment.getDueDate(),
                    payment.createdAt(),
                    simulatePaymentProcessing(),
                    payment.pagador(),
                    payment.customerId(),
                    payment.orderId()
            );
        }

        private PaymentMethod processPixPayment(PixPayment payment) {
            return new PixPayment(
                    payment.id(),
                    payment.getPixKey(),
                    payment.getPixKeyType(),
                    payment.amount(),
                    payment.createdAt(),
                    simulatePaymentProcessing(),
                    payment.customerId(),
                    payment.orderId()
            );
        }

        private PaymentStatus simulatePaymentProcessing() {
            double random = Math.random();
            if (random < 0.8) {
                return PaymentStatus.PROCESSING;
            } else if (random < 0.9) {
                throw new PaymentProcessingException("O processamento do pagamento falhou", new RuntimeException("Erro de gateway"));
            } else {
                try {
                    Thread.sleep(paymentTimeoutSeconds * 2000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new PaymentProcessingException("Processamento de pagamento interrompido", e);
                }
                return PaymentStatus.FAILED;
            }
        }

        private Mono<RefundResponse> executeRefund(PaymentRefundRequest request) {
            return Mono.defer(() -> {
                        log.info("Processando solicitação de reembolso para pagamento ID: {}", request.paymentId());

                        return switch (request.type()) {
                            case CREDIT_CARD -> processRefundWithErrorHandling(
                                    () -> refundClient.processCreditCardRefund(request),
                                    "Erro no processamento de reembolso de cartão de crédito"
                            );
                            case DEBIT_CARD -> processRefundWithErrorHandling(
                                    () -> refundClient.processDebitCardRefund(request),
                                    "Erro no processamento de reembolso de cartão de débito"
                            );
                            case BOLETO -> processRefundWithErrorHandling(
                                    () -> refundClient.processBoletoRefund(request),
                                    "Erro no processamento do reembolso do boleto"
                            );
                            case PIX -> processRefundWithErrorHandling(
                                    () -> refundClient.processPixRefund(request),
                                    "Erro no processamento de reembolso do PIX"
                            );
                        };
                    });
        }

        private Mono<RefundResponse> processRefundWithErrorHandling(
                Supplier<Mono<RefundResponse>> refundProcessor,
                String errorMessage
        ) {
            return refundProcessor.get()
                    .onErrorResume(ex -> {
                        log.error("{}: {}", errorMessage, ex.getMessage());
                        return Mono.error(new RefundProcessingException(errorMessage, ex));
                    });
        }
    }
}

