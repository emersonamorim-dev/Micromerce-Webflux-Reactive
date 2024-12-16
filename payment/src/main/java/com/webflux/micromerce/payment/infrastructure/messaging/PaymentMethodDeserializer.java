package com.webflux.micromerce.payment.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webflux.micromerce.payment.domain.model.PaymentMethod;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentMethodDeserializer implements Deserializer<PaymentMethod> {

    private final ObjectMapper objectMapper;

    public PaymentMethodDeserializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Configurações podem ser feitas aqui, se necessário
    }

    @Override
    public PaymentMethod deserialize(String topic, byte[] data) {
        try {
            // Deserializa o JSON para o objeto PaymentMethod
            return objectMapper.readValue(data, PaymentMethod.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deserializar PaymentMethod", e);
        }
    }

    @Override
    public void close() {
        // Liberar recursos se necessário
    }
}
