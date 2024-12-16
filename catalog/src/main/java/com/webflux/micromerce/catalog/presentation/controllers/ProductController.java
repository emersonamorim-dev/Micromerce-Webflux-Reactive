package com.webflux.micromerce.catalog.presentation.controllers;

import com.webflux.micromerce.catalog.application.dto.ProductRequest;
import com.webflux.micromerce.catalog.application.dto.ProductResponse;
import com.webflux.micromerce.catalog.application.usecase.CreateProductUseCase;
import com.webflux.micromerce.catalog.application.usecase.GetProductUseCase;
import com.webflux.micromerce.catalog.presentation.exceptions.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductResponse>> createProduct(@RequestBody ProductRequest request) {
        log.debug("Creating product: {}", request);
        
        return createProductUseCase.execute(request)
                .map(response -> {
                    log.debug("Product created successfully: {}", response);
                    return ResponseEntity
                            .status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response);
                })
                .doOnError(error -> log.error("Error creating product: {}", error.getMessage()))
                .onErrorResume(error -> Mono.just(ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build()));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductResponse>> getProductById(@PathVariable Long id) {
        log.debug("Getting product by ID: {}", id);
        return getProductUseCase.getProductById(id)
                .map(response -> {
                    log.debug("Product found: {}", response);
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response);
                })
                .onErrorResume(ProductNotFoundException.class, error -> {
                    log.error("Product not found: {}", error.getMessage());
                    return Mono.just(ResponseEntity.notFound().build());
                })
                .onErrorResume(error -> {
                    log.error("Error getting product: {}", error.getMessage());
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
                });
    }

    @GetMapping(value = "/name/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductResponse>> getProductByName(@PathVariable String name) {
        log.debug("Getting product by name: {}", name);
        return getProductUseCase.getProductByName(name)
                .map(response -> {
                    log.debug("Product found: {}", response);
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response);
                })
                .onErrorResume(ProductNotFoundException.class, error -> {
                    log.error("Product not found: {}", error.getMessage());
                    return Mono.just(ResponseEntity.notFound().build());
                })
                .onErrorResume(error -> {
                    log.error("Error getting product: {}", error.getMessage());
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
                });
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Flux<ProductResponse>>> getAllProducts() {
        log.debug("Getting all products");
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getProductUseCase.getAllProducts()
                        .doOnNext(response -> log.debug("Produto recuperado: {}", response))
                        .doOnComplete(() -> log.debug("Todos os produtos recuperados"))
                        .doOnError(error -> log.error("Erro ao obter produtos: {}", error.getMessage()))));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductResponse>> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        log.debug("Updating product with ID {}: {}", id, request);
        return getProductUseCase.updateProduct(id, request)
                .map(response -> {
                    log.debug("Produto atualizado com sucesso: {}", response);
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response);
                })
                .onErrorResume(ProductNotFoundException.class, error -> {
                    log.error("Produto não encontrado: {}", error.getMessage());
                    return Mono.just(ResponseEntity.notFound().build());
                })
                .onErrorResume(error -> {
                    log.error("Erro ao atualizar o produto: {}", error.getMessage());
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
                });
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable Long id) {
        log.debug("Deleting product with ID: {}", id);
        return getProductUseCase.deleteProduct(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .doOnSuccess(result -> log.debug("Produto excluído com sucesso"))
                .onErrorResume(error -> {
                    log.error("Erro ao excluir produto: {}", error.getMessage());
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
                });
    }
}
