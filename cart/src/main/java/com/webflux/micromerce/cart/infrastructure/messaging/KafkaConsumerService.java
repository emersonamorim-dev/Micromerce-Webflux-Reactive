package com.webflux.micromerce.cart.infrastructure.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "cart-topic", groupId = "micromerce-group")
    public void listen(String message) {
        System.out.println("Mensagem recebida: " + message);
    }
}