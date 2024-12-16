package com.webflux.micromerce.payment.application.usecase;

import com.webflux.micromerce.payment.domain.model.PaymentMethodList;
import com.webflux.micromerce.payment.infrastructure.repository.PaymentRepository;
import com.webflux.micromerce.payment.presentation.exception.PaymentNotFoundException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class GetPaymentUseCase {
    private final PaymentRepository paymentRepository;

    public GetPaymentUseCase(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Mono<PaymentMethodList> execute(String id) {
        return paymentRepository.findPaymentById(UUID.fromString(id))
                .switchIfEmpty(Mono.error(new PaymentNotFoundException("Pagamento não encontrado com ID: " + id)));
    }

}

