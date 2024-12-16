package com.webflux.micromerce.catalog.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webflux.micromerce.catalog.application.dto.ProductResponse;
import com.webflux.micromerce.catalog.config.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;

@SpringBootTest
@ActiveProfiles("test")
class RedisServiceTest extends BaseIntegrationTest {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private RedisService redisService;
    private ProductResponse testProduct;
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    @BeforeEach
    void setUp() {
        redisService = new RedisService(redisTemplate, objectMapper);
        
        testProduct = ProductResponse.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(10.0))
                .description("Test Description")
                .build();
    }

    @Test
    void saveProduct_Success() {
        // Act & Assert
        StepVerifier.create(redisService.saveProduct(testProduct))
                .verifyComplete();

        // Verify the product was stored
        String key = "product:" + testProduct.getId();
        StepVerifier.create(redisTemplate.opsForValue().get(key))
                .expectNextMatches(json -> json.contains("Test Product"))
                .verifyComplete();
    }

    @Test
    void getProduct_Success() {
        // Arrange - First save a product
        StepVerifier.create(redisService.saveProduct(testProduct))
                .verifyComplete();

        // Act & Assert
        StepVerifier.create(redisService.getProduct(testProduct.getId()))
                .expectNextMatches(product -> 
                    product.getId().equals(testProduct.getId()) &&
                    product.getName().equals(testProduct.getName()) &&
                    product.getPrice().compareTo(testProduct.getPrice()) == 0 &&
                    product.getDescription().equals(testProduct.getDescription()))
                .verifyComplete();
    }

    @Test
    void getProduct_NotFound() {
        // Act & Assert
        StepVerifier.create(redisService.getProduct(999L))
                .verifyComplete();
    }

    @Test
    void deleteProduct_Success() {
        // Arrange - First save a product
        StepVerifier.create(redisService.saveProduct(testProduct))
                .verifyComplete();

        // Act & Assert
        StepVerifier.create(redisService.deleteProduct(testProduct.getId()))
                .expectNext(true)
                .verifyComplete();

        // Verify the product was deleted
        String key = "product:" + testProduct.getId();
        StepVerifier.create(redisTemplate.opsForValue().get(key))
                .verifyComplete();
    }

    @Test
    void deleteProduct_NotFound() {
        // Act & Assert
        StepVerifier.create(redisService.deleteProduct(999L))
                .expectNext(false)
                .verifyComplete();
    }
}
