package com.webflux.micromerce.cart.application.service;

import com.webflux.micromerce.cart.domain.model.Cart;
import com.webflux.micromerce.cart.domain.model.CartItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CartService {
    Mono<Cart> createCart(String userId);
    Mono<Cart> getCart(String cartId);
    Mono<Cart> addItem(String cartId, CartItem item);
    Mono<Cart> removeItem(String cartId, String itemId);
    Mono<Cart> updateItemQuantity(String cartId, String itemId, int quantity);
    Mono<Cart> applyPromoCode(String cartId, String promoCode);
    Mono<Cart> checkout(String cartId);
    Flux<Cart> getUserCarts(String userId);

    Mono<Cart> getActiveCart(String userId);
}
