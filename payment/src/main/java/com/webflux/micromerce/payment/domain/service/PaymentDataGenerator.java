package com.webflux.micromerce.payment.domain.service;

import java.util.Random;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class PaymentDataGenerator {
    private static final Random random = new Random();

    public static String generateBoletoNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 47; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public static String generatePixKey() {
        return UUID.randomUUID().toString();
    }

    public static String generatePixKeyType() {
        String[] types = {"CPF", "CNPJ", "EMAIL", "TELEFONE", "CHAVE_ALEATORIA"};
        return types[random.nextInt(types.length)];
    }

    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    public static String generateCardNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public static LocalDateTime generateDueDate() {
        return LocalDateTime.now().plusDays(random.nextInt(3, 30))
                          .truncatedTo(ChronoUnit.MINUTES);
    }

    public static String generateCardHolderName() {
        String[] firstNames = {"João", "Maria", "Pedro", "Ana", "Lucas", "Julia"};
        String[] lastNames = {"Silva", "Santos", "Oliveira", "Souza", "Ferreira"};
        return firstNames[random.nextInt(firstNames.length)] + " " + 
               lastNames[random.nextInt(lastNames.length)];
    }
}
