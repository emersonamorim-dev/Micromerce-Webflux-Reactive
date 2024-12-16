package com.webflux.micromerce.cart.infrastructure.persistence;

import com.webflux.micromerce.cart.domain.model.Cart;
import com.webflux.micromerce.cart.domain.model.CartStatus;
import com.webflux.micromerce.cart.domain.repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Primary
@Repository
public class MongoDBCartRepository implements CartRepository {
    
    private final ReactiveMongoTemplate mongoTemplate;

    public MongoDBCartRepository(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<Cart> findById(String id) {
        log.debug("Buscando carrinho por ID: {}", id);
        return mongoTemplate.findById(id, Cart.class)
                .doOnSuccess(cart -> log.debug("Carrinho encontrado: {}", cart))
                .doOnError(error -> log.error("Erro ao buscar carrinho {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<Cart> save(Cart cart) {
        log.debug("Salvando carrinho: {}", cart);
        return mongoTemplate.save(cart)
                .doOnSuccess(savedCart -> log.debug("Carrinho salvo com sucesso: {}", savedCart))
                .doOnError(error -> log.error("Erro ao salvar carrinho: {}", error.getMessage()));
    }

    @Override
    public Mono<Cart> update(Cart cart) {
        log.debug("Atualizando carrinho: {}", cart);
        Query query = Query.query(Criteria.where("id").is(cart.getId()));
        Update update = new Update()
            .set("items", cart.getItems())
            .set("userId", cart.getUserId())
            .set("status", cart.getStatus())
            .set("totalAmount", cart.getTotalAmount())
            .set("promoCode", cart.getPromoCode())
            .set("discountAmount", cart.getDiscountAmount())
            .set("updatedAt", LocalDateTime.now());
        
        return mongoTemplate.findAndModify(query, update, Cart.class)
                .doOnSuccess(updatedCart -> log.debug("Carrinho atualizado com sucesso: {}", updatedCart))
                .doOnError(error -> log.error("Erro ao atualizar carrinho: {}", error.getMessage()));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        log.debug("Deletando carrinho: {}", id);
        return mongoTemplate.remove(Query.query(Criteria.where("id").is(id)), Cart.class)
                .then()
                .doOnSuccess(v -> log.debug("Carrinho {} deletado com sucesso", id))
                .doOnError(error -> log.error("Erro ao deletar carrinho {}: {}", id, error.getMessage()));
    }

    @Override
    public Flux<Cart> findAll() {
        log.debug("Buscando todos os carrinhos");
        return mongoTemplate.findAll(Cart.class)
                .doOnComplete(() -> log.debug("Busca de todos os carrinhos concluída"))
                .doOnError(error -> log.error("Erro ao buscar todos os carrinhos: {}", error.getMessage()));
    }

    @Override
    public Flux<Cart> findByUserId(String userId) {
        log.debug("Buscando carrinhos do usuário: {}", userId);
        return mongoTemplate.find(Query.query(Criteria.where("userId").is(userId)), Cart.class)
                .doOnComplete(() -> log.debug("Busca de carrinhos do usuário {} concluída", userId))
                .doOnError(error -> log.error("Erro ao buscar carrinhos do usuário {}: {}", userId, error.getMessage()));
    }

    @Override
    public Mono<Cart> findByUserIdAndStatus(String userId, CartStatus status) {
        log.debug("Buscando carrinho do usuário {} com status {}", userId, status);
        return mongoTemplate.findOne(
                Query.query(Criteria.where("userId").is(userId).and("status").is(status)),
                Cart.class)
                .doOnSuccess(cart -> log.debug("Carrinho encontrado para usuário {} com status {}: {}", userId, status, cart))
                .doOnError(error -> log.error("Erro ao buscar carrinho do usuário {} com status {}: {}", userId, status, error.getMessage()));
    }

    @Override
    public Flux<Cart> findAbandonedCarts(CartStatus status, LocalDateTime threshold) {
        log.debug("Buscando carrinhos abandonados com status {} e threshold {}", status, threshold);
        return mongoTemplate.find(
                Query.query(Criteria.where("status").is(status).and("updatedAt").lt(threshold)),
                Cart.class)
                .doOnComplete(() -> log.debug("Busca de carrinhos abandonados concluída"))
                .doOnError(error -> log.error("Erro ao buscar carrinhos abandonados: {}", error.getMessage()));
    }

    @Override
    public Flux<Cart> findByUserIdAndStatusBetweenDates(String userId, CartStatus status, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Buscando carrinhos do usuário {} com status {} entre {} e {}", userId, status, startDate, endDate);
        return mongoTemplate.find(
                Query.query(Criteria.where("userId").is(userId)
                        .and("status").is(status)
                        .and("createdAt").gte(startDate).lte(endDate)),
                Cart.class)
                .doOnComplete(() -> log.debug("Busca de carrinhos por período concluída"))
                .doOnError(error -> log.error("Erro ao buscar carrinhos por período: {}", error.getMessage()));
    }

    @Override
    public Flux<Cart> findByStatus(CartStatus status) {
        log.debug("Buscando carrinhos com status: {}", status);
        return mongoTemplate.find(Query.query(Criteria.where("status").is(status)), Cart.class)
                .doOnComplete(() -> log.debug("Busca de carrinhos por status {} concluída", status))
                .doOnError(error -> log.error("Erro ao buscar carrinhos por status {}: {}", status, error.getMessage()));
    }
}
