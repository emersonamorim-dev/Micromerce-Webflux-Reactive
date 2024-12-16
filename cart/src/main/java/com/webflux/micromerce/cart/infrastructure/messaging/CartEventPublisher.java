package com.webflux.micromerce.cart.infrastructure.messaging;

import com.webflux.micromerce.cart.application.dto.response.CartResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CartEventPublisher {
    
    private final KafkaTemplate<String, CartResponse> kafkaTemplate;
    
    @Value("${spring.kafka.topics.cart-events}")
    private String cartEventsTopic;

    public CartEventPublisher(KafkaTemplate<String, CartResponse> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public Mono<Void> publishCartEvent(CartResponse cartResponse) {
        return Mono.fromRunnable(() -> {
            try {
                kafkaTemplate.send(cartEventsTopic, cartResponse.id(), cartResponse)
                        .toCompletableFuture()
                        .whenComplete((result, ex) -> {
                            if (ex == null) {
                                log.info("Evento de carrinho publicado com sucesso para carrinho: {}", cartResponse.id());
                            } else {
                                log.error("Falha ao publicar evento de carrinho para carrinho {}: {}",
                                    cartResponse.id(), ex.getMessage());
                            }
                        });
            } catch (Exception ex) {
                log.error("Erro ao tentar publicar evento de carrinho para carrinho {}: {}",
                    cartResponse.id(), ex.getMessage());
            }
        });
    }
}
