package com.webflux.micromerce.cart.application.usecase;

import com.webflux.micromerce.cart.application.dto.request.CreateCartUserRequest;
import com.webflux.micromerce.cart.application.dto.response.CartResponse;
import com.webflux.micromerce.cart.application.mapper.CartMapper;
import com.webflux.micromerce.cart.presentation.exception.CartCreationException;
import com.webflux.micromerce.cart.domain.model.Cart;
import com.webflux.micromerce.cart.domain.model.CartItem;
import com.webflux.micromerce.cart.domain.model.CartStatus;
import com.webflux.micromerce.cart.domain.repository.CartRepository;
import com.webflux.micromerce.cart.infrastructure.kafka.CartEventPublisher;
import com.webflux.micromerce.cart.infrastructure.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Service
public class CreateCartUseCase {

    private final CartRepository cartRepository;
    private final RedisService redisService;
    private final CartMapper cartMapper;
    private final CartEventPublisher eventPublisher;

    public CreateCartUseCase(
            CartRepository cartRepository,
            RedisService redisService,
            CartMapper cartMapper,
            @Qualifier("kafkaCartEventPublisher") CartEventPublisher eventPublisher) {
        this.cartRepository = cartRepository;
        this.redisService = redisService;
        this.cartMapper = cartMapper;
        this.eventPublisher = eventPublisher;
    }

    public Mono<CartResponse> execute(String userId) {
        log.info("Iniciando criação de carrinho para usuário: {}", userId);
        
        return Mono.just(createNewCart(userId))
                .flatMap(this::persistCart)
                .flatMap(this::publishEvent)
                .map(cartMapper::toResponse)
                .doOnSuccess(response -> log.info("Carrinho criado com sucesso para usuário: {}", userId))
                .onErrorResume(error -> {
                    log.error("Erro ao criar carrinho para usuário {}: {}", userId, error.getMessage());
                    return Mono.error(new CartCreationException(
                        String.format("Falha ao criar carrinho para o usuário: %s - %s", userId, error.getMessage())));
                });
    }

    private Cart createNewCart(String userId) {
        log.debug("Criando novo carrinho para usuário: {}", userId);
        return Cart.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .status(CartStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .totalAmount(BigDecimal.ZERO)
                .build();
    }

    private Mono<Cart> persistCart(Cart cart) {
        log.debug("Persistindo carrinho: {}", cart.getId());
        return cartRepository.save(cart)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .doOnSuccess(savedCart -> {
                    log.debug("Carrinho persistido com sucesso: {}", savedCart.getId());
                    redisService.setCart(savedCart)
                        .subscribe(success -> {
                            if (success) {
                                log.debug("Carrinho cacheado com sucesso no Redis: {}", savedCart.getId());
                            } else {
                                log.warn("Falha ao cachear carrinho no Redis: {}", savedCart.getId());
                            }
                        });
                });
    }

    private Mono<Cart> publishEvent(Cart cart) {
        return eventPublisher.publishCartCreatedEvent(cart)
                .doOnSuccess(result -> log.info("Evento de criação de carrinho publicado com sucesso: {}", cart.getId()))
                .doOnError(error -> log.error("Erro ao publicar evento de criação de carrinho: {}", error.getMessage()))
                .thenReturn(cart);
    }

    public Mono<CartResponse> createCartWithUserData(CreateCartUserRequest request) {
        log.info("Iniciando criação de carrinho com dados completos para usuário: {}", request.getUserId());
        
        // Primeiro verifica se o usuário já tem um registro de carrinho
        return cartRepository.findByUserId(request.getUserId())
                .next() // Pega o primeiro carrinho encontrado
                .switchIfEmpty(Mono.error(new CartCreationException(
                    String.format("Usuário %s precisa primeiro criar um carrinho básico através do endpoint /api/v1/carts", 
                        request.getUserId()))))
                .flatMap(existingCart -> {
                    // Calcula o total dos itens
                    BigDecimal subtotal = request.getItems().stream()
                            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // Calcula o desconto fixo de R$ 18,00 se o total for maior que R$ 100,00
                    BigDecimal discountAmount;
                    if (request.getPromoCode() != null && 
                        request.getPromoCode().equals("WELCOME18") && 
                        subtotal.compareTo(new BigDecimal("100.00")) > 0) {
                        discountAmount = new BigDecimal("18.00");
                    } else {
                        discountAmount = BigDecimal.ZERO;
                    }

                    // Calcula o total final após o desconto
                    BigDecimal finalTotal = subtotal.subtract(discountAmount);
                    
                    Cart cart = Cart.builder()
                            .id(request.getId())
                            .userId(request.getUserId())
                            .description(request.getDescription())
                            .status(CartStatus.valueOf(request.getStatus()))
                            .createdAt(request.getCreatedAt())
                            .updatedAt(request.getUpdatedAt())
                            .items(request.getItems().stream()
                                    .map(item -> CartItem.builder()
                                            .id(item.getId())
                                            .productId(item.getProductId())
                                            .productName(item.getName())
                                            .quantity(item.getQuantity())
                                            .unitPrice(item.getPrice())
                                            .build())
                                    .toList())
                            .totalAmount(finalTotal)
                            .promoCode(request.getPromoCode())
                            .discountAmount(discountAmount)
                            .completedAt(request.getCompletedAt())
                            .build();

                    return persistCart(cart)
                            .flatMap(this::publishEvent)
                            .map(cartMapper::toResponse)
                            .doOnSuccess(response -> {
                                log.info("Carrinho criado com sucesso com dados completos para usuário: {}", request.getUserId());
                                if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
                                    log.info("Desconto de R$ 18,00 aplicado ao carrinho: {}", cart.getId());
                                }
                            })
                            .onErrorResume(error -> {
                                log.error("Erro ao criar carrinho com dados completos para usuário {}: {}", request.getUserId(), error.getMessage());
                                return Mono.error(new CartCreationException(
                                    String.format("Falha ao criar carrinho com dados completos para o usuário: %s - %s", 
                                        request.getUserId(), error.getMessage())));
                            });
                });
    }
}
