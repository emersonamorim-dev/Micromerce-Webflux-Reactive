package com.webflux.micromerce.payment.presentation.controllers;

import com.webflux.micromerce.payment.application.dto.PaymentMethodDTO;
import com.webflux.micromerce.payment.application.dto.request.*;
import com.webflux.micromerce.payment.application.dto.response.PageResponse;
import com.webflux.micromerce.payment.application.usecase.*;
import com.webflux.micromerce.payment.domain.model.PaymentMethod;
import com.webflux.micromerce.payment.domain.model.PaymentMethodList;
import com.webflux.micromerce.payment.presentation.exception.PaymentNotFoundException;
import com.webflux.micromerce.payment.presentation.exception.PaymentProcessingException;
import com.webflux.micromerce.payment.presentation.exception.PaymentValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Payment", description = "Processamento de pagamento API")
@Validated
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final RefundPaymentUseCase refundPaymentUseCase;
    private final CancelPaymentUseCase cancelPaymentUseCase;
    private final GetPaymentUseCase getPaymentUseCase;
    private final ListPaymentsUseCase listPaymentsUseCase;


    @Value("${payment.retry.max-attempts:3}")
    private int maxRetryAttempts;

    @Value("${payment.retry.initial-delay:100}")
    private long initialRetryDelay;

    @Value("${payment.stream.timeout:30}")
    private long streamTimeoutSeconds;

    @Operation(summary = "Processar um novo pagamento", description = "Cria e processa uma nova transação de pagamento")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pagamento processado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Solicitação de pagamento inválida"),
        @ApiResponse(responseCode = "500", description = "Erro do Servidor Interno")
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<PaymentMethod>> processPayment(
            @Parameter(description = "Detalhes da solicitação de pagamento", required = true)
            @Valid @RequestBody ProcessPaymentRequest request) {
        log.info("Iniciando o processamento do pagamento: {}", request);
        return processPaymentUseCase.execute(request)
            .map(payment -> ResponseEntity.status(HttpStatus.CREATED).body(payment))
            .doOnSuccess(response -> log.info("Pagamento processado com sucesso: {}", response.getBody()))
            .doOnError(PaymentValidationException.class,
                error -> log.error("Erro de validação de pagamento: {}", error.getMessage()))
            .doOnError(PaymentProcessingException.class,
                error -> log.error("Erro de processamento de pagamento: {}", error.getMessage()))
            .retryWhen(Retry.backoff(maxRetryAttempts, Duration.ofMillis(initialRetryDelay))
                .filter(throwable -> !(throwable instanceof PaymentValidationException)))
            .onErrorResume(PaymentValidationException.class,
                error -> Mono.just(ResponseEntity.badRequest().build()))
            .onErrorResume(PaymentProcessingException.class,
                error -> Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build()));
    }


    @Operation(summary = "Listar todos os pagamentos", description = "Recupera uma lista de todos os pagamentos")
    @ApiResponse(responseCode = "200", description = "Lista de pagamentos recuperados com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro do Servidor Interno")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PageResponse<PaymentMethodDTO>>> listPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Listagem de pagamentos - page: {}, size: {}", page, size);

        return listPaymentsUseCase.findPayments(page, size)
                .map(paymentPage -> {
                    List<PaymentMethodDTO> dtos = paymentPage.getContent()
                            .stream()
                            .map(PaymentMethodDTO::from)
                            .collect(Collectors.toList());

                    PageResponse<PaymentMethodDTO> response = new PageResponse<>();
                    response.setContent(dtos);
                    response.setPageInfo(PageResponse.PageInfo.from(paymentPage));

                    return ResponseEntity.ok(response);
                })
                .doOnSuccess(result -> log.info("Lista de pagamento recuperada com sucesso"))
                .doOnError(error -> log.error("Erro ao recuperar pagamentos: {}",
                        error.getMessage() != null ? error.getMessage() : "Erro desconhecido"));
    }

    @Operation(summary = "Get pagamento por ID", description = "Recupera detalhes de pagamento por seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagamento encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado"),
            @ApiResponse(responseCode = "400", description = "Formato UUID inválido")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaymentMethodList>> getPayment(@PathVariable String id) {
        return getPaymentUseCase.execute(id)
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, e -> {
                    log.error("Formato UUID inválido: {}", id);
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(PaymentNotFoundException.class, e -> {
                    log.error("Pagamento não encontrado: {}", e.getMessage());
                    return Mono.just(ResponseEntity.notFound().build());
                })
                .doOnSuccess(response -> log.debug("Resposta enviada com status: {}", response.getStatusCode()));
    }



    @Operation(summary = "Reembolso de pagamento", description = "Processar um reembolso para um pagamento existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reembolso processado com sucesso",
                    content = @Content(schema = @Schema(implementation = PaymentMethod.class))),
            @ApiResponse(responseCode = "400", description = "Solicitação de reembolso inválida"),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado"),
            @ApiResponse(responseCode = "503", description = "Serviço não disponível")
    })
    @PostMapping(value = "/{id}/refund", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaymentMethodList>> refundPayment(
            @Parameter(description = "ID de pagamento para reembolso", required = true)
            @PathVariable String id) {
        log.info("Iniciando o reembolso do pagamento para ID: {}", id);
        return refundPaymentUseCase.execute(UUID.fromString(id))
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Reembolso processado com sucesso: {}", response.getBody()))
                .doOnError(PaymentNotFoundException.class,
                        error -> log.error("Pagamento não encontrado para reembolso: {}", error.getMessage()))
                .doOnError(PaymentProcessingException.class,
                        error -> log.error("Erro de processamento de reembolso: {}", error.getMessage()))
                .retryWhen(Retry.backoff(maxRetryAttempts, Duration.ofMillis(initialRetryDelay))
                        .filter(throwable -> throwable instanceof PaymentProcessingException))
                .onErrorResume(PaymentNotFoundException.class,
                        error -> Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(PaymentProcessingException.class,
                        error -> Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build()))
                .onErrorResume(Exception.class,
                        error -> {
                            log.error("Erro inesperado: {}", error.getMessage(), error);
                            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                        });
    }



    @Operation(summary = "Cancelar pagamento", description = "Cancela um pagamento existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagamento cancelado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado"),
        @ApiResponse(responseCode = "400", description = "O pagamento não pode ser cancelado")
    })
    @PostMapping(value = "/{id}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaymentMethodList>> cancelPayment(
            @Parameter(description = "ID de pagamento para cancelar", required = true)
            @PathVariable String id) {
        log.info("Iniciando cancelamento de pagamento para ID: {}", id);
        return cancelPaymentUseCase.execute(UUID.fromString(id))
            .map(ResponseEntity::ok)
            .doOnSuccess(response -> log.info("Pagamento cancelado com sucesso: {}", response.getBody()))
            .doOnError(PaymentNotFoundException.class,
                error -> log.error("Pagamento não encontrado para cancelamento: {}", error.getMessage()))
            .doOnError(PaymentProcessingException.class,
                error -> log.error("Erro de processamento de cancelamento: {}", error.getMessage()))
            .retryWhen(Retry.backoff(maxRetryAttempts, Duration.ofMillis(initialRetryDelay))
                .filter(throwable -> throwable instanceof PaymentProcessingException))
            .onErrorResume(PaymentNotFoundException.class,
                error -> Mono.just(ResponseEntity.notFound().build()))
            .onErrorResume(PaymentProcessingException.class,
                error -> Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build()));
    }


    @Operation(summary = "Get pagamentos por encomenda ID", description = "Recupera uma lista de pagamentos para um pedido")
    @ApiResponse(responseCode = "200", description = "Lista de pagamentos recuperados com sucesso")
    @GetMapping(value = "/order/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Flux<PaymentMethodList>>> getPaymentsByOrderId(
            @Parameter(description = "Order ID", required = true)
            @PathVariable String orderId) {
        log.info("Buscando pagamentos para pedido: {}", orderId);
        return Mono.just(ResponseEntity.ok(listPaymentsUseCase.getByOrderId(orderId)
            .doOnComplete(() -> log.info("Pedidos de pagamento recuperados com sucesso: {}", orderId))
            .doOnError(IllegalArgumentException.class,
                error -> log.error("ID do pedido inválido: {}", error.getMessage()))
            .onErrorResume(IllegalArgumentException.class,
                error -> Flux.error(new PaymentValidationException("ID do pedido inválido")))
        ));
    }

    @Operation(summary = "Get pagamentos por cliente ID", description = "Recupera uma lista de pagamentos para um cliente")
    @ApiResponse(responseCode = "200", description = "Lista de pagamentos recuperados com sucesso")
    @GetMapping(value = "/customer/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Flux<PaymentMethodList>>> getPaymentsByCustomerId(
            @Parameter(description = "Cliente ID", required = true)
            @PathVariable String customerId) {
        log.info("Buscando pagamentos para o cliente: {}", customerId);
        return Mono.just(ResponseEntity.ok(listPaymentsUseCase.getByCustomerId(customerId)
                .doOnComplete(() -> log.info("Pagamentos de clientes recuperados com sucesso: {}", customerId))
                .doOnError(IllegalArgumentException.class,
                        error -> log.error("Cliente inválido ID: {}", error.getMessage()))
                .onErrorResume(IllegalArgumentException.class,
                        error -> Flux.error(new PaymentValidationException("Cliente inválido ID")))
        ));
    }

    @ExceptionHandler(PaymentValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<String>> handleValidationError(PaymentValidationException ex) {
        return Mono.just(ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ex.getMessage()));
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ResponseEntity<String>> handleNotFoundError(PaymentNotFoundException ex) {
        return Mono.just(ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ex.getMessage()));
    }

    @ExceptionHandler(PaymentProcessingException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Mono<ResponseEntity<String>> handleProcessingError(PaymentProcessingException ex) {
        return Mono.just(ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ResponseEntity<String>> handleGenericError(Exception ex) {
        log.error("Erro inesperado: ", ex);
        return Mono.just(ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Ocorreu um erro inesperado"));
    }
}
