package com.webflux.micromerce.payment.infrastructure.config;

import com.webflux.micromerce.payment.domain.enums.PixKeyType;
import com.webflux.micromerce.payment.domain.enums.PaymentType;
import com.webflux.micromerce.payment.domain.model.*;
import com.webflux.micromerce.payment.presentation.exception.PaymentConversionException;
import io.r2dbc.spi.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Optional;

@Component
@ReadingConverter
public class PaymentReadingConverter implements Converter<Row, PaymentMethod> {

    private static final Logger log = LoggerFactory.getLogger(PaymentReadingConverter.class);
    private static final int DECIMAL_PLACES = 2;

    @Override
    public PaymentMethod convert(Row source) {
        try {
            String paymentTypeStr = getRequiredValue(source, "payment_type", String.class, "Payment type");
            UUID id = getRequiredValue(source, "id", UUID.class, "ID");
            UUID customerId = getRequiredValue(source, "customer_id", UUID.class, "Customer ID");
            String orderId = getRequiredValue(source, "order_id", String.class, "Order ID");
            Double rawAmount = getRequiredValue(source, "amount", Double.class, "Amount");
            String beneficiario = getRequiredValue(source, "beneficiario", String.class, "beneficiario");
            LocalDateTime createdAt = getRequiredValue(source, "created_at", LocalDateTime.class, "Created at");
            String statusStr = getRequiredValue(source, "status", String.class, "Status");
            String pagador = getRequiredValue(source, "pagador", String.class, "pagador");

            BigDecimal amount = convertToBigDecimal(rawAmount);
            PaymentStatus status = PaymentStatus.valueOf(statusStr.toUpperCase());
            PaymentType paymentType = PaymentType.valueOf(paymentTypeStr.toUpperCase());

            log.debug("Conversão de pagamento do tipo {} para encomendar {}", paymentType, orderId);

            return switch (paymentType) {
                case CREDIT_CARD -> createCreditCardPayment(source, id, amount, createdAt, status, customerId, orderId);
                case DEBIT_CARD -> createDebitCardPayment(source, id, amount, createdAt, status, customerId, orderId);
                case BOLETO -> createBoletoPayment(source, id, amount, beneficiario, createdAt, status, pagador, customerId, orderId);
                case PIX -> createPixPayment(source, id, amount, createdAt, status, customerId, orderId);
                default -> throw new PaymentConversionException("Tipo de pagamento não suportado: " + paymentTypeStr);
            };



        } catch (IllegalArgumentException e) {
            log.error("Erro ao converter tipo de pagamento ou status: {}", e.getMessage());
            throw new PaymentConversionException("Tipo de pagamento ou status inválido", e);
        } catch (Exception e) {
            log.error("Erro ao converter pagamento: {}", e.getMessage());
            throw new PaymentConversionException("Falha ao converter o pagamento", e);
        }
    }

    private <T> T getRequiredValue(Row source, String column, Class<T> type, String fieldName) {
        try {
            T value = source.get(column, type);
            if (value == null) {
                throw new PaymentConversionException("Campo obrigatório " + fieldName + " está faltando");
            }
            return value;
        } catch (Exception e) {
            log.error("Erro ao obter valor para campo {}: {}", fieldName, e.getMessage());
            throw new PaymentConversionException("Erro ao obter " + fieldName, e);
        }
    }

    private PaymentStatus parsePaymentStatus(String status) {
        try {
            return PaymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Status de pagamento inválido: {}", status);
            throw new PaymentConversionException("Status de pagamento inválido: " + status);
        }
    }

    private CreditCardPayment createCreditCardPayment(Row source, UUID id, BigDecimal amount,
                                                      LocalDateTime createdAt, PaymentStatus status, UUID customerId, String orderId) {
        String cardNumber = getRequiredValue(source, "card_number", String.class, "Card number");
        String cardHolderName = getRequiredValue(source, "card_holder_name", String.class, "Card holder name");
        String cvv = getRequiredValue(source, "cvv", String.class, "CVV");

        CreditCardPayment payment = new CreditCardPayment();
        payment.setId(id);
        payment.setCardNumber(cardNumber);
        payment.setCardHolderName(cardHolderName);
        payment.setCvv(cvv);
        payment.setAmount(amount);
        payment.setCreatedAt(createdAt);
        payment.setStatus(status);
        payment.setCustomerId(customerId);
        payment.setOrderId(orderId);
        return payment;
    }

    private DebitCardPayment createDebitCardPayment(Row source, UUID id, BigDecimal amount,
                                                    LocalDateTime createdAt, PaymentStatus status, UUID customerId, String orderId) {
        String cardNumber = getRequiredValue(source, "card_number", String.class, "Card number");
        String cardHolderName = getRequiredValue(source, "card_holder_name", String.class, "Card holder name");

        return new DebitCardPayment(id, cardNumber, cardHolderName, amount, createdAt, status, customerId, orderId);
    }

    private BoletoPayment createBoletoPayment(Row source, UUID id, BigDecimal amount, String beneficiario,
                                              LocalDateTime createdAt, PaymentStatus status, String pagador, UUID customerId, String orderId) {
        String boletoNumber = getRequiredValue(source, "boleto_number", String.class, "Boleto number");
        LocalDateTime dueDate = getRequiredValue(source, "due_date", LocalDateTime.class, "Due date");

        return new BoletoPayment(id, boletoNumber, amount, beneficiario, dueDate, createdAt, status, pagador, customerId, orderId);
    }

    private PixPayment createPixPayment(Row source, UUID id, BigDecimal amount,
                                        LocalDateTime createdAt, PaymentStatus status, UUID customerId, String orderId) {
        String pixKey = getRequiredValue(source, "pix_key", String.class, "PIX key");
        String pixKeyTypeStr = getRequiredValue(source, "pix_key_type", String.class, "PIX key type");
        PixKeyType pixKeyType = PixKeyType.valueOf(pixKeyTypeStr.toUpperCase());

        PixPayment payment = new PixPayment();
        payment.setId(id);
        payment.setPixKey(pixKey);
        payment.setPixKeyType(String.valueOf(pixKeyType));
        payment.setAmount(amount);
        payment.setCreatedAt(createdAt);
        payment.setStatus(status);
        payment.setCustomerId(customerId);
        payment.setOrderId(orderId);
        return payment;
    }

    private BigDecimal convertToBigDecimal(Double value) {
        return Optional.ofNullable(value)
                .map(v -> BigDecimal.valueOf(v).setScale(DECIMAL_PLACES, RoundingMode.HALF_EVEN))
                .orElse(BigDecimal.ZERO);
    }
}
