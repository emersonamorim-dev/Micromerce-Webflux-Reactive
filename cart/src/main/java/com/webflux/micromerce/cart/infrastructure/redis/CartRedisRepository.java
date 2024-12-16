package com.webflux.micromerce.cart.infrastructure.redis;

import com.webflux.micromerce.cart.domain.model.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class CartRedisRepository {
    
    private final ReactiveRedisTemplate<String, Cart> redisTemplate;
    private static final String KEY_PREFIX = "cart:";
    private static final Duration CACHE_TTL = Duration.ofHours(24);

    public Mono<Cart> save(Cart cart) {
        String key = KEY_PREFIX + cart.getId();
        return redisTemplate.opsForValue()
                .set(key, cart, CACHE_TTL)
                .thenReturn(cart);
    }

    public Mono<Cart> findById(String id) {
        String key = KEY_PREFIX + id;
        return redisTemplate.opsForValue().get(key);
    }

    public Mono<Boolean> deleteById(String id) {
        String key = KEY_PREFIX + id;
        return redisTemplate.opsForValue().delete(key);
    }

    public Flux<Cart> findByUserId(String userId) {
        String pattern = KEY_PREFIX + "*";
        return redisTemplate.keys(pattern)
                .flatMap(key -> redisTemplate.opsForValue().get(key))
                .filter(cart -> cart.getUserId().equals(userId));
    }

    public Flux<Cart> findByStatus(String status) {
        String pattern = KEY_PREFIX + "*";
        return redisTemplate.keys(pattern)
                .flatMap(key -> redisTemplate.opsForValue().get(key))
                .filter(cart -> cart.getStatus().toString().equals(status));
    }

    public Flux<Cart> findByUserIdAndStatus(String userId, String status) {
        String pattern = KEY_PREFIX + "*";
        return redisTemplate.keys(pattern)
                .flatMap(key -> redisTemplate.opsForValue().get(key))
                .filter(cart -> cart.getUserId().equals(userId) && 
                              cart.getStatus().toString().equals(status));
    }
}