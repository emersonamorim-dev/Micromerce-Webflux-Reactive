package com.webflux.micromerce.catalog.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webflux.micromerce.catalog.application.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String PRODUCT_KEY_PREFIX = "product:";
    private static final Duration CACHE_DURATION = Duration.ofHours(24);

    public Mono<Void> saveProduct(ProductResponse product) {
        log.debug("Saving product to Redis cache: {}", product);
        String key = PRODUCT_KEY_PREFIX + product.getId();
        
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(product))
                .flatMap(productJson -> redisTemplate.opsForValue().set(key, productJson, CACHE_DURATION))
                .then()
                .doOnSuccess(unused -> log.debug("Produto armazenado em cache com sucesso com chave: {}", key))
                .doOnError(error -> log.error("Erro ao armazenar em cache o produto com a chave {}: {}", key, error.getMessage()))
                .onErrorResume(e -> Mono.empty());
    }

    public Mono<ProductResponse> getProduct(Long id) {
        String key = PRODUCT_KEY_PREFIX + id;
        log.debug("Recuperando produto do cache Redis com chave: {}", key);
        
        return redisTemplate.opsForValue().get(key)
                .flatMap(productJson -> Mono.fromCallable(() -> objectMapper.readValue(productJson, ProductResponse.class)))
                .doOnSuccess(product -> {
                    if (product != null) {
                        log.debug("Produto encontrado no cache: {}", product);
                    } else {
                        log.debug("Produto não encontrado no cache para chave: {}", key);
                    }
                })
                .doOnError(error -> log.error("Erro ao recuperar produto do cache com chave {}: {}", key, error.getMessage()))
                .onErrorResume(e -> Mono.empty());
    }

    public Mono<Boolean> deleteProduct(Long id) {
        String key = PRODUCT_KEY_PREFIX + id;
        log.debug("Excluindo produto do cache do Redis com chave: {}", key);
        
        return redisTemplate.delete(key)
                .map(count -> count > 0)
                .doOnSuccess(deleted -> {
                    if (deleted) {
                        log.debug("Produto excluído com sucesso do cache com chave: {}", key);
                    } else {
                        log.debug("Produto não encontrado no cache para exclusão com chave: {}", key);
                    }
                })
                .doOnError(error -> log.error("Erro ao excluir produto do cache com chave {}: {}", key, error.getMessage()))
                .onErrorResume(e -> Mono.just(false));
    }
}
