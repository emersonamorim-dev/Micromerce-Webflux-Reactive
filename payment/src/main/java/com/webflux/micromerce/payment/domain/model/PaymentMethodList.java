package com.webflux.micromerce.payment.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class PaymentMethodList {
    private UUID id;
    private BigDecimal amount;
    private String beneficiario;
    private LocalDateTime createdAt;
    private PaymentStatus status;
    private String pagador;
    private UUID customerId;
    private String orderId;
    private PaymentType paymentType;
    private String paymentIdentifier;

    // Construtor padrão necessário para o R2DBC
    public PaymentMethodList() {}
}
