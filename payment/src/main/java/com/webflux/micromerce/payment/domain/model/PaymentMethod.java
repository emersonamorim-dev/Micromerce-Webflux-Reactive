package com.webflux.micromerce.payment.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Table("payment")
@Document(indexName = "payments")
public sealed interface PaymentMethod permits BoletoPayment, CreditCardPayment, DebitCardPayment, PixPayment {
    @Id
    @Field(type = FieldType.Keyword)
    UUID id();

    @Column("amount")
    @Field(type = FieldType.Double)
    BigDecimal amount();

    @Column("created_at")
    @Field(type = FieldType.Date)
    LocalDateTime createdAt();

    @Column("status")
    @Field(type = FieldType.Keyword)
    PaymentStatus status();

    @Column("customer_id")
    @Field(type = FieldType.Keyword)
    UUID customerId();

    @Column("order_id")
    @Field(type = FieldType.Keyword)
    String orderId();

    @Column("payment_type")
    @Field(type = FieldType.Keyword)
    PaymentType paymentType();

    default PaymentMethod.Builder toBuilder() {
        return new Builder(this);
    }

    // Classe Builder genérica para todos os tipos de PaymentMethod
    class Builder {
        protected UUID id;
        protected BigDecimal amount;
        protected String beneficiario;
        protected LocalDateTime createdAt;
        protected PaymentStatus status;
        protected String pagador;
        protected UUID customerId;
        protected String orderId;
        protected PaymentType paymentType;

        public Builder() {
        }

        public Builder(PaymentMethod payment) {
            this.id = payment.id();
            this.amount = payment.amount();
            this.createdAt = payment.createdAt();
            this.status = payment.status();
            this.customerId = payment.customerId();
            this.orderId = payment.orderId();
            this.paymentType = payment.paymentType();
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder status(PaymentStatus status) {
            this.status = status;
            return this;
        }

        public Builder customerId(UUID customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder paymentType(PaymentType paymentType) {
            this.paymentType = paymentType;
            return this;
        }

        public PaymentMethod build() {
            switch (paymentType) {
                case CREDIT_CARD -> {
                    return new CreditCardPayment(
                            id,
                            "", // cardNumber
                            "", // cardHolderName
                            "", // cvv
                            amount,
                            createdAt,
                            status,
                            customerId,
                            orderId
                    );
                }
                case DEBIT_CARD -> {
                    return new DebitCardPayment(
                            id,
                            "", // cardNumber
                            "", // cardHolderName
                            amount,
                            createdAt,
                            status,
                            customerId,
                            orderId
                    );
                }
                case BOLETO -> {
                    return new BoletoPayment(
                            id,
                            "", // boletoNumber
                            amount,
                            beneficiario,
                            LocalDateTime.now().plusDays(3), // dueDate padrão de 3 dias
                            createdAt,
                            status,
                            pagador,
                            customerId,
                            orderId
                    );
                }
                case PIX -> {
                    return new PixPayment(
                            id,
                            "", // pixKey
                            "", // pixKeyType
                            amount,
                            createdAt,
                            status,
                            customerId,
                            orderId
                    );
                }
                default -> throw new IllegalArgumentException("Tipo de pagamento não suportado: " + paymentType);
            }
        }
    }
}