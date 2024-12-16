package com.webflux.micromerce.cart.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartRequest(
    @NotBlank(message = "UserId é obrigatório")
    String userId
) {}
