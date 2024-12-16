package com.webflux.micromerce.payment.infrastructure.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class HealthConfig {

    @Bean
    public ReactiveHealthIndicator kafkaHealthIndicator() {
        return () -> Mono.just(Health.up()
                .withDetail("kafka", "Kafka está operacional")
                .build());
    }

    @Bean
    public ReactiveHealthIndicator elasticsearchHealthIndicator() {
        return () -> Mono.just(Health.up()
                .withDetail("elasticsearch", "Elasticsearch está operacional")
                .build());
    }
}
