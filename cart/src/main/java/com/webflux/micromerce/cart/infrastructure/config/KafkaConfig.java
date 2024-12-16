package com.webflux.micromerce.cart.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.webflux.micromerce.cart.application.dto.response.CartResponse;
import com.webflux.micromerce.cart.domain.event.CartCreatedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.sender.SenderOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.retries:3}")
    private Integer retries;

    @Value("${spring.kafka.producer.batch-size:16384}")
    private Integer batchSize;

    @Value("${spring.kafka.producer.buffer-memory:33554432}")
    private Integer bufferMemory;

    @Bean
    public ObjectMapper kafkaObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(JsonSerializer.TYPE_MAPPINGS, 
            "cartResponse:com.webflux.micromerce.cart.application.dto.response.CartResponse," +
            "cartCreatedEvent:com.webflux.micromerce.cart.domain.event.CartCreatedEvent");
        
        logger.info("Configurando Kafka producer com bootstrap servers: {}", bootstrapServers);
        return props;
    }

    @Bean(name = "cartResponseKafkaTemplate")
    public ReactiveKafkaProducerTemplate<String, CartResponse> cartResponseKafkaTemplate() {
        Map<String, Object> props = producerConfigs();
        
        JsonSerializer<CartResponse> valueSerializer = new JsonSerializer<>(kafkaObjectMapper());
        valueSerializer.setAddTypeInfo(false);
        
        SenderOptions<String, CartResponse> senderOptions = SenderOptions
            .<String, CartResponse>create(props)
            .withValueSerializer(valueSerializer);

        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }

    @Bean(name = "cartEventKafkaTemplate")
    public ReactiveKafkaProducerTemplate<String, CartCreatedEvent> cartEventKafkaTemplate() {
        Map<String, Object> props = producerConfigs();
        
        JsonSerializer<CartCreatedEvent> valueSerializer = new JsonSerializer<>(kafkaObjectMapper());
        valueSerializer.setAddTypeInfo(false);
        
        SenderOptions<String, CartCreatedEvent> senderOptions = SenderOptions
            .<String, CartCreatedEvent>create(props)
            .withValueSerializer(valueSerializer);

        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }
}
