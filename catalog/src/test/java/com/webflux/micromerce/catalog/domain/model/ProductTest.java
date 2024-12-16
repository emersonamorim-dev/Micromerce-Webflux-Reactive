package com.webflux.micromerce.catalog.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    @Test
    public void testProductCreation() {
        // Arrange
        Long id = 1L;
        String name = "Product Name";
        BigDecimal price = BigDecimal.valueOf(100.0);
        String description = "Product Description";

        // Act
        Product product = new Product(id, name, price, description);

        // Assert
        assertNotNull(product);
        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(price, product.getPrice());
        assertEquals(description, product.getDescription());
    }

    @Test
    public void testProductNoArgsConstructor() {
        // Act
        Product product = new Product();

        // Assert
        assertNotNull(product);
        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getPrice());
        assertNull(product.getDescription());
    }

    @Test
    public void testProductSetters() {
        // Arrange
        Product product = new Product();

        // Act
        product.setId(1L);
        product.setName("Product Name");
        product.setPrice(BigDecimal.valueOf(100.0));
        product.setDescription("Product Description");

        // Assert
        assertEquals(1L, product.getId());
        assertEquals("Product Name", product.getName());
        assertEquals(BigDecimal.valueOf(100.0), product.getPrice());
        assertEquals("Product Description", product.getDescription());
    }

    @Test
    public void testProductToString() {
        // Arrange
        Product product = new Product(1L, "Product Name", BigDecimal.valueOf(100.0), "Product Description");

        // Act
        String productString = product.toString();

        // Assert
        assertEquals("Product(id=1, name=Product Name, price=100.0, description=Product Description)", productString);
    }
}
