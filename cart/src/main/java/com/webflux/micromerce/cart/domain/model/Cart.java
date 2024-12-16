package com.webflux.micromerce.cart.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "carts")
public class Cart {
    @Id
    private String id;

    private String userId;

    private String description;

    private CartStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private BigDecimal totalAmount;

    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    private String promoCode;

    private BigDecimal discountAmount;

    private LocalDateTime completedAt;

    public void addItem(CartItem item) {
        item.setId(UUID.randomUUID().toString());
        this.items.add(item);
        recalculateTotal();
    }

    public void removeItem(String itemId) {
        items.removeIf(item -> item.getId().equals(itemId));
        recalculateTotal();
    }

    public void updateItemQuantity(String itemId, int quantity) {
        items.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    recalculateTotal();
                });
    }

    public void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (this.discountAmount != null && this.discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.totalAmount = this.totalAmount.subtract(this.discountAmount);
        }
        
        this.updatedAt = LocalDateTime.now();
    }

    public void applyPromoCode(String promoCode, BigDecimal discount) {
        this.promoCode = promoCode;
        this.discountAmount = discount;
        recalculateTotal();
    }

    public CartItem findItemById(String itemId) {
        return items.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
        this.updatedAt = LocalDateTime.now();
    }
}
