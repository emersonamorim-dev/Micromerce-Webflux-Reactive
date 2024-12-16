package com.webflux.micromerce.cart.presentation.exception;

public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException(int quantity) {
        super(String.format("Quantidade inválida: %d. A quantidade deve ser maior que 0", quantity));
    }
}
