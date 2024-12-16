package com.webflux.micromerce.cart.application.dto.request;

import com.webflux.micromerce.cart.domain.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartRequest {
    private List<CartItem> items;
    private String promoCode;
    private BigDecimal discountAmount;
}
