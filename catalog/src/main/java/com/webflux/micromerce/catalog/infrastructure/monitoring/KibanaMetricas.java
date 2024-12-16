package com.webflux.micromerce.catalog.infrastructure.monitoring;

import com.webflux.micromerce.catalog.infrastructure.cache.RedisMetricsService;
import com.webflux.micromerce.catalog.infrastructure.config.PostgresMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class KibanaMetricas {

    private final RedisMetricsService redisMetricsService;
    private final PostgresMetricsService postgresMetricsService;
    private final KibanaMetricsSender kibanaMetricsSender;

    @Autowired
    public KibanaMetricas(RedisMetricsService redisMetricsService,
                         @Lazy PostgresMetricsService postgresMetricsService,
                         KibanaMetricsSender kibanaMetricsSender) {
        this.redisMetricsService = redisMetricsService;
        this.postgresMetricsService = postgresMetricsService;
        this.kibanaMetricsSender = kibanaMetricsSender;
    }

    public Mono<Void> monitorMetrics() {
        return Mono.when(
                redisMetricsService.monitorRedisMetrics(),
                postgresMetricsService.monitorPostgresMetrics()
        ).doOnSuccess(aVoid -> {
            kibanaMetricsSender.sendMetricsToKibana("Redis", "some-redis-metrics");
            kibanaMetricsSender.sendMetricsToKibana("Postgres", "some-postgres-metrics");
        });
    }
}
