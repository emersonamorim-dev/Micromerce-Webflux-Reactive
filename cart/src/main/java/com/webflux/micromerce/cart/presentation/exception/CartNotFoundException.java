package com.webflux.micromerce.cart.presentation.exception;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String cartId) {
        super("Carrinho não encontrado com id: " + cartId);
    }
}
