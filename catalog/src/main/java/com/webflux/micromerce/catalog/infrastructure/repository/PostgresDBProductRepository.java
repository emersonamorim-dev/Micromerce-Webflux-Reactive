package com.webflux.micromerce.catalog.infrastructure.repository;

import com.webflux.micromerce.catalog.domain.model.Product;
import com.webflux.micromerce.catalog.domain.repository.ProductRepository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PostgresDBProductRepository extends R2dbcRepository<Product, Long>, ProductRepository {

    @Override
    Mono<Product> findByName(String name);
}
