package com.webflux.micromerce.cart.infrastructure.monitoring;

import com.webflux.micromerce.cart.infrastructure.cache.RedisMetricsService;
import com.webflux.micromerce.cart.infrastructure.config.MongoMetricsService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class KibanaMetrics {
    private final MeterRegistry meterRegistry;
    private final WebClient webClient;
    private final RedisMetricsService redisMetricsService;
    private final MongoMetricsService mongoMetricsService;
    private final KibanaMetricsSender kibanaMetricsSender;
    private static final String SERVICE_NAME = "cart-service";
    
    private final KibanaMetricsData metricsData = new KibanaMetricsData();

    public <T> Mono<T> recordOperation(String operationName, Supplier<Mono<T>> operation) {
        Timer timer = Timer.builder("cart.operation")
            .tag("operation", operationName)
            .tag("service", SERVICE_NAME)
            .register(meterRegistry);

        return Mono.just(System.nanoTime())
            .flatMap(startTime -> 
                timer.record(operation)
                    .doOnSuccess(result -> {
                        long duration = (System.nanoTime() - startTime) / 1_000_000; // Convert to milliseconds
                        metricsData.recordOperation(duration);
                        
                        switch (operationName) {
                            case "create_cart":
                                metricsData.recordCartCreated();
                                break;
                            case "checkout_cart":
                                metricsData.recordCartCompleted();
                                metricsData.recordCheckout(duration);
                                break;
                            case "abandon_cart":
                                metricsData.recordCartAbandoned();
                                break;
                            case "add_item":
                                metricsData.recordItemAdded(duration);
                                break;
                        }
                    })
                    .doOnError(error -> metricsData.recordError())
            );
    }

    public KibanaMetricsData getCartMetrics() {
        return metricsData;
    }

    public Mono<Void> sendMetricsToKibana(KibanaMetricsData metrics) {
        return webClient.post()
            .uri("/api/metrics")
            .bodyValue(metrics)
            .retrieve()
            .bodyToMono(Void.class)
            .doOnSuccess(response -> log.debug("Métricas enviadas com sucesso para Kibana"))
            .doOnError(error -> log.error("Erro ao enviar métricas para o Kibana: {}", error.getMessage()));
    }

    public Mono<Void> monitorMetrics() {
        return Mono.when(
                redisMetricsService.monitorRedisMetrics(),
                mongoMetricsService.monitorMongoMetrics()
        ).then();
    }

    public Mono<Void> sendCustomMetric(String service, String metric) {
        return kibanaMetricsSender.sendMetricsToKibana(service, metric);
    }
}
