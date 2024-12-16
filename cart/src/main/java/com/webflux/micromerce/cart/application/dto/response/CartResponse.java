package com.webflux.micromerce.cart.application.dto.response;

import com.webflux.micromerce.cart.domain.model.Cart;
import com.webflux.micromerce.cart.domain.model.CartStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record CartResponse(
    String id,
    String userId,
    List<CartItemResponse> items,
    BigDecimal totalAmount,
    String promoCode,
    BigDecimal discountAmount,
    CartStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static CartResponse fromDomain(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
            .map(item -> new CartItemResponse(
                item.getId(),
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
            ))
            .collect(Collectors.toList());

        return new CartResponse(
            cart.getId(),
            cart.getUserId(),
            itemResponses,
            cart.getTotalAmount(),
            cart.getPromoCode(),
            cart.getDiscountAmount(),
            cart.getStatus(),
            cart.getCreatedAt(),
            cart.getUpdatedAt()
        );
    }
}
