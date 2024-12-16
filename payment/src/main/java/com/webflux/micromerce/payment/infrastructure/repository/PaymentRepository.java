package com.webflux.micromerce.payment.infrastructure.repository;

import com.webflux.micromerce.payment.domain.model.PaymentMethod;
import com.webflux.micromerce.payment.domain.model.PaymentMethodList;
import com.webflux.micromerce.payment.domain.model.PaymentStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;


public interface PaymentRepository extends R2dbcRepository<PaymentMethod, UUID> {


    @Query("""
        SELECT 
            p.id, 
            p.amount, 
            p.beneficiario,
            p.created_at as createdAt, 
            p.status, 
            p.pagador,
            p.customer_id as customerId, 
            p.order_id as orderId,
            p.payment_type as paymentType,
            p.card_number as cardNumber,
            p.card_holder_name as cardHolderName,
            p.cvv,
            p.boleto_number as boletoNumber,
            p.due_date as dueDate,
            p.pix_key as pixKey,
            p.pix_key_type as pixKeyType
        FROM payment p
        WHERE (:status IS NULL OR p.status = :status)
        AND (:startDate IS NULL OR p.created_at >= :startDate)
        AND (:endDate IS NULL OR p.created_at <= :endDate)
        ORDER BY p.created_at DESC
        LIMIT :size OFFSET :offset
        """)

    Flux<PaymentMethod> findByStatusAndCreatedAtBetween(
            PaymentStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int size,
            long offset
    );

    @Query("""
        SELECT COUNT(*) FROM payment p
        WHERE (:status IS NULL OR p.status = :status)
        AND (:startDate IS NULL OR p.created_at >= :startDate)
        AND (:endDate IS NULL OR p.created_at <= :endDate)
        """)
    Mono<Long> countByStatusAndCreatedAtBetween(

            @Nullable PaymentStatus status,
            @Nullable LocalDateTime startDate,
            @Nullable LocalDateTime endDate
    );



    @Query("SELECT p.* FROM payment p WHERE p.customer_id = :customerId")
    Flux<PaymentMethodList> findByCustomerId(UUID customerId);

    @Query("SELECT COUNT(*) FROM payment p WHERE p.customer_id = :customerId")
    Mono<Long> countByCustomerId(UUID customerId);

    @Query("SELECT p.* FROM payment p WHERE p.order_id = :orderId")
    Flux<PaymentMethodList> findByOrderId(String orderId);

    @Query("SELECT COUNT(*) FROM payment p WHERE p.order_id = :orderId")
    Mono<Long> countByOrderId(String orderId);

    @Query("""
        SELECT p.* FROM payment p
        WHERE p.payment_type = :paymentType
        AND p.created_at BETWEEN :startDate AND :endDate
        """)
    Flux<PaymentMethod> findByPaymentTypeAndCreatedAtBetween(
            @Nullable String paymentType,
            @Nullable LocalDateTime startDate,
            @Nullable LocalDateTime endDate
    );

    @Query("""
    SELECT p.id, p.amount, p.beneficiario, p.created_at as createdAt, p.status, p.pagador, p.customer_id as customerId, p.order_id as orderId, p.payment_type as paymentType,
           CASE
               WHEN p.payment_type = 'CREDIT_CARD' THEN p.card_number
               WHEN p.payment_type = 'DEBIT_CARD' THEN p.card_number
               WHEN p.payment_type = 'BOLETO' THEN p.boleto_number
               WHEN p.payment_type = 'PIX' THEN p.pix_key
           END as payment_identifier,
           p.card_holder_name, p.cvv, p.due_date, p.pix_key_type
    FROM payment p
    ORDER BY p.created_at DESC
    LIMIT :limit OFFSET :offset
""")
    Flux<PaymentMethodList> findAllByPage(@Param("size") int size, @Param("offset") int offset);

    @Query("""
    SELECT 
        p.id, 
        p.amount, 
        p.beneficiario,
        p.created_at as createdAt, 
        p.status, 
        p.pagador,
        p.customer_id as customerId, 
        p.order_id as orderId,
        p.payment_type as paymentType,
        p.card_number as cardNumber,
        p.card_holder_name as cardHolderName,
        p.cvv,
        p.boleto_number as boletoNumber,
        p.due_date as dueDate,
        p.pix_key as pixKey,
        p.pix_key_type as pixKeyType
    FROM payment p
    WHERE p.id = :id
    """)
    Mono<PaymentMethodList> findPaymentById(UUID id);

    @Query("""
    SELECT 
        p.id, 
        p.amount, 
        p.beneficiario,
        p.created_at as createdAt, 
        p.status, 
        p.pagador,
        p.customer_id as customerId, 
        p.order_id as orderId,
        p.payment_type as paymentType,
        p.card_number as cardNumber,
        p.card_holder_name as cardHolderName,
        p.cvv,
        p.boleto_number as boletoNumber,
        p.due_date as dueDate,
        p.pix_key as pixKey,
        p.pix_key_type as pixKeyType
    FROM payment p 
    WHERE p.id = :id AND status IN ('PROCESSING', 'COMPLETED')
    """)
    Mono<PaymentMethodList> findByIdRefund(UUID id);

    @Query("""
    SELECT 
        p.id, 
        p.amount, 
        p.beneficiario,
        p.created_at as createdAt, 
        p.status, 
        p.pagador,
        p.customer_id as customerId, 
        p.order_id as orderId,
        p.payment_type as paymentType,
        p.card_number as cardNumber,
        p.card_holder_name as cardHolderName,
        p.cvv,
        p.boleto_number as boletoNumber,
        p.due_date as dueDate,
        p.pix_key as pixKey,
        p.pix_key_type as pixKeyType
    FROM payment p 
    WHERE p.id = :id AND status IN ('PROCESSING', 'COMPLETED')
    """)
    Mono<PaymentMethodList> findByIdCancel(UUID id);


}

