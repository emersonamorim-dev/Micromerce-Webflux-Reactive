package com.webflux.micromerce.catalog.application.usecase;

import com.webflux.micromerce.catalog.application.dto.ProductRequest;
import com.webflux.micromerce.catalog.application.dto.ProductResponse;
import com.webflux.micromerce.catalog.domain.model.Product;
import com.webflux.micromerce.catalog.domain.repository.ProductRepository;
import com.webflux.micromerce.catalog.infrastructure.cache.RedisService;
import com.webflux.micromerce.catalog.infrastructure.messaging.ProductEventProducer;
import com.webflux.micromerce.catalog.presentation.exceptions.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetProductUseCase {

    private final ProductRepository productRepository;
    private final ProductEventProducer productEventProducer;
    private final RedisService redisService;

    public Mono<ProductResponse> getProductById(Long id) {
        log.debug("Getting product by ID: {}", id);
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException("Produto não encontrado com ID: " + id)))
                .map(this::mapToResponse)
                .doOnSuccess(response -> {
                    log.debug("Product found: {}", response);
                    productEventProducer.sendProductEvent(response).subscribe();
                })
                .doOnError(error -> log.error("Erro ao obter o produto por ID {}: {}", id, error.getMessage()));
    }

    public Mono<ProductResponse> getProductByName(String name) {
        log.debug("Getting product by name: {}", name);
        return productRepository.findByName(name)
                .switchIfEmpty(Mono.error(new ProductNotFoundException("Produto não encontrado com nome: " + name)))
                .map(this::mapToResponse)
                .doOnSuccess(response -> {
                    log.debug("Product found: {}", response);
                    productEventProducer.sendProductEvent(response).subscribe();
                })
                .doOnError(error -> log.error("Erro ao obter produto pelo nome {}: {}", name, error.getMessage()));
    }

    public Flux<ProductResponse> getAllProducts() {
        log.debug("Getting all products");
        return productRepository.findAll()
                .map(this::mapToResponse)
                .doOnNext(response -> {
                    log.debug("Product found: {}", response);
                    productEventProducer.sendProductEvent(response).subscribe();
                })
                .doOnComplete(() -> log.debug("Terminou de receber todos os produtos"))
                .doOnError(error -> log.error("Erro ao obter todos os produtos: {}", error.getMessage()));
    }

    public Mono<ProductResponse> updateProduct(Long id, ProductRequest request) {
        log.debug("Updating product with ID {}: {}", id, request);
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException("Produto não encontrado com ID: " + id)))
                .flatMap(existingProduct -> {
                    existingProduct.setName(request.getName());
                    existingProduct.setPrice(request.getPrice());
                    existingProduct.setDescription(request.getDescription());
                    return productRepository.save(existingProduct);
                })
                .map(this::mapToResponse)
                .doOnSuccess(response -> {
                    log.debug("Product updated: {}", response);
                    productEventProducer.sendProductEvent(response).subscribe();
                })
                .doOnError(error -> log.error("Erro ao atualizar produto com ID {}: {}", id, error.getMessage()));
    }

    public Mono<Void> deleteProduct(Long id) {
        log.debug("Deleting product with ID: {}", id);
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException("Produto não encontrado com ID: " + id)))
                .flatMap(product -> {
                    ProductResponse response = mapToResponse(product);
                    return productRepository.delete(product)
                            .then(productEventProducer.sendProductEvent(response));
                })
                .doOnSuccess(unused -> log.debug("Product deleted successfully"))
                .doOnError(error -> log.error("Error deleting product with ID {}: {}", id, error.getMessage()));
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
