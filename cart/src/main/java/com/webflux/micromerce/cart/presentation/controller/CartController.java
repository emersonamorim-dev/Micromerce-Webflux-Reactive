package com.webflux.micromerce.cart.presentation.controller;

import com.webflux.micromerce.cart.application.dto.request.CartItemRequest;
import com.webflux.micromerce.cart.application.dto.request.CreateCartUserRequest;
import com.webflux.micromerce.cart.application.dto.request.PromoCodeRequest;
import com.webflux.micromerce.cart.application.dto.response.CartResponse;
import com.webflux.micromerce.cart.application.usecase.*;
import com.webflux.micromerce.cart.domain.model.Cart;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CreateCartUseCase createCartUseCase;
    private final GetCartUseCase getCartUseCase;
    private final AddItemToCartUseCase addItemToCartUseCase;
    private final RemoveItemFromCartUseCase removeItemFromCartUseCase;
    private final UpdateItemQuantityUseCase updateItemQuantityUseCase;
    private final ApplyPromoCodeUseCase applyPromoCodeUseCase;
    private final CheckoutCartUseCase checkoutCartUseCase;
    private final GetUserCartsUseCase getUserCartsUseCase;
    private final AbandonCartUseCase abandonCartUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CartResponse> createCart(@RequestHeader(value = "X-User-Id", required = true) String userId) {
        log.info("Recebida requisição para criar carrinho para usuário: {}", userId);
        return createCartUseCase.execute(userId)
                .doOnSuccess(response -> log.info("Carrinho criado com sucesso para usuário: {}", userId))
                .doOnError(error -> log.error("Erro ao criar carrinho para usuário {}: {}", userId, error.getMessage()));
    }

    @GetMapping("/{cartId}")
    public Mono<CartResponse> getCart(@PathVariable String cartId) {
        log.info("Buscando carrinho: {}", cartId);
        return getCartUseCase.execute(cartId)
                .doOnSuccess(response -> log.info("Carrinho {} encontrado com sucesso", cartId))
                .doOnError(error -> log.error("Erro ao buscar carrinho {}: {}", cartId, error.getMessage()));
    }

    @PostMapping("/{cartId}/items")
    public Mono<CartResponse> addItem(
            @PathVariable String cartId,
            @Valid @RequestBody CartItemRequest request) {
        log.info("Adicionando item ao carrinho: {}", cartId);
        return addItemToCartUseCase.execute(cartId, request)
                .doOnSuccess(response -> log.info("Item adicionado com sucesso ao carrinho {}", cartId))
                .doOnError(error -> log.error("Erro ao adicionar item ao carrinho {}: {}", cartId, error.getMessage()));
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    public Mono<CartResponse> removeItem(
            @PathVariable String cartId,
            @PathVariable String itemId) {
        log.info("Removendo item {} do carrinho: {}", itemId, cartId);
        return removeItemFromCartUseCase.execute(cartId, itemId)
                .doOnSuccess(response -> log.info("Item {} removido com sucesso do carrinho {}", itemId, cartId))
                .doOnError(error -> log.error("Erro ao remover item {} do carrinho {}: {}", itemId, cartId, error.getMessage()));
    }

    @PutMapping("/{cartId}/items/{itemId}")
    public Mono<CartResponse> updateItemQuantity(
            @PathVariable String cartId,
            @PathVariable String itemId,
            @RequestParam int quantity) {
        log.info("Atualizando quantidade do item {} no carrinho: {}", itemId, cartId);
        return updateItemQuantityUseCase.execute(cartId, itemId, quantity)
                .doOnSuccess(response -> log.info("Quantidade do item {} atualizada com sucesso no carrinho {}", itemId, cartId))
                .doOnError(error -> log.error("Erro ao atualizar quantidade do item {} no carrinho {}: {}", itemId, cartId, error.getMessage()));
    }

    @PostMapping("/{cartId}/promo")
    public Mono<CartResponse> applyPromoCode(
            @PathVariable String cartId,
            @Valid @RequestBody PromoCodeRequest request) {
        log.info("Aplicando código promocional ao carrinho: {}", cartId);
        return applyPromoCodeUseCase.execute(cartId, request)
                .doOnSuccess(response -> log.info("Código promocional aplicado com sucesso ao carrinho {}", cartId))
                .doOnError(error -> log.error("Erro ao aplicar código promocional ao carrinho {}: {}", cartId, error.getMessage()));
    }

    @PostMapping("/{cartId}/checkout")
    public Mono<CartResponse> checkout(@PathVariable String cartId) {
        log.info("Iniciando checkout do carrinho: {}", cartId);
        return checkoutCartUseCase.execute(cartId)
                .doOnSuccess(response -> log.info("Checkout do carrinho {} realizado com sucesso", cartId))
                .doOnError(error -> log.error("Erro ao realizar checkout do carrinho {}: {}", cartId, error.getMessage()));
    }

    @PostMapping("/user-cart")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CartResponse> createCartUser(@Valid @RequestBody CreateCartUserRequest request) {
        log.info("Recebida requisição para criar carrinho com dados completos para usuário: {}", request.getUserId());
        return createCartUseCase.createCartWithUserData(request)
                .doOnSuccess(response -> log.info("Carrinho criado com sucesso com dados completos para usuário: {}", request.getUserId()))
                .doOnError(error -> log.error("Erro ao criar carrinho com dados completos para usuário {}: {}", 
                    request.getUserId(), error.getMessage()));
    }

    @GetMapping("/user/{userId}")
    public Flux<Cart> getUserCarts(@PathVariable String userId) {
        log.info("Buscando carrinhos do usuário: {}", userId);
        return getUserCartsUseCase.execute(userId)
                .doOnComplete(() -> log.info("Busca de carrinhos do usuário {} concluída", userId))
                .doOnError(error -> log.error("Erro ao buscar carrinhos do usuário {}: {}", userId, error.getMessage()));
    }

    @PatchMapping("/{cartId}/abandon")
    @ResponseStatus(HttpStatus.OK)
    public Mono<CartResponse> abandonCart(@PathVariable String cartId) {
        log.info("Abandonando carrinho: {}", cartId);
        return abandonCartUseCase.execute(cartId)
                .doOnSuccess(response -> log.info("Carrinho {} abandonado com sucesso", cartId))
                .doOnError(error -> log.error("Erro ao abandonar carrinho {}: {}", cartId, error.getMessage()));
    }
}
