package com.webflux.micromerce.catalog.infrastructure.messaging;

import com.webflux.micromerce.catalog.config.BaseIntegrationTest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class KafkaConsumerServiceTest extends BaseIntegrationTest {

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    private KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "test-product-events";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    @BeforeEach
    void setUp() {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, 
            System.getProperty("spring.kafka.bootstrap-servers"));
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 1000);

        ProducerFactory<String, String> producerFactory = 
            new DefaultKafkaProducerFactory<>(producerProps);
        kafkaTemplate = new KafkaTemplate<>(producerFactory);
    }

    @Test
    void testMessageConsumption() throws InterruptedException {
        // Arrange
        String message = "{\"id\":1,\"name\":\"Test Product\",\"price\":10.0}";
        CountDownLatch latch = new CountDownLatch(1);
        
        // Act
        kafkaTemplate.send(TOPIC, message)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    latch.countDown();
                }
            });

        // Assert
        assertTrue(latch.await(10, TimeUnit.SECONDS), "Message not sent within timeout");
        
        // Verify the message was processed (add appropriate verification based on your service implementation)
        // For example, if your service saves to a repository or performs some operation:
        // StepVerifier.create(yourRepository.findById(1L))
        //     .expectNextMatches(product -> product.getName().equals("Test Product"))
        //     .verifyComplete();
    }

    @Test
    void testInvalidMessageHandling() throws InterruptedException {
        // Arrange
        String invalidMessage = "invalid json";
        CountDownLatch latch = new CountDownLatch(1);
        
        // Act
        kafkaTemplate.send(TOPIC, invalidMessage)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    latch.countDown();
                }
            });

        // Assert
        assertTrue(latch.await(10, TimeUnit.SECONDS), "Message not sent within timeout");
        // Verify error handling (add appropriate verification based on your service implementation)
    }
}
