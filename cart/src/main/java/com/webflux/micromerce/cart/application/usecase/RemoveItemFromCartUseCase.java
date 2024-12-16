package com.webflux.micromerce.cart.application.usecase;

import com.webflux.micromerce.cart.application.dto.response.CartResponse;
import com.webflux.micromerce.cart.application.mapper.CartMapper;
import com.webflux.micromerce.cart.domain.repository.CartRepository;
import com.webflux.micromerce.cart.presentation.exception.CartNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RemoveItemFromCartUseCase {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    public Mono<CartResponse> execute(String cartId, String itemId) {
        return cartRepository.findById(cartId)
                .switchIfEmpty(Mono.error(new CartNotFoundException(cartId)))
                .map(cart -> {
                    cart.removeItem(itemId);
                    return cart;
                })
                .flatMap(cartRepository::save)
                .map(cartMapper::toResponse);
    }
}
