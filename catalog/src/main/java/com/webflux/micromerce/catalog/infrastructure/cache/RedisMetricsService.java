package com.webflux.micromerce.catalog.infrastructure.cache;

import com.webflux.micromerce.catalog.infrastructure.monitoring.KibanaMetricsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;
import java.util.Properties;

@Service
public class RedisMetricsService {
    private static final Logger logger = LoggerFactory.getLogger(RedisMetricsService.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    
    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final KibanaMetricsSender kibanaMetricsSender;
    private final ReactiveRedisConnection redisConnection;

    public RedisMetricsService(Optional<ReactiveRedisTemplate<String, Object>> redisTemplate, KibanaMetricsSender kibanaMetricsSender) {
        this.redisTemplate = redisTemplate.orElse(null);
        this.kibanaMetricsSender = kibanaMetricsSender;
        this.redisConnection = this.redisTemplate != null ? this.redisTemplate.getConnectionFactory().getReactiveConnection() : null;
        
        if (this.redisTemplate == null) {
            logger.warn("Redis template is not available. Metrics will not be recorded.");
        }
    }

    public Mono<Void> recordMetric(String key, Object value) {
        if (redisTemplate == null) {
            logger.debug("Skipping metric recording as Redis is not available: key={}", key);
            return Mono.empty();
        }

        return redisTemplate.opsForValue()
            .set(key, value)
            .timeout(TIMEOUT)
            .doOnSuccess(success -> logger.debug("Successfully recorded metric: key={}", key))
            .onErrorResume(error -> {
                logger.error("Failed to record metric: key={}, error={}", key, error.getMessage());
                return Mono.empty();
            })
            .then();
    }

    public Mono<Object> getMetric(String key) {
        if (redisTemplate == null) {
            logger.debug("Skipping metric retrieval as Redis is not available: key={}", key);
            return Mono.empty();
        }

        return redisTemplate.opsForValue()
            .get(key)
            .timeout(TIMEOUT)
            .doOnSuccess(value -> logger.debug("Successfully retrieved metric: key={}", key))
            .onErrorResume(error -> {
                logger.error("Failed to retrieve metric: key={}, error={}", key, error.getMessage());
                return Mono.empty();
            });
    }

    public Mono<Void> monitorRedisMetrics() {
        if (redisConnection == null) {
            logger.debug("Skipping Redis monitoring as connection is not available");
            return Mono.empty();
        }

        return redisConnection.serverCommands().info()
            .map(this::parseRedisInfo)
            .timeout(TIMEOUT)
            .doOnNext(metrics -> kibanaMetricsSender.sendMetricsToKibana("Redis", metrics))
            .onErrorResume(error -> {
                logger.error("Failed to monitor Redis metrics: error={}", error.getMessage());
                return Mono.empty();
            })
            .then();
    }

    private String parseRedisInfo(Properties info) {
        StringBuilder metrics = new StringBuilder();
        if (info.containsKey("used_memory")) {
            metrics.append("Memory: ").append(info.getProperty("used_memory")).append(" bytes");
        }
        if (info.containsKey("connected_clients")) {
            metrics.append(", Clients: ").append(info.getProperty("connected_clients"));
        }
        if (info.containsKey("total_commands_processed")) {
            metrics.append(", Commands: ").append(info.getProperty("total_commands_processed"));
        }
        return metrics.toString();
    }
}
