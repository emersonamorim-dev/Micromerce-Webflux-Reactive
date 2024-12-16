package com.webflux.micromerce.catalog.infrastructure.monitoring;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class KibanaMetricsSender {

    private final String kibanaUrl = "http://localhost:9200/metrics/_doc";

    private final WebClient webClient;

    public KibanaMetricsSender(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(kibanaUrl).build();
    }

    public Mono<Void> sendMetricsToKibana(String source, String metrics) {
        // Envia as métricas para o Kibana
        String payload = String.format("{\"source\": \"%s\", \"metrics\": \"%s\"}", source, metrics);
        return webClient.post()
                .body(Mono.just(payload), String.class)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.empty();
                });
    }
}

