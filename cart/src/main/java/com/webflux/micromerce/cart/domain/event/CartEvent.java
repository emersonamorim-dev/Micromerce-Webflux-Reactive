package com.webflux.micromerce.cart.domain.event;

import com.webflux.micromerce.cart.domain.model.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartEvent {
    private String cartId;
    private String eventType;
    private LocalDateTime timestamp;
    private Cart cart;

    public static CartEvent of(String eventType, Cart cart) {
        return CartEvent.builder()
                .cartId(cart.getId())
                .eventType(eventType)
                .timestamp(LocalDateTime.now())
                .cart(cart)
                .build();
    }

    public static final class EventType {
        public static final String CART_CREATED = "CART_CREATED";
        public static final String CART_UPDATED = "CART_UPDATED";
        public static final String CART_DELETED = "CART_DELETED";
        public static final String ITEM_ADDED = "ITEM_ADDED";
        public static final String ITEM_REMOVED = "ITEM_REMOVED";
        public static final String CART_CHECKED_OUT = "CART_CHECKED_OUT";
        public static final String CART_ABANDONED = "CART_ABANDONED";
    }
}
