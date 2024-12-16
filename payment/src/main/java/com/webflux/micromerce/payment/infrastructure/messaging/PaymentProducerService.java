package com.webflux.micromerce.payment.infrastructure.messaging;

import com.webflux.micromerce.payment.domain.model.PaymentMethod;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentProducerService {

    private final KafkaTemplate<String, PaymentMethod> kafkaTemplate;

    public PaymentProducerService(KafkaTemplate<String, PaymentMethod> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPayment(PaymentMethod payment) {
        // Cria um registro de produtor com o cabeçalho do tipo
        ProducerRecord<String, PaymentMethod> record = new ProducerRecord<>("payment-topic-processing", payment);
        record.headers().add("type", payment.getClass().getSimpleName().getBytes());

        // Envia a mensagem
        kafkaTemplate.send(record);
    }
}