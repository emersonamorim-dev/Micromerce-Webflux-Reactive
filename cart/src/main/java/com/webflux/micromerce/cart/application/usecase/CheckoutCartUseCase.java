package com.webflux.micromerce.cart.application.usecase;

import com.webflux.micromerce.cart.application.dto.response.CartResponse;
import com.webflux.micromerce.cart.application.mapper.CartMapper;
import com.webflux.micromerce.cart.domain.model.Cart;
import com.webflux.micromerce.cart.domain.model.CartStatus;
import com.webflux.micromerce.cart.domain.repository.CartRepository;
import com.webflux.micromerce.cart.infrastructure.messaging.CartEventPublisher;
import com.webflux.micromerce.cart.infrastructure.redis.RedisService;
import com.webflux.micromerce.cart.presentation.exception.CartNotFoundException;
import com.webflux.micromerce.cart.presentation.exception.InvalidCartStateException;
import com.webflux.micromerce.cart.presentation.exception.EmptyCartException;
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
public class CheckoutCartUseCase {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final RedisService redisService;
    private final CartEventPublisher eventPublisher;

    public Mono<CartResponse> execute(String cartId) {
        return findAndValidateCart(cartId)
            .flatMap(this::startCheckout)
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
        if (cart.getItems().isEmpty()) {
            return Mono.error(new EmptyCartException(cart.getId()));
        }
        return Mono.just(cart);
    }

    private Mono<Cart> startCheckout(Cart cart) {
        cart.setStatus(CartStatus.CHECKOUT_IN_PROGRESS);
        cart.setUpdatedAt(LocalDateTime.now());
        return Mono.just(cart);
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
