package com.webflux.micromerce.payment.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RefundPaymentRequest(
    @NotNull(message = "ID do pagamento é obrigatório")
    UUID paymentId,

    String reason

) {}
