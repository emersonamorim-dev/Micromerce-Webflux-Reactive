package com.webflux.micromerce.catalog.domain.repository;

import com.webflux.micromerce.catalog.config.BaseIntegrationTest;
import com.webflux.micromerce.catalog.config.TestPostgresDBConfig;
import com.webflux.micromerce.catalog.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@DataR2dbcTest
@Import(TestPostgresDBConfig.class)
@ActiveProfiles("test")
class ProductRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void save_Success() {
        // Arrange
        Product product = Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .description("Test Description")
                .build();

        // Act & Assert
        StepVerifier.create(productRepository.save(product))
                .expectNextMatches(savedProduct ->
                        savedProduct.getId() != null &&
                        savedProduct.getName().equals("Test Product") &&
                        savedProduct.getPrice().compareTo(BigDecimal.valueOf(100)) == 0 &&
                        savedProduct.getDescription().equals("Test Description"))
                .verifyComplete();
    }

    @Test
    void findAll_Success() {
        // Arrange
        Product product1 = Product.builder()
                .name("Product 1")
                .price(BigDecimal.valueOf(100))
                .description("Description 1")
                .build();

        Product product2 = Product.builder()
                .name("Product 2")
                .price(BigDecimal.valueOf(200))
                .description("Description 2")
                .build();

        // Save products and verify findAll
        StepVerifier.create(productRepository.save(product1)
                .then(productRepository.save(product2))
                .thenMany(productRepository.findAll()))
                .expectNextMatches(p -> p.getName().equals("Product 1"))
                .expectNextMatches(p -> p.getName().equals("Product 2"))
                .verifyComplete();
    }

    @Test
    void findById_Success() {
        // Arrange
        Product product = Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .description("Test Description")
                .build();

        // Save product and find by id
        StepVerifier.create(productRepository.save(product)
                .flatMap(savedProduct -> productRepository.findById(savedProduct.getId())))
                .expectNextMatches(foundProduct ->
                        foundProduct.getName().equals("Test Product") &&
                        foundProduct.getPrice().compareTo(BigDecimal.valueOf(100)) == 0)
                .verifyComplete();
    }

    @Test
    void findById_NotFound() {
        // Act & Assert
        StepVerifier.create(productRepository.findById(999L))
                .verifyComplete();
    }

    @Test
    void deleteById_Success() {
        // Arrange
        Product product = Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .description("Test Description")
                .build();

        // Save product, delete it, and verify it's gone
        StepVerifier.create(productRepository.save(product)
                .flatMap(savedProduct -> productRepository.deleteById(savedProduct.getId())
                        .then(productRepository.findById(savedProduct.getId()))))
                .verifyComplete();
    }

    @Test
    void findByName_Success() {
        // Arrange
        Product product = Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .description("Test Description")
                .build();

        // Save product and find by name
        StepVerifier.create(productRepository.save(product)
                .flatMap(savedProduct -> productRepository.findByName(savedProduct.getName())))
                .expectNextMatches(foundProduct ->
                        foundProduct.getName().equals("Test Product") &&
                        foundProduct.getPrice().compareTo(BigDecimal.valueOf(100)) == 0)
                .verifyComplete();
    }

    @Test
    void findByNameContaining_Success() {
        // Arrange
        Product product1 = Product.builder()
                .name("Test Product 1")
                .price(BigDecimal.valueOf(100))
                .description("Description 1")
                .build();

        Product product2 = Product.builder()
                .name("Test Product 2")
                .price(BigDecimal.valueOf(200))
                .description("Description 2")
                .build();

        Product product3 = Product.builder()
                .name("Different Name")
                .price(BigDecimal.valueOf(300))
                .description("Description 3")
                .build();

        // Save products and search by name containing
        StepVerifier.create(Flux.just(product1, product2, product3)
                .flatMap(productRepository::save)
                .thenMany(productRepository.findByNameContaining("Test")))
                .expectNextMatches(p -> p.getName().equals("Test Product 1"))
                .expectNextMatches(p -> p.getName().equals("Test Product 2"))
                .verifyComplete();
    }
}
