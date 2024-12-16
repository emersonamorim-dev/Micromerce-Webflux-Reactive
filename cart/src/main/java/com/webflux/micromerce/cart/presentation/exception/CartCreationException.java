package com.webflux.micromerce.cart.presentation.exception;

public class CartCreationException extends RuntimeException {
    public CartCreationException(String message) {
        super(message);
    }

    public CartCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
