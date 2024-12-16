package com.webflux.micromerce.payment.infrastructure.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.webflux.micromerce.payment.domain.model.CreditCardPayment;
import com.webflux.micromerce.payment.domain.service.PaymentDataGenerator;

import java.io.IOException;

public class CreditCardPaymentSerializer extends StdSerializer<CreditCardPayment> {

    public CreditCardPaymentSerializer() {
        super(CreditCardPayment.class);
    }

    @Override
    public void serialize(CreditCardPayment payment, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", payment.id() != null ? payment.id().toString() : null);
        gen.writeStringField("paymentType", payment.paymentType().name());
        
        // Preserva os valores originais, mas mascara o número do cartão
        String cardNumber = payment.getCardNumber();
        gen.writeStringField("cardNumber", cardNumber != null && !cardNumber.isEmpty() 
            ? PaymentDataGenerator.maskCardNumber(cardNumber) 
            : cardNumber);
            
        gen.writeStringField("cardHolderName", payment.getCardHolderName());
        gen.writeStringField("cvv", payment.getCvv());
        gen.writeNumberField("amount", payment.amount() != null ? payment.amount().doubleValue() : 0.0);
        gen.writeStringField("customerId", payment.customerId() != null ? payment.customerId().toString() : null);
        gen.writeStringField("orderId", payment.orderId());
        gen.writeStringField("status", payment.status() != null ? payment.status().name() : null);
        gen.writeStringField("createdAt", payment.createdAt() != null ? payment.createdAt().toString() : null);
        gen.writeEndObject();
    }
}
