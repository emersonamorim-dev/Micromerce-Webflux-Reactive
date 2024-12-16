package com.webflux.micromerce.cart.application.usecase;

import com.webflux.micromerce.cart.domain.model.Cart;
import com.webflux.micromerce.cart.domain.repository.CartRepository;
import com.webflux.micromerce.cart.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetUserCartsUseCase {

    private final CartRepository cartRepository;
    private final RedisService redisService;

    public Flux<Cart> execute(String userId) {
        // Primeiro tenta buscar do cache
        return findCartsFromCache(userId)
                .switchIfEmpty(findCartsFromRepository(userId))
                .doOnComplete(() -> log.debug("Concluída a recuperação de carrinhos para o usuário {}", userId))
                .doOnError(error -> log.error("Erro ao recuperar carrinhos para o usuário {}: {}", userId, error.getMessage()));
    }

    private Flux<Cart> findCartsFromCache(String userId) {
        return redisService.getUserCarts(userId)
                .flatMap(cartId -> redisService.getCart(String.valueOf(cartId)))
                .doOnNext(cart -> log.debug("Carrinho {} encontrado no Redis para o usuário {}", cart.getId(), userId))
                .onErrorResume(error -> {
                    log.error("Erro ao recuperar carrinhos do Redis para o usuário {}: {}", userId, error.getMessage());
                    return Flux.empty();
                });
    }

    private Flux<Cart> findCartsFromRepository(String userId) {
        return cartRepository.findByUserId(userId)
                .flatMap(cart -> {
                    log.debug("Carrinho {} encontrado no Repositório para o usuário {}", cart.getId(), userId);
                    return updateCache(cart).thenReturn(cart);
                })
                .onErrorResume(error -> {
                    log.error("Erro ao recuperar carrinhos do Repositório para o usuário {}: {}", userId, error.getMessage());
                    return Flux.empty();
                });
    }

    private Mono<Boolean> updateCache(Cart cart) {
        return redisService.setCart(cart)
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
                .onErrorResume(error -> {
                    log.error("Erro ao atualizar o cache do Redis para o carrinho {}: {}", cart.getId(), error.getMessage());
                    return Mono.just(false);
                });
    }
}
