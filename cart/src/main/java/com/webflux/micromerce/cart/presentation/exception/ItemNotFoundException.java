package com.webflux.micromerce.cart.presentation.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String productId) {
        super(String.format("Item com ID do produto %s não encontrado no carrinho", productId));
    }
}
