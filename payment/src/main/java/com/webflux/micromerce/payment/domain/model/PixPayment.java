package com.webflux.micromerce.payment.domain.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("payment")
@Document(indexName = "payments")
public final class PixPayment implements PaymentMethod {
    @Setter
    @Id
    @Field(type = FieldType.Keyword)
    private UUID id;
    
    @Setter
    @Column("amount")
    @Field(type = FieldType.Double)
    private BigDecimal amount;
    
    @Setter
    @Column("created_at")
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;
    
    @Setter
    @Column("status")
    @Field(type = FieldType.Keyword)
    private PaymentStatus status;

    @Setter
    @Column("customer_id")
    @Field(type = FieldType.Keyword)
    private UUID customerId;

    @Setter
    @Column("order_id")
    @Field(type = FieldType.Keyword) 
    private String orderId;

    @Setter
    @Getter
    @Column("pix_key")
    @Field(type = FieldType.Keyword)
    private String pixKey;

    @Setter
    @Getter
    @Column("pix_key_type")
    @Field(type = FieldType.Keyword)
    private String pixKeyType;

    @Column("payment_type")
    @Field(type = FieldType.Keyword)
    private final PaymentType paymentType = PaymentType.PIX;

    public PixPayment() {
    }

    public PixPayment(UUID id, 
                     @NotBlank(message = "Chave PIX é obrigatória") String pixKey, 
                     @NotBlank(message = "Tipo da chave PIX é obrigatório") String pixKeyType, 
                     @NotNull(message = "Valor do pagamento é obrigatório") 
                     @DecimalMin(value = "0.01", message = "Valor do pagamento deve ser maior que zero") BigDecimal amount, 
                     LocalDateTime createdAt, 
                     PaymentStatus status,
                     @NotNull(message = "ID do cliente é obrigatório") UUID customerId, 
                     @NotBlank(message = "ID do pedido é obrigatório") String orderId) {
        this.id = id;
        this.pixKey = pixKey;
        this.pixKeyType = pixKeyType;
        this.amount = amount;
        this.createdAt = createdAt;
        this.status = status;
        this.customerId = customerId;
        this.orderId = orderId;
    }

    @Override
    public UUID id() {
        return id;
    }


    @Override
    public BigDecimal amount() {
        return amount;
    }

    @Override
    public LocalDateTime createdAt() {
        return createdAt;
    }

    @Override
    public PaymentStatus status() {
        return status;
    }

    @Override
    public UUID customerId() {
        return customerId;
    }

    @Override
    public String orderId() {
        return orderId;
    }

    @Override
    public PaymentType paymentType() {
        return paymentType;
    }
}
