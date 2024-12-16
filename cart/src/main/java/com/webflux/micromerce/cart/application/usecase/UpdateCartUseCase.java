package com.webflux.micromerce.cart.application.usecase;

import com.webflux.micromerce.cart.application.dto.request.UpdateCartRequest;
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
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateCartUseCase {

    private final CartRepository cartRepository;
    private final RedisService redisService;

    public Mono<Cart> execute(String cartId, UpdateCartRequest request) {
        return cartRepository.findById(cartId)
            .switchIfEmpty(Mono.error(new CartNotFoundException("Carrinho não encontrado: " + cartId)))
            .flatMap(cart -> validateAndPrepareCart(cart, request))
            .flatMap(this::persistCartUpdates)
            .doOnSuccess(cart -> {
                log.info("Carrinho {} atualizado com sucesso", cartId);
            })
            .doOnError(error -> {
                log.error("Erro ao atualizar o carrinho {}: {}", cartId, error.getMessage());
            });
    }

    private Mono<Cart> validateAndPrepareCart(Cart cart, UpdateCartRequest request) {
        if (cart == null) {
            return Mono.error(new IllegalArgumentException("O carrinho não pode ser nulo"));
        }
        if (cart.getId() == null) {
            return Mono.error(new IllegalArgumentException("O ID do carrinho não pode ser nulo"));
        }
        
        if (request.getItems() != null) {
            cart.setItems(request.getItems());
            cart.recalculateTotal();
        }
        
        if (request.getPromoCode() != null) {
            cart.setPromoCode(request.getPromoCode());
        }
        
        if (request.getDiscountAmount() != null) {
            cart.setDiscountAmount(request.getDiscountAmount());
            cart.recalculateTotal();
        }

        cart.setUpdatedAt(LocalDateTime.now());
        return Mono.just(cart);
    }

    private Mono<Cart> persistCartUpdates(Cart cart) {
        return cartRepository.save(cart)
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
                .flatMap(savedCart -> 
                    redisService.setCart(savedCart)
                        .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
                        .onErrorResume(error -> {
                            log.error("Erro ao atualizar o cache do Redis para o carrinho {}: {}", cart.getId(), error.getMessage());
                            return Mono.just(false);
                        })
                        .thenReturn(savedCart)
                )
                .doOnError(error -> 
                    log.error("Erro a atualizar o carrinho {}: {}", cart.getId(), error.getMessage())
                );
    }
}
