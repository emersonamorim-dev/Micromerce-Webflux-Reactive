package com.webflux.micromerce.payment.application.dto;

import com.webflux.micromerce.payment.domain.model.PaymentMethod;
import com.webflux.micromerce.payment.domain.model.PaymentMethodList;
import com.webflux.micromerce.payment.domain.model.PaymentStatus;
import com.webflux.micromerce.payment.domain.model.PaymentType;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethodDTO {
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

    public static PaymentMethodDTO from(PaymentMethodList payment) {
        PaymentMethodDTO dto = new PaymentMethodDTO();
        dto.id = payment.getId();
        dto.amount = payment.getAmount();
        dto.beneficiario = payment.getBeneficiario();
        dto.createdAt = payment.getCreatedAt();
        dto.status = payment.getStatus();
        dto.pagador = payment.getPagador();
        dto.customerId = payment.getCustomerId();
        dto.orderId = payment.getOrderId();
        dto.paymentType = payment.getPaymentType();
        dto.paymentIdentifier = payment.getPaymentIdentifier();
        return dto;
    }
}
