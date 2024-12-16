package com.webflux.micromerce.cart.infrastructure.repository;

import com.webflux.micromerce.cart.domain.model.CartStatus;
import com.webflux.micromerce.cart.infrastructure.document.CartDocument;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository("elasticsearchCartRepository")
public interface ElasticsearchCartRepository extends ReactiveElasticsearchRepository<CartDocument, String> {
    Flux<CartDocument> findByUserIdAndStatus(String userId, CartStatus status);
}
