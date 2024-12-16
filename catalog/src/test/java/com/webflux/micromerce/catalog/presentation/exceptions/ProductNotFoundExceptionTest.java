package com.webflux.micromerce.catalog.presentation.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProductNotFoundExceptionTest {

    @Test
    public void testProductNotFoundException() {
        String message = "Product not found";
        ProductNotFoundException exception = new ProductNotFoundException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testProductNotFoundExceptionWithNullMessage() {
        ProductNotFoundException exception = new ProductNotFoundException(null);

        assertNull(exception.getMessage());
    }
}
