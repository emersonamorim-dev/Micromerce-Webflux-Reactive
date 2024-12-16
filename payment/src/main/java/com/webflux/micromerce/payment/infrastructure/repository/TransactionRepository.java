package com.webflux.micromerce.payment.infrastructure.repository;

import com.webflux.micromerce.payment.application.dto.response.RefundResponse;
import com.webflux.micromerce.payment.domain.model.CreditCardPayment; // Importar a classe concreta
import com.webflux.micromerce.payment.domain.model.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TransactionRepository {

    private final PaymentRepository paymentRepository;

    public Mono<Void> saveRefundTransaction(RefundResponse response) {
        return Mono.fromRunnable(() -> {
            if (response.isSuccessful()) {
                log.info("Transação de reembolso salva: {}", response.transactionId());
            } else {
                log.error("Falha ao salvar transação de reembolso: {}", response.failureReason());
            }
        });
    }

}