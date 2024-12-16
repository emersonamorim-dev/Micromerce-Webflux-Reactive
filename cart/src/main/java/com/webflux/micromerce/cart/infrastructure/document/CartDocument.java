package com.webflux.micromerce.cart.infrastructure.document;

import com.webflux.micromerce.cart.domain.model.Cart;
import com.webflux.micromerce.cart.domain.model.CartItem;
import com.webflux.micromerce.cart.domain.model.CartStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "carts")
@org.springframework.data.mongodb.core.mapping.Document(collection = "carts")
public class CartDocument {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private UUID userId;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Double)
    private BigDecimal totalAmount;

    @Field(type = FieldType.Keyword)
    private CartStatus status;

    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;

    @Builder.Default
    @Field(type = FieldType.Nested)
    private List<CartItem> items = new ArrayList<>();

    @Field(type = FieldType.Keyword)
    private String promoCode;

    @Field(type = FieldType.Double)
    private BigDecimal discountAmount;

    @Field(type = FieldType.Date)
    private LocalDateTime completedAt;

    public static CartDocument fromDomain(Cart cart) {
        return CartDocument.builder()
                .id(cart.getId())
                .userId(UUID.fromString(cart.getUserId()))
                .description(cart.getDescription())
                .totalAmount(cart.getTotalAmount())
                .status(cart.getStatus())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .items(cart.getItems())
                .promoCode(cart.getPromoCode())
                .discountAmount(cart.getDiscountAmount())
                .completedAt(cart.getCompletedAt())
                .build();
    }

    public Cart toDomain() {
        return Cart.builder()
                .id(this.id)
                .userId(String.valueOf(this.userId))
                .description(this.description)
                .totalAmount(this.totalAmount)
                .status(this.status)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .items(this.items)
                .promoCode(this.promoCode)
                .discountAmount(this.discountAmount)
                .completedAt(this.completedAt)
                .build();
    }
}
