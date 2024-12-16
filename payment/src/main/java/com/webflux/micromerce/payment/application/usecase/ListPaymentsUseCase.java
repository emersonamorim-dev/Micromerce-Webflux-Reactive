package com.webflux.micromerce.payment.application.usecase;

import com.webflux.micromerce.payment.domain.model.PaymentMethodList;
import com.webflux.micromerce.payment.infrastructure.repository.PaymentRepository;
import com.webflux.micromerce.payment.presentation.exception.PaymentNotFoundException;
import com.webflux.micromerce.payment.presentation.exception.PaymentProcessingException;
import com.webflux.micromerce.payment.presentation.exception.PaymentValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListPaymentsUseCase {
    private final PaymentRepository paymentRepository;

    public Mono<PageImpl<PaymentMethodList>> findPayments(int page, int size) {
        // Ajusta valores negativos ou inválidos para evitar erros
        int safePage = Math.max(0, page);
        int safeSize = size <= 0 ? 20 : Math.min(size, 100); // Limita tamanho máximo da página

        log.debug("Iniciando busca paginada de pagamentos - página: {}, tamanho: {}", safePage, safeSize);

        return paymentRepository.count()
                .defaultIfEmpty(0L)
                .flatMap(total -> {
                    if (total == 0) {
                        log.info("Nenhum pagamento encontrado");
                        return Mono.just(new PageImpl<PaymentMethodList>(
                                new ArrayList<>(),
                                PageRequest.of(safePage, safeSize),
                                0
                        ));
                    }

                    return paymentRepository.findAllByPage(safeSize, safePage * safeSize)
                            .collectList()
                            .defaultIfEmpty(new ArrayList<>())
                            .map(content -> {
                                log.debug("Recuperados {} registros", content.size());
                                return new PageImpl<PaymentMethodList>(
                                        content,
                                        PageRequest.of(safePage, safeSize),
                                        total
                                );
                            });
                })
                .doOnSuccess(pageResult -> log.info(
                        "Busca paginada concluída - Total: {}, Página: {}, Tamanho: {}",
                        pageResult.getTotalElements(),
                        pageResult.getNumber(),
                        pageResult.getSize()
                ))
                .doOnError(error -> log.warn(
                        "Erro não crítico durante busca paginada: {}",
                        error.getMessage()
                ))
                .onErrorReturn(new PageImpl<>(
                        new ArrayList<>(),
                        PageRequest.of(safePage, safeSize),
                        0
                ));
    }

    public Flux<PaymentMethodList> getByOrderId(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            log.error("ID do pedido inválido ou vazio");
            return Flux.error(new PaymentValidationException("ID do pedido não pode ser vazio"));
        }

        log.debug("Buscando pagamentos para o pedido: {}", orderId);
        return paymentRepository.findByOrderId(orderId)
                .doOnComplete(() -> log.debug("Busca de pagamentos concluída para o pedido: {}", orderId))
                .doOnError(error -> log.error("Erro ao buscar pagamentos para o pedido {}: {}",
                        orderId, error.getMessage()))
                .switchIfEmpty(Mono.error(new PaymentNotFoundException(
                        String.format("Nenhum pagamento encontrado para o pedido: %s", orderId))))
                .onErrorMap(error -> {
                    if (error instanceof PaymentNotFoundException) {
                        return error;
                    }
                    return new PaymentProcessingException(
                            String.format("Erro ao processar busca de pagamentos para o pedido: %s", orderId),
                            error);
                });
    }

    public Flux<PaymentMethodList> getByCustomerId(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            log.error("ID do cliente inválido ou vazio");
            return Flux.error(new PaymentValidationException("ID do cliente não pode ser vazio"));
        }

        UUID customerUUID;
        try {
            customerUUID = UUID.fromString(customerId);
        } catch (IllegalArgumentException e) {
            log.error("ID do cliente inválido: {}", customerId);
            return Flux.error(new PaymentValidationException(
                    String.format("ID do cliente inválido: %s", customerId)));
        }

        log.debug("Buscando pagamentos para o cliente: {}", customerUUID);
        return paymentRepository.findByCustomerId(customerUUID)
                .doOnComplete(() -> log.debug("Busca de pagamentos concluída para o cliente: {}", customerUUID))
                .doOnError(error -> log.error("Erro ao buscar pagamentos para o cliente {}: {}",
                        customerUUID, error.getMessage()))
                .switchIfEmpty(Mono.error(new PaymentNotFoundException(
                        String.format("Nenhum pagamento encontrado para o cliente: %s", customerUUID))))
                .onErrorMap(error -> {
                    if (error instanceof PaymentNotFoundException ||
                            error instanceof PaymentValidationException) {
                        return error;
                    }
                    return new PaymentProcessingException(
                            String.format("Erro ao processar busca de pagamentos para o cliente: %s", customerUUID),
                            error);
                });
    }
}

