package com.webflux.micromerce.catalog.application.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductResponseTest {

    @Test
    void testProductResponseBuilder() {
        // Arrange & Act
        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .description("Test Description")
                .build();

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Product", response.getName());
        assertEquals(BigDecimal.valueOf(100), response.getPrice());
        assertEquals("Test Description", response.getDescription());
    }

    @Test
    void testProductResponseGettersAndSetters() {
        // Arrange
        ProductResponse response = new ProductResponse();

        // Act
        response.setId(1L);
        response.setName("Test Product");
        response.setPrice(BigDecimal.valueOf(100));
        response.setDescription("Test Description");

        // Assert
        assertEquals(1L, response.getId());
        assertEquals("Test Product", response.getName());
        assertEquals(BigDecimal.valueOf(100), response.getPrice());
        assertEquals("Test Description", response.getDescription());
    }

    @Test
    void testProductResponseEqualsAndHashCode() {
        // Arrange
        ProductResponse response1 = ProductResponse.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .description("Test Description")
                .build();

        ProductResponse response2 = ProductResponse.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .description("Test Description")
                .build();

        // Assert
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }
}
