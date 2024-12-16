package com.webflux.micromerce.catalog.application.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductRequestTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidProductRequest() {
        ProductRequest productRequest = new ProductRequest("Product Name", BigDecimal.valueOf(100.0), "Product Description");
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(productRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidProductRequest_NameBlank() {
        ProductRequest productRequest = new ProductRequest("", BigDecimal.valueOf(100.0), "Product Description");
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(productRequest);
        assertEquals(1, violations.size());
        assertEquals("não deve estar em branco", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidProductRequest_PriceNull() {
        ProductRequest productRequest = new ProductRequest("Product Name", null, "Product Description");
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(productRequest);
        assertEquals(1, violations.size());
        assertEquals("não deve ser nulo", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidProductRequest_DescriptionBlank() {
        ProductRequest productRequest = new ProductRequest("Product Name", BigDecimal.valueOf(100.0), "");
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(productRequest);
        assertEquals(1, violations.size());
        assertEquals("não deve estar em branco", violations.iterator().next().getMessage());
    }

    @Test
    void testProductRequestBuilder() {
        // Arrange & Act
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .description("Test Description")
                .build();

        // Assert
        assertNotNull(request);
        assertEquals("Test Product", request.getName());
        assertEquals(BigDecimal.valueOf(100), request.getPrice());
        assertEquals("Test Description", request.getDescription());
    }

    @Test
    void testProductRequestGettersAndSetters() {
        // Arrange
        ProductRequest request = new ProductRequest();

        // Act
        request.setName("Test Product");
        request.setPrice(BigDecimal.valueOf(100));
        request.setDescription("Test Description");

        // Assert
        assertEquals("Test Product", request.getName());
        assertEquals(BigDecimal.valueOf(100), request.getPrice());
        assertEquals("Test Description", request.getDescription());
    }

    @Test
    void testProductRequestEqualsAndHashCode() {
        // Arrange
        ProductRequest request1 = ProductRequest.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .description("Test Description")
                .build();

        ProductRequest request2 = ProductRequest.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .description("Test Description")
                .build();

        // Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }
}
