package com.webflux.micromerce.cart.application.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PromoService {
    private final Map<String, BigDecimal> promoDiscounts = new ConcurrentHashMap<>();

    public PromoService() {
        // Inicialize alguns códigos promocionais de amostra
        promoDiscounts.put("WELCOME18", new BigDecimal("18.00"));
        promoDiscounts.put("SAVE28", new BigDecimal("28.00"));
    }

    public Mono<BigDecimal> validateAndCalculateDiscount(String promoCode) {
        return Mono.justOrEmpty(promoDiscounts.get(promoCode))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Código promocional inválido")));
    }

    public Mono<Void> addPromoCode(String code, BigDecimal discount) {
        return Mono.fromRunnable(() -> promoDiscounts.put(code, discount));
    }

    public Mono<Void> removePromoCode(String code) {
        return Mono.fromRunnable(() -> promoDiscounts.remove(code));
    }
}
