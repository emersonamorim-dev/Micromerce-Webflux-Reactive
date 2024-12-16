package com.webflux.micromerce.cart.presentation.exception;

import com.webflux.micromerce.cart.domain.model.CartStatus;

public class InvalidCartStateException extends RuntimeException {
    public InvalidCartStateException(String cartId, CartStatus status) {
        super(String.format("Carrinho %s não está no estado ATIVO. Estado atual: %s", cartId, status));
    }
}
