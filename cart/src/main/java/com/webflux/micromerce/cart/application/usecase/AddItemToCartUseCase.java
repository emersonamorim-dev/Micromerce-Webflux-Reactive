package com.webflux.micromerce.cart.application.usecase;

import com.webflux.micromerce.cart.domain.model.Cart;
import com.webflux.micromerce.cart.domain.model.CartStatus;
import com.webflux.micromerce.cart.domain.repository.CartRepository;
import com.webflux.micromerce.cart.application.mapper.CartMapper;
import com.webflux.micromerce.cart.application.dto.request.CartItemRequest;
import com.webflux.micromerce.cart.application.dto.response.CartResponse;
import com.webflux.micromerce.cart.infrastructure.messaging.CartEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Service
public class AddItemToCartUseCase {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final CartEventPublisher eventPublisher;

    public AddItemToCartUseCase(CartRepository cartRepository,
                               CartMapper cartMapper,
                               CartEventPublisher eventPublisher) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
        this.eventPublisher = eventPublisher;
    }

    public Mono<CartResponse> execute(String cartId, CartItemRequest request) {
        log.info("Adicionando item ao carrinho: {}", cartId);
        
        return findOrCreateCart(cartId)
            .flatMap(cart -> addItemToCart(cart, request))
            .flatMap(this::persistCartUpdates)
            .map(cartMapper::toResponse)
            .doOnSuccess(this::publishCartEvent)
            .doOnError(e -> log.error("Erro ao adicionar item ao carrinho: {}", e.getMessage()));
    }

    private Mono<Cart> findOrCreateCart(String cartId) {
        return cartRepository.findById(cartId)
            .switchIfEmpty(createNewCart(cartId))
            .doOnNext(cart -> log.debug("Carrinho encontrado/criado: {}", cartId));
    }

    private Mono<Cart> createNewCart(String cartId) {
        Cart newCart = Cart.builder()
                .id(cartId)
                .userId(cartId) // Using cartId as userId for now
                .items(new ArrayList<>())
                .status(CartStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
                
        log.debug("Criando novo carrinho: {}", cartId);
        return cartRepository.save(newCart);
    }

    private Mono<Cart> addItemToCart(Cart cart, CartItemRequest request) {
        if (cart.getStatus() != CartStatus.ACTIVE) {
            log.warn("Tentou adicionar item ao carrinho inativo: {}", cart.getId());
            return Mono.error(new IllegalStateException("O carrinho não está ativo"));
        }

        cart.addItem(cartMapper.toCartItem(request));
        cart.setUpdatedAt(LocalDateTime.now());
        
        log.debug("Item adicionado ao carrinho: {}", cart.getId());
        return Mono.just(cart);
    }

    private Mono<Cart> persistCartUpdates(Cart cart) {
        return cartRepository.save(cart)
            .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
                .doBeforeRetry(signal -> 
                    log.warn("Tentando atualizar o carrinho novamente após falha. Tentativa: {}", signal.totalRetries())
                ))
            .doOnSuccess(savedCart -> 
                log.debug("Atualizações de carrinho persistentes com sucesso: {}", savedCart.getId())
            );
    }

    private void publishCartEvent(CartResponse cartResponse) {
        eventPublisher.publishCartEvent(cartResponse)
            .doOnSuccess(unused -> log.debug("Evento de carrinho publicado com sucesso"))
            .doOnError(error -> log.error("Erro ao publicar evento de carrinho: {}", error.getMessage()))
            .subscribe();
    }
}
