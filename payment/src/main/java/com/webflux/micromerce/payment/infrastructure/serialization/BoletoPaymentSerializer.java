package com.webflux.micromerce.payment.infrastructure.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.webflux.micromerce.payment.domain.model.BoletoPayment;
import com.webflux.micromerce.payment.domain.service.PaymentDataGenerator;

import java.io.IOException;

public class BoletoPaymentSerializer extends StdSerializer<BoletoPayment> {

    public BoletoPaymentSerializer() {
        super(BoletoPayment.class);
    }

    @Override
    public void serialize(BoletoPayment payment, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", payment.id() != null ? payment.id().toString() : null);
        gen.writeStringField("paymentType", payment.paymentType().name());
        
        // Preserve original boleto number and due date
        gen.writeStringField("boletoNumber", payment.getBoletoNumber());
        gen.writeStringField("dueDate", payment.getDueDate() != null ? payment.getDueDate().toString() : null);
        
        gen.writeNumberField("amount", payment.amount() != null ? payment.amount().doubleValue() : 0.0);
        gen.writeStringField("customerId", payment.customerId() != null ? payment.customerId().toString() : null);
        gen.writeStringField("orderId", payment.orderId());
        gen.writeStringField("status", payment.status() != null ? payment.status().name() : null);
        gen.writeStringField("createdAt", payment.createdAt() != null ? payment.createdAt().toString() : null);
        gen.writeEndObject();
    }
}