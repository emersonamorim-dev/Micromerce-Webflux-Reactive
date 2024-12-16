package com.webflux.micromerce.cart.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webflux.micromerce.cart.application.dto.response.CartResponse;
import com.webflux.micromerce.cart.domain.model.Cart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

@Service
@Slf4j
public class CartEventProducer {

    private final ReactiveKafkaProducerTemplate<String, CartResponse> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String cartTopic;

    public CartEventProducer(
            @Qualifier("cartResponseKafkaTemplate") ReactiveKafkaProducerTemplate<String, CartResponse> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${spring.kafka.topics.cart-events}") String cartTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.cartTopic = cartTopic;
    }

    public Mono<SenderResult<Void>> publishCartEvent(Cart cart) {
        return Mono.just(cart)
                .map(CartResponse::fromDomain)
                .flatMap(this::sendCartEvent)
                .doOnSuccess(result -> log.info("Evento de carrinho publicado com sucesso para ID de carrinho: {} com offset: {}",
                    cart.getId(), result.recordMetadata().offset()))
                .doOnError(error -> log.error("Erro ao publicar evento de carrinho para ID de carrinho: {}", cart.getId(), error));
    }

    private Mono<SenderResult<Void>> sendCartEvent(CartResponse cartResponse) {
        log.info("Enviando evento de carrinho para tópico {}: {}", cartTopic, cartResponse);

        return kafkaTemplate.send(cartTopic, cartResponse.id(), cartResponse)
                .doOnSuccess(result -> log.info("Evento de carrinho enviado com sucesso: offset={}",
                        result.recordMetadata().offset()))
                .doOnError(error -> log.error("Erro ao enviar evento de carrinho: {}", error.getMessage()));
    }
}
