package com.webflux.micromerce.cart.application.mapper;

import com.webflux.micromerce.cart.application.dto.request.CartItemRequest;
import com.webflux.micromerce.cart.application.dto.response.CartItemResponse;
import com.webflux.micromerce.cart.application.dto.response.CartResponse;
import com.webflux.micromerce.cart.domain.model.Cart;
import com.webflux.micromerce.cart.domain.model.CartItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {
    
    public CartResponse toResponse(Cart cart) {
        return new CartResponse(
            cart.getId(),
            cart.getUserId(),
            toCartItemResponses(cart.getItems()),
            cart.getTotalAmount(),
            cart.getPromoCode(),
            cart.getDiscountAmount(),
            cart.getStatus(),
            cart.getCreatedAt(),
            cart.getUpdatedAt()
        );
    }
    
    private List<CartItemResponse> toCartItemResponses(List<CartItem> items) {
        return items.stream()
                .map(this::toCartItemResponse)
                .collect(Collectors.toList());
    }
    
    private CartItemResponse toCartItemResponse(CartItem item) {
        return new CartItemResponse(
            item.getId(),
            item.getProductId(),
            item.getQuantity(),
            item.getUnitPrice(),
            item.getSubtotal()
        );
    }

    public CartItem toCartItem(CartItemRequest request) {
        return CartItem.builder()
            .productId(request.productId())
            .productName(request.productName())
            .quantity(request.quantity())
            .unitPrice(request.unitPrice())
            .imageUrl(request.imageUrl())
            .build();
    }
}
