package com.webflux.micromerce.catalog.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webflux.micromerce.catalog.application.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaAdmin kafkaAdmin;
    private final ObjectMapper objectMapper;
    
    @Value("${spring.kafka.topics.product-events}")
    private String topicName;

    private void ensureTopicExists() {
        try {
            NewTopic newTopic = TopicBuilder.name(topicName)
                    .partitions(1)
                    .replicas(1)
                    .build();
            
            kafkaAdmin.createOrModifyTopics(newTopic);
            log.info("Topic '{}' created or verified", topicName);
        } catch (Exception e) {
            log.error("Error creating topic '{}': {}", topicName, e.getMessage());
            throw new RuntimeException("Failed to create Kafka topic", e);
        }
    }

    public Mono<Void> sendProductEvent(ProductResponse product) {
        try {
            String key = String.valueOf(product.getId());
            String message = objectMapper.writeValueAsString(product);

            return Mono.fromRunnable(this::ensureTopicExists)
                    .then(Mono.fromFuture(() -> kafkaTemplate.send(topicName, key, message).toCompletableFuture()))
                    .doFirst(() -> log.debug("Sending product event to topic {}: {}", topicName, product))
                    .doOnSuccess(result -> log.debug("Product event sent successfully to topic {} - Offset: {}", 
                            topicName, result.getRecordMetadata().offset()))
                    .doOnError(error -> log.error("Failed to send product event to topic {}: {}", 
                            topicName, error.getMessage()))
                    .then();
        } catch (JsonProcessingException e) {
            log.error("Error serializing product event: {}", e.getMessage());
            return Mono.error(e);
        }
    }
}
