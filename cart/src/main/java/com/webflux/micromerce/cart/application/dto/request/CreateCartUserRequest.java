package com.webflux.micromerce.cart.application.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCartUserRequest {
    private String id;
    private String userId;
    private String description;
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private BigDecimal totalAmount;
    private List<CartItemRequest> items;
    private String promoCode;
    private BigDecimal discountAmount;
    private LocalDateTime completedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemRequest {
        private String id;
        private String productId;
        private String name;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subtotal;
    }
}
