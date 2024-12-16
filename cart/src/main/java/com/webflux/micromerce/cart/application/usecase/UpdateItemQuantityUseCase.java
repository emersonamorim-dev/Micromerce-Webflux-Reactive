package com.webflux.micromerce.cart.application.usecase;

import com.webflux.micromerce.cart.application.dto.request.UpdateQuantityRequest;
import com.webflux.micromerce.cart.application.dto.response.CartResponse;
import com.webflux.micromerce.cart.application.mapper.CartMapper;
import com.webflux.micromerce.cart.domain.model.Cart;
import com.webflux.micromerce.cart.domain.model.CartStatus;
import com.webflux.micromerce.cart.domain.repository.CartRepository;
import com.webflux.micromerce.cart.infrastructure.messaging.CartEventPublisher;
import com.webflux.micromerce.cart.infrastructure.redis.RedisService;
import com.webflux.micromerce.cart.presentation.exception.CartNotFoundException;
import com.webflux.micromerce.cart.presentation.exception.ItemNotFoundException;
import com.webflux.micromerce.cart.presentation.exception.InvalidCartStateException;
import com.webflux.micromerce.cart.presentation.exception.InvalidQuantityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateItemQuantityUseCase {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final RedisService redisService;
    private final CartEventPublisher eventPublisher;

    public Mono<CartResponse> execute(String cartId, String productId, int quantity) {
        if (quantity < 1) {
            return Mono.error(new InvalidQuantityException(quantity));
        }

        UpdateQuantityRequest request = UpdateQuantityRequest.builder()
            .quantity(quantity)
            .build();

        return findAndValidateCart(cartId)
            .flatMap(cart -> updateCartItem(cart, productId, request))
            .flatMap(this::persistCartUpdates)
            .map(cartMapper::toResponse)
            .doOnSuccess(response -> 
                eventPublisher.publishCartEvent(response).subscribe()
            );
    }

    private Mono<Cart> findAndValidateCart(String cartId) {
        return redisService.getCart(cartId)
            .switchIfEmpty(cartRepository.findById(cartId))
            .switchIfEmpty(Mono.error(new CartNotFoundException(cartId)))
            .flatMap(this::validateCartState);
    }

    private Mono<Cart> validateCartState(Cart cart) {
        if (cart.getStatus() != CartStatus.ACTIVE) {
            return Mono.error(new InvalidCartStateException(cart.getId(), cart.getStatus()));
        }
        return Mono.just(cart);
    }

    private Mono<Cart> updateCartItem(Cart cart, String productId, UpdateQuantityRequest request) {
        return Mono.justOrEmpty(cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst())
                .switchIfEmpty(Mono.error(new ItemNotFoundException(productId)))
                .map(item -> {
                    item.setQuantity(request.getQuantity());
                    cart.setUpdatedAt(LocalDateTime.now());
                    cart.recalculateTotal();
                    return cart;
                });
    }

    private Mono<Cart> persistCartUpdates(Cart cart) {
        return cartRepository.save(cart)
            .flatMap(savedCart -> 
                redisService.setCart(savedCart)
                    .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
                    .onErrorResume(error -> {
                        log.error("Erro ao atualizar o cache do Redis para o carrinho {}: {}", cart.getId(), error.getMessage());
                        return Mono.just(false);
                    })
                    .thenReturn(savedCart)
            );
    }
}
