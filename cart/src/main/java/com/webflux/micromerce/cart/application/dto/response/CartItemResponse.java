package com.webflux.micromerce.cart.application.dto.response;

import java.math.BigDecimal;

public record CartItemResponse(
    String id,
    String productId,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal subtotal
) {}
