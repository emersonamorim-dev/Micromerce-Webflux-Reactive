package com.webflux.micromerce.catalog.application.usecase;

import com.webflux.micromerce.catalog.application.dto.ProductRequest;
import com.webflux.micromerce.catalog.application.dto.ProductResponse;
import com.webflux.micromerce.catalog.domain.model.Product;
import com.webflux.micromerce.catalog.domain.repository.ProductRepository;
import com.webflux.micromerce.catalog.infrastructure.cache.RedisService;
import com.webflux.micromerce.catalog.infrastructure.messaging.ProductEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductEventProducer productEventProducer;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private CreateProductUseCase createProductUseCase;

    private ProductRequest productRequest;
    private Product product;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        productRequest = ProductRequest.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(10.0))
                .description("Test Description")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(10.0))
                .description("Test Description")
                .build();

        productResponse = ProductResponse.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(10.0))
                .description("Test Description")
                .build();
    }

    @Test
    void execute_Success() {
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));
        when(productEventProducer.sendProductEvent(any(ProductResponse.class))).thenReturn(Mono.empty());
        when(redisService.saveProduct(any(ProductResponse.class))).thenReturn(Mono.empty());

        StepVerifier.create(createProductUseCase.execute(productRequest))
                .expectNext(productResponse)
                .verifyComplete();
    }

    @Test
    void execute_RepositoryError() {
        when(productRepository.save(any(Product.class))).thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(createProductUseCase.execute(productRequest))
                .expectErrorMessage("Database error")
                .verify();
    }

    @Test
    void execute_EventProducerError() {
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));
        when(productEventProducer.sendProductEvent(any(ProductResponse.class)))
                .thenReturn(Mono.error(new RuntimeException("Event error")));
        when(redisService.saveProduct(any(ProductResponse.class))).thenReturn(Mono.empty());

        StepVerifier.create(createProductUseCase.execute(productRequest))
                .expectNext(productResponse)
                .verifyComplete();
    }

    @Test
    void execute_RedisError() {
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));
        when(productEventProducer.sendProductEvent(any(ProductResponse.class))).thenReturn(Mono.empty());
        when(redisService.saveProduct(any(ProductResponse.class)))
                .thenReturn(Mono.error(new RuntimeException("Redis error")));

        StepVerifier.create(createProductUseCase.execute(productRequest))
                .expectNext(productResponse)
                .verifyComplete();
    }
}
