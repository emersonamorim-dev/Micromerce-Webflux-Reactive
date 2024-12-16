package com.webflux.micromerce.catalog.infrastructure.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "product-topic", groupId = "micromerce-group")
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }
}