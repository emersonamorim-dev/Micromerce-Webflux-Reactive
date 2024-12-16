package com.webflux.micromerce.payment.presentation.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timestamp,
    
    int status,
    
    String code,
    
    String error,
    
    String message,
    
    String detail,
    
    String path,
    
    List<ValidationError> validationErrors
) {
    public ErrorResponse {
        if (validationErrors == null) {
            validationErrors = new ArrayList<>();
        }
    }

    public record ValidationError(
        String field,
        String message,
        String code
    ) {}

    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder();
    }

    public static class ErrorResponseBuilder {
        private LocalDateTime timestamp = LocalDateTime.now();
        private int status;
        private String code;
        private String error;
        private String message;
        private String detail;
        private String path;
        private List<ValidationError> validationErrors = new ArrayList<>();

        public ErrorResponseBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ErrorResponseBuilder status(int status) {
            this.status = status;
            return this;
        }

        public ErrorResponseBuilder code(String code) {
            this.code = code;
            return this;
        }

        public ErrorResponseBuilder error(String error) {
            this.error = error;
            return this;
        }

        public ErrorResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ErrorResponseBuilder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public ErrorResponseBuilder path(String path) {
            this.path = path;
            return this;
        }

        public void validationError(String field, String message, String code) {
            this.validationErrors.add(new ValidationError(field, message, code));
        }

        public ErrorResponseBuilder validationErrors(List<ValidationError> validationErrors) {
            this.validationErrors = validationErrors;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(
                timestamp,
                status,
                code,
                error,
                message,
                detail,
                path,
                validationErrors
            );
        }
    }
}
