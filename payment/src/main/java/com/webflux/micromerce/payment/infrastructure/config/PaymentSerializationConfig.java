package com.webflux.micromerce.payment.infrastructure.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.webflux.micromerce.payment.domain.model.*;
import com.webflux.micromerce.payment.infrastructure.serialization.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentSerializationConfig {

    @Bean
    public SimpleModule paymentSerializationModule() {
        SimpleModule module = new SimpleModule("PaymentSerializationModule");
        
        // Registrar serializadores personalizados para cada tipo de pagamento
        module.addSerializer(CreditCardPayment.class, new CreditCardPaymentSerializer());
        module.addSerializer(DebitCardPayment.class, new DebitCardPaymentSerializer());
        module.addSerializer(PixPayment.class, new PixPaymentSerializer());
        module.addSerializer(BoletoPayment.class, new BoletoPaymentSerializer());
        
        return module;
    }
}
