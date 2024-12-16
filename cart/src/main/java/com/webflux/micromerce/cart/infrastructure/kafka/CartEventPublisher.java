package com.webflux.micromerce.cart.infrastructure.kafka;

import com.webflux.micromerce.cart.domain.event.CartCreatedEvent;
import com.webflux.micromerce.cart.domain.model.Cart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

@Slf4j
@Component("kafkaCartEventPublisher")
public class CartEventPublisher {

    private final ReactiveKafkaProducerTemplate<String, CartCreatedEvent> kafkaTemplate;
    private final String cartEventsTopic;

    public CartEventPublisher(
            @Qualifier("cartEventKafkaTemplate") ReactiveKafkaProducerTemplate<String, CartCreatedEvent> kafkaTemplate,
            @Value("${spring.kafka.topics.cart-events}") String cartEventsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.cartEventsTopic = cartEventsTopic;
    }

    public Mono<SenderResult<Void>> publishCartCreatedEvent(Cart cart) {
        CartCreatedEvent event = CartCreatedEvent.fromCart(cart);
        
        log.info("Publicando evento de criação de carrinho: {} no tópico: {}", event.getEventId(), cartEventsTopic);
        
        return kafkaTemplate.send(cartEventsTopic, event.getCartId(), event)
                .doOnSuccess(result -> 
                    log.info("Evento de criação de carrinho publicado com sucesso: {} no tópico: {} no offset: {}", 
                        event.getEventId(), 
                        cartEventsTopic,
                        result.recordMetadata().offset()))
                .doOnError(error -> 
                    log.error("Falha ao publicar evento de criação de carrinho: {} no tópico: {}. Erro: {}", 
                        event.getEventId(), 
                        cartEventsTopic,
                        error.getMessage()));
    }
}
