package com.webflux.micromerce.payment.infrastructure.config;

import com.webflux.micromerce.payment.domain.model.PaymentMethod;
import com.webflux.micromerce.payment.infrastructure.cache.PaymentCacheService;
import com.webflux.micromerce.payment.infrastructure.messaging.PaymentEventProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.webflux.micromerce.payment.infrastructure.repository")
public class PaymentInfrastructureConfig {

    @Bean
    public PaymentCacheService paymentCacheService(
            ReactiveRedisTemplate<String, PaymentMethod> redisTemplate) {
        return new PaymentCacheService(redisTemplate);
    }

    @Bean
    public PaymentEventProducer paymentEventProducer(
            ReactiveKafkaProducerTemplate<String, PaymentMethod> kafkaTemplate) {
        return new PaymentEventProducer(kafkaTemplate);
    }
}
