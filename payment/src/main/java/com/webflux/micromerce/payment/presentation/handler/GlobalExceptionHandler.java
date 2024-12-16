package com.webflux.micromerce.payment.presentation.handler;

import com.webflux.micromerce.payment.presentation.exception.ErrorCode;
import com.webflux.micromerce.payment.presentation.exception.ErrorResponse;
import com.webflux.micromerce.payment.presentation.exception.PaymentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handlePaymentException(PaymentException ex, ServerWebExchange exchange) {
        log.error("Ocorreu uma exceção de pagamento: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(ex.getStatus().value())
            .code(ex.getErrorCode().getCode())
            .error(ex.getStatus().getReasonPhrase())
            .message(ex.getTitle())
            .detail(ex.getDetails())
            .path(exchange.getRequest().getPath().value())
            .build();

        return Mono.just(ResponseEntity
            .status(ex.getStatus())
            .body(errorResponse));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(WebExchangeBindException ex, ServerWebExchange exchange) {
        log.error("Ocorreu uma exceção de validação: {}", ex.getMessage(), ex);
        
        ErrorResponse.ErrorResponseBuilder responseBuilder = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .code(ErrorCode.INVALID_REQUEST_DATA.getCode())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message("Erro de validação")
            .path(exchange.getRequest().getPath().value());

        ex.getBindingResult().getFieldErrors().forEach(error ->
            responseBuilder.validationError(
                error.getField(),
                error.getDefaultMessage(),
                ErrorCode.INVALID_FIELD_VALUE.getCode()
            )
        );

        return Mono.just(ResponseEntity
            .badRequest()
            .body(responseBuilder.build()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ResponseEntity<ErrorResponse>> handleUnexpectedException(Exception ex, ServerWebExchange exchange) {
        log.error("Ocorreu uma exceção inesperada: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message("Erro interno do servidor")
            .detail("Um erro inesperado ocorreu. Por favor, tente novamente mais tarde.")
            .path(exchange.getRequest().getPath().value())
            .build();

        return Mono.just(ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse));
    }
}
