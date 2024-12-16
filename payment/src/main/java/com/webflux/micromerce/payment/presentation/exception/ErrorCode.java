package com.webflux.micromerce.payment.presentation.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // Erros de Validação (4000-4099)
    INVALID_REQUEST_DATA("4000", "Dados da requisição inválidos"),
    MISSING_REQUIRED_FIELD("4001", "Campo obrigatório ausente"),
    INVALID_FIELD_FORMAT("4002", "Formato de campo inválido"),
    INVALID_FIELD_VALUE("4003", "Valor de campo inválido"),
    INVALID_PAYMENT_METHOD("4004", "Método de pagamento inválido"),
    INVALID_PAYMENT("4005", "Pagamento inválido"),
    INVALID_PAYMENT_STATUS("4006", "Status do pagamento inválido"),
    PAYMENT_VALIDATION_FAILED("4007", "Falha na validação do pagamento"),
    PAYMENT_DATA_INVALID("4008", "Dados do pagamento inválidos"),
    MISSING_PAYMENT_DATA("4009", "Dados do pagamento ausentes"),
    INVALID_PAYMENT_ID("4010", "ID do pagamento inválido"),
    ALREADY_CANCELLED("4011", "Pagamento já cancelado"),
    GATEWAY_ERROR("4012", "Erro no gateway de pagamento"),
    INVALID_PAYMENT_DATE("4013", "Data do pagamento inválida"),
    INVALID_MODIFICATION_DATE("4014", "Data de modificação inválida"),

    // Erros de Negócio (4100-4199)
    PAYMENT_NOT_FOUND("4100", "Pagamento não encontrado"),
    PAYMENT_ALREADY_PROCESSED("4101", "Pagamento já processado"),
    PAYMENT_EXPIRED("4102", "Pagamento expirado"),
    PAYMENT_ALREADY_CANCELLED("4103", "Pagamento já cancelado"),
    PAYMENT_CANNOT_BE_CANCELLED("4104", "Pagamento não pode ser cancelado"),
    INSUFFICIENT_FUNDS("4105", "Saldo insuficiente"),
    PAYMENT_UPDATE_ERROR("4106", "Erro ao atualizar pagamento"),
    PAYMENT_PROCESS_ERROR("4107", "Erro ao processar pagamento"),
    PAYMENT_PROCESSING_ERROR("4108", "Erro durante o processamento do pagamento"),

    // Erros de Gateway (4200-4299)
    GATEWAY_COMMUNICATION_ERROR("4200", "Erro de comunicação com gateway"),
    GATEWAY_TIMEOUT("4201", "Timeout na comunicação com gateway"),
    GATEWAY_INVALID_RESPONSE("4202", "Resposta inválida do gateway"),
    PAYMENT_GATEWAY_ERROR("4203", "Erro no gateway de pagamento"),
    PAYMENT_GATEWAY_UNAVAILABLE("4204", "Gateway de pagamento indisponível"),
    GATEWAY_PROCESSING_ERROR("4205", "Erro no processamento do gateway"),
    GATEWAY_VALIDATION_ERROR("4206", "Erro de validação no gateway"),

    // Erros de Sistema (5000-5099)
    INTERNAL_SERVER_ERROR("5000", "Erro interno do servidor"),
    DATABASE_ERROR("5001", "Erro de banco de dados"),
    UNEXPECTED_ERROR("5002", "Erro inesperado"),
    VALIDATION_ERROR("5003", "Erro de validação"),
    UNKNOWN_ERROR("5004", "Erro desconhecido");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
