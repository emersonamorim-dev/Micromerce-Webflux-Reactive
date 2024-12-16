package com.webflux.micromerce.cart.application.usecase;

import com.webflux.micromerce.cart.application.dto.request.PromoCodeRequest;
import com.webflux.micromerce.cart.application.dto.response.CartResponse;
import com.webflux.micromerce.cart.application.mapper.CartMapper;
import com.webflux.micromerce.cart.application.service.PromoService;
import com.webflux.micromerce.cart.domain.model.CartStatus;
import com.webflux.micromerce.cart.domain.repository.CartRepository;
import com.webflux.micromerce.cart.presentation.exception.CartNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyPromoCodeUseCase {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final PromoService promoService;

    public Mono<CartResponse> execute(String cartId, PromoCodeRequest request) {
        return cartRepository.findById(cartId)
                .switchIfEmpty(Mono.error(new CartNotFoundException(cartId)))
                .flatMap(cart -> {
                    if (cart.getStatus() != CartStatus.ACTIVE) {
                        return Mono.error(new IllegalStateException("O carrinho não está ativo"));
                    }
                    return promoService.validateAndCalculateDiscount(request.promoCode())
                            .doOnNext(discount -> {
                                cart.setPromoCode(request.promoCode());
                                cart.setDiscountAmount(discount);
                                cart.recalculateTotal();
                                log.info("Código promocional aplicado {} com desconto {} para carrinho {}",
                                    request.promoCode(), discount, cartId);
                            })
                            .thenReturn(cart);
                })
                .flatMap(cartRepository::save)
                .map(cartMapper::toResponse)
                .doOnError(error -> log.error("Erro ao aplicar o código promocional ao carrinho {}: {}",
                    cartId, error.getMessage()));
    }
}
