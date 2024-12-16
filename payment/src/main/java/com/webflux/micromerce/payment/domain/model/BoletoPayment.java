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
public final class BoletoPayment implements PaymentMethod {
    @Setter
    @Id
    @Field(type = FieldType.Keyword)
    private UUID id;

    @Setter
    @Column("amount")
    @Field(type = FieldType.Double)
    private BigDecimal amount;

    @Setter
    @Column("beneficiario")
    @Field(type = FieldType.Double)
    private @NotBlank(message = "beneficiario é obrigatório")
    String beneficiario;

    @Setter
    @Column("created_at")
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    @Setter
    @Column("status")
    @Field(type = FieldType.Keyword)
    private PaymentStatus status;

    @Setter
    @Column("pagador")
    @Field(type = FieldType.Double)
    @NotBlank(message = "pagador é obrigatório")
    public String pagador;

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
    @Column("boleto_number")
    @Field(type = FieldType.Keyword)
    private String boletoNumber;

    @Getter
    @Column("due_date")
    @Field(type = FieldType.Date)
    private LocalDateTime dueDate;

    @Column("payment_type")
    @Field(type = FieldType.Keyword)
    private PaymentType paymentType = PaymentType.BOLETO;

    public BoletoPayment() {
    }

    public BoletoPayment(UUID id,
                         @NotBlank(message = "Número do boleto é obrigatório") String boletoNumber,
                         @NotNull(message = "Valor do pagamento é obrigatório")
                         @DecimalMin(value = "0.01", message = "Valor do pagamento deve ser maior que zero") BigDecimal amount,
                         @NotBlank(message = "beneficiario é obrigatório") String beneficiario,
                         @NotNull(message = "Data de vencimento é obrigatória") LocalDateTime dueDate,
                         LocalDateTime createdAt,
                         PaymentStatus status,
                         @NotBlank(message = "pagador é obrigatório") String pagador,
                         @NotNull(message = "ID do cliente é obrigatório") UUID customerId,
                         @NotBlank(message = "ID do pedido é obrigatório") String orderId) {
        this.id = id;
        this.boletoNumber = boletoNumber;
        this.amount = amount;
        this.beneficiario = beneficiario;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
        this.status = status;
        this.pagador = pagador;
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



    public String beneficiario() {
        return beneficiario;
    }

    public String pagador() {
        return pagador;
    }
}
