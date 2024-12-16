package com.webflux.micromerce.cart.infrastructure.repository;

import com.webflux.micromerce.cart.domain.model.Cart;
import com.webflux.micromerce.cart.domain.model.CartStatus;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository("mongoCartRepository")
public interface MongoCartRepository extends ReactiveMongoRepository<Cart, String> {
    Flux<Cart> findByUserId(String userId);
    
    Mono<Cart> findByUserIdAndStatus(String userId, CartStatus status);
    
    @Query("{'status': ?0, 'updatedAt': {$lt: ?1}}")
    Flux<Cart> findAbandonedCarts(CartStatus status, LocalDateTime threshold);
    
    @Query("{'userId': ?0, 'status': ?1, 'createdAt': {$gte: ?2, $lte: ?3}}")
    Flux<Cart> findByUserIdAndStatusBetweenDates(
            String userId, 
            CartStatus status, 
            LocalDateTime startDate, 
            LocalDateTime endDate);
            
    Flux<Cart> findByStatus(CartStatus status);
}
