package com.webflux.micromerce.cart.domain.repository;

import com.webflux.micromerce.cart.domain.model.Cart;
import com.webflux.micromerce.cart.domain.model.CartStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface CartRepository {
    Mono<Cart> save(Cart cart);
    Mono<Cart> findById(String id);

    Mono<Cart> update(Cart cart);

    Mono<Void> deleteById(String id);
    Flux<Cart> findAll();
    Flux<Cart> findByUserId(String userId);
    Mono<Cart> findByUserIdAndStatus(String userId, CartStatus status);
    Flux<Cart> findAbandonedCarts(CartStatus status, LocalDateTime threshold);
    Flux<Cart> findByUserIdAndStatusBetweenDates(String userId, CartStatus status, LocalDateTime startDate, LocalDateTime endDate);
    Flux<Cart> findByStatus(CartStatus status);
}
