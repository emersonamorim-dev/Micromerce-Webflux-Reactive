package com.webflux.micromerce.cart.application.usecase;

import com.webflux.micromerce.cart.application.dto.response.CartResponse;
import com.webflux.micromerce.cart.application.mapper.CartMapper;
import com.webflux.micromerce.cart.domain.model.Cart;
import com.webflux.micromerce.cart.domain.repository.CartRepository;
import com.webflux.micromerce.cart.infrastructure.redis.RedisService;
import com.webflux.micromerce.cart.presentation.exception.CartNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetCartUseCase {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final RedisService redisService;

    public Mono<CartResponse> execute(String cartId) {
        return findCart(cartId)
            .map(cartMapper::toResponse);
    }

    private Mono<Cart> findCart(String cartId) {
        return redisService.getCart(cartId)
            .doOnNext(cart -> log.debug("Carrinho {} encontrado em Redis cache", cartId))
            .switchIfEmpty(findCartFromRepository(cartId))
            .switchIfEmpty(Mono.error(new CartNotFoundException(cartId)))
            .doOnError(error -> log.error("Erro ao encontrar o carrinho {}: {}", cartId, error.getMessage()));
    }

    private Mono<Cart> findCartFromRepository(String cartId) {
        return cartRepository.findById(cartId)
            .flatMap(cartRepository::save)
            .doOnSuccess(cart -> {
                redisService.setCart(cart)
                    .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
                    .doOnError(error -> 
                        log.error("Erro ao atualizar o cache do carrinho {}: {}", cartId, error.getMessage())
                    )
                    .subscribe();
            });
    }
}
