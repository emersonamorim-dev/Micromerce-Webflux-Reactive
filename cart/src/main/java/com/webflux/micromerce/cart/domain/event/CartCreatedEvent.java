package com.webflux.micromerce.cart.domain.event;

import com.webflux.micromerce.cart.domain.model.Cart;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CartCreatedEvent {
    private String eventId;
    private String cartId;
    private String userId;
    private String eventType;
    private LocalDateTime timestamp;
    private Cart cart;

    public static CartCreatedEvent fromCart(Cart cart) {
        return CartCreatedEvent.builder()
                .eventId(java.util.UUID.randomUUID().toString())
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .eventType("CART_CREATED")
                .timestamp(LocalDateTime.now())
                .cart(cart)
                .build();
    }
}
