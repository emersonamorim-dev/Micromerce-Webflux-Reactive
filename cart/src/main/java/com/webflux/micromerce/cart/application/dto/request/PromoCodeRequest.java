package com.webflux.micromerce.cart.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PromoCodeRequest(
    @NotBlank(message = "O código promocional é obrigatório")
    String promoCode
) {}
