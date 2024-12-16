package com.webflux.micromerce.payment.domain.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.relational.core.mapping.Column;

import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class BasePayment {
    @Id
    @Field(type = FieldType.Keyword)
    private UUID id;

    @Column("amount")
    @Field(type = FieldType.Double)
    private BigDecimal amount;

    @Column("created_at")
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    @Column("status")
    @Field(type = FieldType.Keyword)
    private PaymentStatus status;

    @Column("customer_id")
    @Field(type = FieldType.Keyword)
    private UUID customerId;

    @Column("order_id")
    @Field(type = FieldType.Keyword)
    private String orderId;

    @Column("payment_type")
    @Field(type = FieldType.Keyword)
    private PaymentType paymentType;
}
