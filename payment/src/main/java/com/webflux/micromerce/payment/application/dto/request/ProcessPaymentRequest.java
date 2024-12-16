package com.webflux.micromerce.payment.application.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.webflux.micromerce.payment.domain.model.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "paymentType",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ProcessPaymentRequest.CreditCardPaymentRequest.class, name = "CREDIT_CARD"),
    @JsonSubTypes.Type(value = ProcessPaymentRequest.DebitCardPaymentRequest.class, name = "DEBIT_CARD"),
    @JsonSubTypes.Type(value = ProcessPaymentRequest.BoletoPaymentRequest.class, name = "BOLETO"),
    @JsonSubTypes.Type(value = ProcessPaymentRequest.PixPaymentRequest.class, name = "PIX")
})
public sealed interface ProcessPaymentRequest {
    @NotNull(message = "Valor do pagamento é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor do pagamento deve ser maior que zero")
    BigDecimal amount();

    @NotNull(message = "ID do cliente é obrigatório")
    UUID customerId();

    @NotBlank(message = "ID do pedido é obrigatório")
    String orderId();

    String paymentType();

    record CreditCardPaymentRequest(
        @NotBlank(message = "Número do cartão é obrigatório")
        String cardNumber,

        @NotBlank(message = "Nome do titular do cartão é obrigatório")
        String cardHolderName,

        @NotBlank(message = "CVV é obrigatório")
        String cvv,

        @NotNull(message = "Valor do pagamento é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor do pagamento deve ser maior que zero")
        BigDecimal amount,

        @NotNull(message = "ID do cliente é obrigatório")
        UUID customerId,

        @NotBlank(message = "ID do pedido é obrigatório")
        String orderId
    ) implements ProcessPaymentRequest {
        public String paymentType() {
            return "CREDIT_CARD";
        }

        public PaymentMethod toDomain() {
            return new CreditCardPayment(
                UUID.randomUUID(), 
                cardNumber, 
                cardHolderName, 
                cvv, 
                amount, 
                LocalDateTime.now(), 
                PaymentStatus.PENDING,
                customerId,
                orderId
            );
        }
    }

    record DebitCardPaymentRequest(
        @NotBlank(message = "Número do cartão é obrigatório")
        String cardNumber,

        @NotBlank(message = "Nome do titular do cartão é obrigatório")
        String cardHolderName,

        @NotNull(message = "Valor do pagamento é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor do pagamento deve ser maior que zero")
        BigDecimal amount,

        @NotNull(message = "ID do cliente é obrigatório")
        UUID customerId,

        @NotBlank(message = "ID do pedido é obrigatório")
        String orderId
    ) implements ProcessPaymentRequest {
        public String paymentType() {
            return "DEBIT_CARD";
        }

        public PaymentMethod toDomain() {
            return new DebitCardPayment(
                UUID.randomUUID(), 
                cardNumber, 
                cardHolderName, 
                amount, 
                LocalDateTime.now(), 
                PaymentStatus.PENDING,
                customerId,
                orderId
            );
        }
    }

    record BoletoPaymentRequest(
        @NotBlank(message = "Número do boleto é obrigatório")
        String boletoNumber,

        @NotNull(message = "Valor do pagamento é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor do pagamento deve ser maior que zero")
        BigDecimal amount,

        @NotBlank(message = "beneficiario é obrigatório")
        String beneficiario,

        @NotNull(message = "Data de vencimento é obrigatória")
        LocalDateTime dueDate,

        @NotNull(message = "ID do cliente é obrigatório")
        UUID customerId,

        @NotBlank(message = "pagador é obrigatório")
        String pagador,

        @NotBlank(message = "ID do pedido é obrigatório")
        String orderId
    ) implements ProcessPaymentRequest {
        public String paymentType() {
            return "BOLETO";
        }

        public PaymentMethod toDomain() {
            return new BoletoPayment(
                UUID.randomUUID(), 
                boletoNumber, 
                amount,
                beneficiario,
                dueDate, 
                LocalDateTime.now(), 
                PaymentStatus.PENDING,
                pagador,
                customerId,
                orderId
            );
        }
    }

    record PixPaymentRequest(
        @NotBlank(message = "Chave PIX é obrigatória")
        String pixKey,

        @NotBlank(message = "Tipo da chave PIX é obrigatório")
        String pixKeyType,

        @NotNull(message = "Valor do pagamento é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor do pagamento deve ser maior que zero")
        BigDecimal amount,

        @NotNull(message = "ID do cliente é obrigatório")
        UUID customerId,

        @NotBlank(message = "ID do pedido é obrigatório")
        String orderId
    ) implements ProcessPaymentRequest {
        public String paymentType() {
            return "PIX";
        }

        public PaymentMethod toDomain() {
            return new PixPayment(
                UUID.randomUUID(), 
                pixKey,
                pixKeyType,
                amount, 
                LocalDateTime.now(), 
                PaymentStatus.PENDING,
                customerId,
                orderId
            );
        }
    }
}
