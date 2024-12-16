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
                .doOnSuccess(unused -> log.debug("Product successfully cached with key: {}", key))
                .doOnError(error -> log.error("Error caching product with key {}: {}", key, error.getMessage()))
                .onErrorResume(e -> Mono.empty());
    }

    public Mono<ProductResponse> getProduct(Long id) {
        String key = PRODUCT_KEY_PREFIX + id;
        log.debug("Retrieving product from Redis cache with key: {}", key);
        
        return redisTemplate.opsForValue().get(key)
                .flatMap(productJson -> Mono.fromCallable(() -> objectMapper.readValue(productJson, ProductResponse.class)))
                .doOnSuccess(product -> {
                    if (product != null) {
                        log.debug("Product found in cache: {}", product);
                    } else {
                        log.debug("Product not found in cache for key: {}", key);
                    }
                })
                .doOnError(error -> log.error("Error retrieving product from cache with key {}: {}", key, error.getMessage()))
                .onErrorResume(e -> Mono.empty());
    }

    public Mono<Boolean> deleteProduct(Long id) {
        String key = PRODUCT_KEY_PREFIX + id;
        log.debug("Deleting product from Redis cache with key: {}", key);
        
        return redisTemplate.delete(key)
                .map(count -> count > 0)
                .doOnSuccess(deleted -> {
                    if (deleted) {
                        log.debug("Product successfully deleted from cache with key: {}", key);
                    } else {
                        log.debug("Product not found in cache for deletion with key: {}", key);
                    }
                })
                .doOnError(error -> log.error("Error deleting product from cache with key {}: {}", key, error.getMessage()))
                .onErrorResume(e -> Mono.just(false));
    }
}
