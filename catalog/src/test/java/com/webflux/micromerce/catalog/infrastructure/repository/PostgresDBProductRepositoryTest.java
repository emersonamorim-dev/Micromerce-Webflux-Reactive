package com.webflux.micromerce.catalog.infrastructure.repository;

import com.webflux.micromerce.catalog.config.BaseIntegrationTest;
import com.webflux.micromerce.catalog.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@SpringBootTest
@ActiveProfiles("test")
class PostgresDBProductRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private PostgresDBProductRepository postgresDBProductRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product(null, "Test Product", BigDecimal.valueOf(100.0), "Test Description");
        postgresDBProductRepository.deleteAll().block();
    }

    @Test
    void testSaveAndFindById() {
        StepVerifier.create(postgresDBProductRepository.save(testProduct))
                .expectNextMatches(saved -> {
                    testProduct.setId(saved.getId());
                    return saved.getName().equals(testProduct.getName()) &&
                           saved.getPrice().equals(testProduct.getPrice()) &&
                           saved.getDescription().equals(testProduct.getDescription());
                })
                .verifyComplete();

        StepVerifier.create(postgresDBProductRepository.findById(testProduct.getId()))
                .expectNext(testProduct)
                .verifyComplete();
    }

    @Test
    void testFindByName() {
        StepVerifier.create(postgresDBProductRepository.save(testProduct)
                .flatMap(saved -> {
                    testProduct.setId(saved.getId());
                    return postgresDBProductRepository.findByName(testProduct.getName());
                }))
                .expectNext(testProduct)
                .verifyComplete();
    }

    @Test
    void testFindAll() {
        Product product1 = new Product(null, "Product 1", BigDecimal.valueOf(100.0), "Description 1");
        Product product2 = new Product(null, "Product 2", BigDecimal.valueOf(200.0), "Description 2");

        StepVerifier.create(postgresDBProductRepository.save(product1)
                .then(postgresDBProductRepository.save(product2))
                .thenMany(postgresDBProductRepository.findAll()))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void testDeleteById() {
        StepVerifier.create(postgresDBProductRepository.save(testProduct)
                .flatMap(saved -> {
                    testProduct.setId(saved.getId());
                    return postgresDBProductRepository.deleteById(saved.getId())
                            .then(postgresDBProductRepository.findById(saved.getId()));
                }))
                .verifyComplete();
    }

    @Test
    void testFindByIdNotFound() {
        StepVerifier.create(postgresDBProductRepository.findById(999L))
                .verifyComplete();
    }

    @Test
    void testFindByNameNotFound() {
        StepVerifier.create(postgresDBProductRepository.findByName("Non Existent Product"))
                .verifyComplete();
    }
}
