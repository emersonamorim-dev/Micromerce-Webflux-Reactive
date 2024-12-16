package com.webflux.micromerce.payment.infrastructure.messaging;

import com.webflux.micromerce.payment.domain.model.PaymentMethod;
import com.webflux.micromerce.payment.domain.model.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;
import reactor.util.retry.Retry;

import java.time.Duration;

public class PaymentEventProducer {
    private static final Logger log = LoggerFactory.getLogger(PaymentEventProducer.class);
    private final ReactiveKafkaProducerTemplate<String, PaymentMethod> kafkaTemplate;

    @Value("${spring.kafka.topic.payment}")
    private String paymentTopic;

    @Value("${spring.kafka.topic.payment.retry-attempts:3}")
    private int retryAttempts;

    @Value("${spring.kafka.topic.payment.retry-delay:1000}")
    private long retryDelayMillis;

    public PaymentEventProducer(ReactiveKafkaProducerTemplate<String, PaymentMethod> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public Mono<Void> publishPaymentEvent(PaymentMethod payment) {
        String topic = determineTopicByStatus(payment.status());
        String key = generateEventKey(payment);

        return Mono.defer(() -> {
                    log.info("Tentando publicar evento de pagamento - ID: {}, Status: {}, Topic: {}",
                            payment.id(), payment.status(), topic);

                    return kafkaTemplate.send(topic, key, payment)
                            .doOnSuccess(result -> logSuccessfulEvent(payment, topic))
                            .doOnError(error -> logFailedEvent(payment, topic, error))
                            .retryWhen(Retry.backoff(retryAttempts, Duration.ofMillis(retryDelayMillis))
                                    .filter(throwable -> {
                                        if (throwable instanceof IllegalStateException
                                                && throwable.getMessage().contains("RecordHeaders foi fechado")) {
                                            log.warn("Tentando novamente com novos cabeçalhos para pagamento ID: {}", payment.id());
                                            return true;
                                        }
                                        return !(throwable instanceof IllegalArgumentException);
                                    })
                                    .doBeforeRetry(signal -> log.warn("Tentando novamente a publicação do evento de pagamento para ID: {}, attempt: {}, error: {}",
                                            payment.id(), signal.totalRetries() + 1,
                                            signal.failure().getMessage())))
                            .onErrorResume(error -> {
                                if (error instanceof IllegalStateException
                                        && error.getMessage().contains("RecordHeaders foi fechado")) {
                                    log.warn("Cabeçalhos fechados, criando novo registro de produtor para pagamento ID: {}", payment.id());
                                    return kafkaTemplate.send(topic, key, payment);
                                }
                                return Mono.error(error);
                            });
                })
                .then();
    }

    private String determineTopicByStatus(PaymentStatus status) {
        return switch (status) {
            case PENDING -> paymentTopic + "-pending";
            case PROCESSING -> paymentTopic + "-processing";
            case COMPLETED -> paymentTopic + "-completed";
            case FAILED -> paymentTopic + "-failed";
            case CANCELLED -> paymentTopic + "-cancelled";
            case REFUNDED -> paymentTopic + "-refunded";
        };
    }

    private String generateEventKey(PaymentMethod payment) {
        return String.format("%s-%s-%s",
                payment.id(),
                payment.customerId(),
                payment.orderId()
        );
    }

    private void logSuccessfulEvent(PaymentMethod payment, String topic) {
        log.info("Evento de pagamento publicado com sucesso - ID: {}, Status: {}, Topic: {}",
                payment.id(),
                payment.status(),
                topic
        );
    }

    private void logFailedEvent(PaymentMethod payment, String topic, Throwable error) {
        log.error("Falha ao publicar evento de pagamento - ID: {}, Status: {}, Topic: {}, Error: {}",
                payment.id(),
                payment.status(),
                topic,
                error.getMessage()
        );
    }
}
