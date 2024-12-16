package com.webflux.micromerce.cart.presentation.exception;

public class EmptyCartException extends RuntimeException {
    public EmptyCartException(String cartId) {
        super(String.format("Não é possível completar o carrinho vazio com o ID: %s", cartId));
    }
}
