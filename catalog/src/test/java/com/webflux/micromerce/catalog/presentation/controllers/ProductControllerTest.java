package com.webflux.micromerce.catalog.presentation.controllers;

import com.webflux.micromerce.catalog.application.dto.ProductRequest;
import com.webflux.micromerce.catalog.application.dto.ProductResponse;
import com.webflux.micromerce.catalog.application.usecase.CreateProductUseCase;
import com.webflux.micromerce.catalog.application.usecase.GetProductUseCase;
import com.webflux.micromerce.catalog.presentation.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ProductControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private CreateProductUseCase createProductUseCase;

    @Mock
    private GetProductUseCase getProductUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ProductController productController = new ProductController(createProductUseCase, getProductUseCase);
        webTestClient = WebTestClient.bindToController(productController).build();
    }

    @Test
    void createProduct_Success() {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .description("Test Description")
                .build();

        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .description("Test Description")
                .build();

        when(createProductUseCase.execute(any(ProductRequest.class))).thenReturn(Mono.just(response));

        // Act & Assert
        webTestClient.post()
                .uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductResponse.class)
                .isEqualTo(response);
    }

    @Test
    void getProductById_Success() {
        // Arrange
        Long productId = 1L;
        ProductResponse response = ProductResponse.builder()
                .id(productId)
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .description("Test Description")
                .build();

        when(getProductUseCase.getProductById(productId)).thenReturn(Mono.just(response));

        // Act & Assert
        webTestClient.get()
                .uri("/products/{id}", productId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponse.class)
                .isEqualTo(response);
    }

    @Test
    void getProductById_NotFound() {
        // Arrange
        Long productId = 1L;
        when(getProductUseCase.getProductById(productId))
                .thenReturn(Mono.error(new ProductNotFoundException("Product not found")));

        // Act & Assert
        webTestClient.get()
                .uri("/products/{id}", productId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getAllProducts_Success() {
        // Arrange
        ProductResponse response1 = ProductResponse.builder()
                .id(1L)
                .name("Product 1")
                .price(BigDecimal.valueOf(100))
                .description("Description 1")
                .build();

        ProductResponse response2 = ProductResponse.builder()
                .id(2L)
                .name("Product 2")
                .price(BigDecimal.valueOf(200))
                .description("Description 2")
                .build();

        when(getProductUseCase.getAllProducts()).thenReturn(Flux.just(response1, response2));

        // Act & Assert
        webTestClient.get()
                .uri("/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductResponse.class)
                .contains(response1, response2);
    }

    @Test
    void updateProduct_Success() {
        // Arrange
        Long productId = 1L;
        ProductRequest request = ProductRequest.builder()
                .name("Updated Product")
                .price(BigDecimal.valueOf(150))
                .description("Updated Description")
                .build();

        ProductResponse response = ProductResponse.builder()
                .id(productId)
                .name("Updated Product")
                .price(BigDecimal.valueOf(150))
                .description("Updated Description")
                .build();

        when(getProductUseCase.updateProduct(eq(productId), any(ProductRequest.class)))
                .thenReturn(Mono.just(response));

        // Act & Assert
        webTestClient.put()
                .uri("/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponse.class)
                .isEqualTo(response);
    }

    @Test
    void updateProduct_NotFound() {
        // Arrange
        Long productId = 1L;
        ProductRequest request = ProductRequest.builder()
                .name("Updated Product")
                .price(BigDecimal.valueOf(150))
                .description("Updated Description")
                .build();

        when(getProductUseCase.updateProduct(eq(productId), any(ProductRequest.class)))
                .thenReturn(Mono.error(new ProductNotFoundException("Product not found")));

        // Act & Assert
        webTestClient.put()
                .uri("/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteProduct_Success() {
        // Arrange
        Long productId = 1L;
        when(getProductUseCase.deleteProduct(productId)).thenReturn(Mono.empty());

        // Act & Assert
        webTestClient.delete()
                .uri("/products/{id}", productId)
                .exchange()
                .expectStatus().isNoContent();
    }
}
