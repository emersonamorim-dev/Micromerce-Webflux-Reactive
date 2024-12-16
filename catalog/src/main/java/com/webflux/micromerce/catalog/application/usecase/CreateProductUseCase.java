package com.webflux.micromerce.catalog.application.usecase;

import com.webflux.micromerce.catalog.application.dto.ProductRequest;
import com.webflux.micromerce.catalog.application.dto.ProductResponse;
import com.webflux.micromerce.catalog.domain.model.Product;
import com.webflux.micromerce.catalog.domain.repository.ProductRepository;
import com.webflux.micromerce.catalog.infrastructure.cache.RedisService;
import com.webflux.micromerce.catalog.infrastructure.messaging.ProductEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateProductUseCase {

    private final ProductRepository productRepository;
    private final ProductEventProducer productEventProducer;
    private final RedisService redisService;

    public Mono<ProductResponse> execute(ProductRequest request) {
        log.debug("Creating new product: {}", request);
        
        return Mono.just(request)
                .map(this::mapToProduct)
                .flatMap(productRepository::save)
                .map(this::mapToResponse)
                .doOnSuccess(response -> {
                    log.debug("Product created successfully: {}", response);
                    productEventProducer.sendProductEvent(response)
                            .doOnSuccess(v -> log.debug("Evento de produto enviado com sucesso"))
                            .doOnError(error -> log.error("Erro ao enviar evento do produto: {}", error.getMessage()))
                            .subscribe();
                    
                    redisService.saveProduct(response)
                            .doOnSuccess(v -> log.debug("Produto armazenado em cache com sucesso"))
                            .doOnError(error -> log.error("Erro ao armazenar em cache o produto: {}", error.getMessage()))
                            .subscribe();
                })
                .doOnError(error -> log.error("Erro ao criar produto: {}", error.getMessage()));
    }

    private Product mapToProduct(ProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .description(request.getDescription())
                .build();
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();
    }
}
