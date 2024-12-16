package com.webflux.micromerce.cart.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CartItemRequest(
    @NotBlank(message = "ProductId é obrigatório")
    String productId,
    
    @NotBlank(message = "O nome do produto é obrigatório")
    String productName,
    
    @NotNull(message = "Quantidade é necessária")
    @Positive(message = "A quantidade deve ser positiva")
    Integer quantity,
    
    @NotNull(message = "Preço unitário é obrigatório")
    @Positive(message = "O preço unitário deve ser positivo")
    BigDecimal unitPrice,

    String imageUrl
) {}
