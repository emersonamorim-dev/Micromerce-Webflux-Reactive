package com.webflux.micromerce.cart.infrastructure.cache;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RedisMetricsScheduler {

    private final RedisMetricsService redisMetricsService;

    public RedisMetricsScheduler(RedisMetricsService redisMetricsService) {
        this.redisMetricsService = redisMetricsService;
    }

    @Scheduled(fixedRate = 60000)  // Executa a cada 60 segundos
    public void scheduleMetricsMonitoring() {
        redisMetricsService.monitorRedisMetrics().subscribe();
    }
}
