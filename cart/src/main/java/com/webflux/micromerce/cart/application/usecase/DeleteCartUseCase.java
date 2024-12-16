package com.webflux.micromerce.cart.application.usecase;

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
public class DeleteCartUseCase {
    private final CartRepository cartRepository;
    private final RedisService redisService;

    public Mono<Void> execute(String cartId) {
        return cartRepository.findById(cartId)
            .switchIfEmpty(Mono.error(new CartNotFoundException("Carrinho não encontrado: " + cartId)))
            .flatMap(this::deleteFromAllSources)
            .doOnSuccess(v -> 
                log.info("Carrinho {} excluído com sucesso de todas as fontes", cartId)
            )
            .doOnError(error -> 
                log.error("Erro ao excluir carrinho {}: {}", cartId, error.getMessage())
            );
    }

    private Mono<Void> deleteFromAllSources(Cart cart) {
        String cartId = cart.getId();

        Mono<Boolean> redisDelete = redisService.deleteCart(cartId)
            .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
            .onErrorResume(error -> {
                log.error("Erro a excluir carrinho {} do Redis: {}", cartId, error.getMessage());
                return Mono.just(false);
            });

        Mono<Void> mongoDelete = cartRepository.deleteById(cartId)
            .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
            .onErrorResume(error -> {
                log.error("Erro a excluir carrinho {} do MongoDB: {}", cartId, error.getMessage());
                return Mono.empty();
            });

        return Mono.when(redisDelete, mongoDelete);
    }
}
