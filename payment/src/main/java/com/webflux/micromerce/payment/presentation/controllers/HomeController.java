package com.webflux.micromerce.payment.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Tag(name = "Home", description = "Home endpoint com informações da API")
public class HomeController {

    @GetMapping("/")
    public Mono<Void> home(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
        response.getHeaders().setLocation(URI.create("/swagger-ui/index.html"));
        return response.setComplete();
    }

    @GetMapping("/api")
    public Mono<Void> api(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
        response.getHeaders().setLocation(URI.create("/v3/api-docs"));
        return response.setComplete();
    }

    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenha informações de API e endpoints disponíveis")
    public Mono<Map<String, Object>> info() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("service", "Payment Service");
        info.put("version", "1.0.0");
        info.put("status", "UP");

        Map<String, String> links = new LinkedHashMap<>();
        links.put("swagger-ui", "/swagger-ui/index.html");
        links.put("api-docs", "/v3/api-docs");
        links.put("health", "/actuator/health");
        links.put("metrics", "/actuator/metrics");
        links.put("payments", "/api/v1/payments");

        info.put("links", links);

        return Mono.just(info);
    }
}

