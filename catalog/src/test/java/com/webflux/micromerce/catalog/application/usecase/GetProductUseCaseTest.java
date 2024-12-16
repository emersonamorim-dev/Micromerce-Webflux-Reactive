package com.webflux.micromerce.catalog.application.usecase;

import com.webflux.micromerce.catalog.application.dto.ProductRequest;
import com.webflux.micromerce.catalog.application.dto.ProductResponse;
import com.webflux.micromerce.catalog.domain.model.Product;
import com.webflux.micromerce.catalog.domain.repository.ProductRepository;
import com.webflux.micromerce.catalog.infrastructure.cache.RedisService;
import com.webflux.micromerce.catalog.infrastructure.messaging.ProductEventProducer;
import com.webflux.micromerce.catalog.presentation.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductEventProducer productEventProducer;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private GetProductUseCase getProductUseCase;

    private Product product;
    private ProductResponse productResponse;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
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

        productRequest = ProductRequest.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(10.0))
                .description("Test Description")
                .build();
    }

    @Test
    void getProductById_Success() {
        when(productRepository.findById(anyLong())).thenReturn(Mono.just(product));
        when(productEventProducer.sendProductEvent(any(ProductResponse.class))).thenReturn(Mono.empty());

        StepVerifier.create(getProductUseCase.getProductById(1L))
                .expectNext(productResponse)
                .verifyComplete();
    }

    @Test
    void getProductById_NotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(getProductUseCase.getProductById(1L))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void getProductByName_Success() {
        when(productRepository.findByName(anyString())).thenReturn(Mono.just(product));
        when(productEventProducer.sendProductEvent(any(ProductResponse.class))).thenReturn(Mono.empty());

        StepVerifier.create(getProductUseCase.getProductByName("Test Product"))
                .expectNext(productResponse)
                .verifyComplete();
    }

    @Test
    void getProductByName_NotFound() {
        when(productRepository.findByName(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(getProductUseCase.getProductByName("Non-existent Product"))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void getAllProducts_Success() {
        when(productRepository.findAll()).thenReturn(Flux.just(product));
        when(productEventProducer.sendProductEvent(any(ProductResponse.class))).thenReturn(Mono.empty());

        StepVerifier.create(getProductUseCase.getAllProducts())
                .expectNext(productResponse)
                .verifyComplete();
    }

    @Test
    void getAllProducts_Empty() {
        when(productRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(getProductUseCase.getAllProducts())
                .verifyComplete();
    }

    @Test
    void updateProduct_Success() {
        when(productRepository.findById(anyLong())).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));
        when(productEventProducer.sendProductEvent(any(ProductResponse.class))).thenReturn(Mono.empty());

        StepVerifier.create(getProductUseCase.updateProduct(1L, productRequest))
                .expectNext(productResponse)
                .verifyComplete();
    }

    @Test
    void updateProduct_NotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(getProductUseCase.updateProduct(1L, productRequest))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void deleteProduct_Success() {
        when(productRepository.findById(anyLong())).thenReturn(Mono.just(product));
        when(productRepository.delete(any(Product.class))).thenReturn(Mono.empty());
        when(productEventProducer.sendProductEvent(any(ProductResponse.class))).thenReturn(Mono.empty());

        StepVerifier.create(getProductUseCase.deleteProduct(1L))
                .verifyComplete();
    }

    @Test
    void deleteProduct_NotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(getProductUseCase.deleteProduct(1L))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void eventProducer_Error() {
        when(productRepository.findById(anyLong())).thenReturn(Mono.just(product));
        when(productEventProducer.sendProductEvent(any(ProductResponse.class)))
                .thenReturn(Mono.error(new RuntimeException("Event error")));

        StepVerifier.create(getProductUseCase.getProductById(1L))
                .expectNext(productResponse)
                .verifyComplete();
    }
}
