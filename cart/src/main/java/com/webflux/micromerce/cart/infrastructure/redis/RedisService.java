package com.webflux.micromerce.cart.infrastructure.redis;

import com.webflux.micromerce.cart.domain.model.Cart;
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
public class RedisService {

    private final CartRedisRepository redisRepository;

    public Mono<Cart> getCart(String cartId) {
        return redisRepository.findById(cartId)
                .doOnNext(cart -> log.debug("Carrinho {} encontrado em Redis", cartId))
                .doOnError(error -> log.error("Erro ao recuperar carrinho {} do Redis: {}", cartId, error.getMessage()));
    }

    public Mono<Boolean> setCart(Cart cart) {
        return redisRepository.save(cart)
                .map(savedCart -> true)
                .doOnNext(result -> log.debug("Carrinho {} salvo em Redis", cart.getId()))
                .doOnError(error -> log.error("Erro ao salvar carrinho {} para Redis: {}", cart.getId(), error.getMessage()))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
                .onErrorReturn(false);
    }

    public Mono<Boolean> deleteCart(String cartId) {
        return redisRepository.deleteById(cartId)
                .doOnNext(result -> log.debug("Carrinho {} excluído do Redis", cartId))
                .doOnError(error -> log.error("Erro ao excluir carrinho {} do Redis: {}", cartId, error.getMessage()))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
                .onErrorReturn(false);
    }

    public Flux<Cart> getUserCarts(String userId) {
        return redisRepository.findByUserId(userId)
                .doOnComplete(() -> log.debug("Carrinhos recuperados para o usuário {} do Redis", userId))
                .doOnError(error -> log.error("Erro ao recuperar carrinhos para o usuário {} do Redis: {}", userId, error.getMessage()));
    }
}
