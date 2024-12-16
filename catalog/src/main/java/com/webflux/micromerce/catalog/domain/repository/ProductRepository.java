package com.webflux.micromerce.catalog.domain.repository;

import com.webflux.micromerce.catalog.domain.model.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends R2dbcRepository<Product, Long> {

    Mono<Product> findByName(String name);
    
    @Query("SELECT * FROM products WHERE name ILIKE concat('%', :name, '%')")
    Flux<Product> findByNameContaining(String name);
}
