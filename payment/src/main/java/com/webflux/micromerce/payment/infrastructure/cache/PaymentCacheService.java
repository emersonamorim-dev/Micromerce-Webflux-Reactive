package com.webflux.micromerce.payment.infrastructure.cache;

import com.webflux.micromerce.payment.domain.model.PaymentMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

public class PaymentCacheService {
    private static final Logger log = LoggerFactory.getLogger(PaymentCacheService.class);
    private final ReactiveRedisTemplate<String, PaymentMethod> redisTemplate;
    private static final Duration CACHE_TTL = Duration.ofHours(1);
    private static final Duration RETRY_BACKOFF = Duration.ofMillis(100);
    private static final int MAX_RETRIES = 3;

    public PaymentCacheService(ReactiveRedisTemplate<String, PaymentMethod> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Boolean> cachePayment(PaymentMethod payment) {
        String key = getCacheKey(payment);
        log.debug("Caching pagamento com key: {}", key);
        
        return redisTemplate.opsForValue()
            .set(key, payment, CACHE_TTL)
            .retryWhen(Retry.backoff(MAX_RETRIES, RETRY_BACKOFF)
                .filter(throwable -> !(throwable instanceof IllegalArgumentException)))
            .doOnSuccess(result -> log.debug("Pagamento armazenado em cache com sucesso: {}", key))
            .doOnError(error -> log.error("Erro ao armazenar em cache o pagamento {}: {}", key, error.getMessage()))
            .onErrorResume(error -> {
                log.warn("Falha ao armazenar o pagamento em cache após novas tentativas: {}", key);
                return Mono.just(false);
            });
    }

    public Mono<PaymentMethod> getCachedPayment(String paymentId) {
        String key = getCacheKey(paymentId);
        log.debug("Obtendo pagamento do cache com key: {}", key);
        
        return redisTemplate.opsForValue().get(key)
            .retryWhen(Retry.backoff(MAX_RETRIES, RETRY_BACKOFF)
                .filter(throwable -> !(throwable instanceof IllegalArgumentException)))
            .doOnSuccess(payment -> {
                if (payment != null) {
                    log.debug("Cache atingido por key: {}", key);
                } else {
                    log.debug("Falta de cache para key: {}", key);
                }
            })
            .doOnError(error -> log.error("Erro ao buscar de cache {}: {}", key, error.getMessage()))
            .onErrorResume(error -> {
                log.warn("Falha ao buscar no cache após novas tentativas: {}", key);
                return Mono.empty();
            });
    }

    public Mono<Void> save(PaymentMethod payment) {
        return cachePayment(payment)
            .then(Mono.empty())
            .doOnSuccess(v -> log.debug("Operação de salvamento concluída para pagamento: {}", payment.id()))
            .doOnError(error -> log.error("Erro na operação de salvamento para pagamento {}: {}",
                payment.id(), error.getMessage())).then();
    }

    private String getCacheKey(PaymentMethod payment) {
        return getCacheKey(payment.id().toString());
    }

    private String getCacheKey(String paymentId) {
        return "payment:" + paymentId;
    }
}
