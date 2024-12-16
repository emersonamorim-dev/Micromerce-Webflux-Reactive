package com.webflux.micromerce.cart.domain.service;

import com.webflux.micromerce.cart.domain.model.Cart;
import com.webflux.micromerce.cart.domain.model.CartItem;
import com.webflux.micromerce.cart.domain.model.CartStatus;
import com.webflux.micromerce.cart.infrastructure.document.CartDocument;
import com.webflux.micromerce.cart.infrastructure.repository.ElasticsearchCartRepository;
import com.webflux.micromerce.cart.infrastructure.repository.MongoCartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
public class CartService {
    private final MongoCartRepository mongoCartRepository;
    private final ElasticsearchCartRepository elasticsearchCartRepository;

    public CartService(
            @Qualifier("mongoCartRepository") MongoCartRepository mongoCartRepository,
            @Qualifier("elasticsearchCartRepository") ElasticsearchCartRepository elasticsearchCartRepository) {
        this.mongoCartRepository = mongoCartRepository;
        this.elasticsearchCartRepository = elasticsearchCartRepository;
    }

    public Mono<Cart> createCart(Cart cart) {
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        cart.setStatus(CartStatus.ACTIVE);
        
        return mongoCartRepository.save(cart)
                .flatMap(savedCart -> {
                    CartDocument cartDocument = CartDocument.fromDomain(savedCart);
                    return elasticsearchCartRepository.save(cartDocument)
                            .thenReturn(savedCart)
                            .doOnError(error -> log.error("Erro ao indexar carrinho no Elasticsearch: {}", error.getMessage()));
                })
                .doOnSuccess(savedCart -> log.debug("Carrinho criado e indexado com sucesso: {}", savedCart.getId()));
    }

    public Mono<Cart> getCart(String cartId) {
        return mongoCartRepository.findById(cartId)
                .doOnSuccess(cart -> {
                    if (cart != null) {
                        log.debug("Carrinho encontrado: {}", cartId);
                    } else {
                        log.debug("Carrinho não encontrado: {}", cartId);
                    }
                });
    }

    public Flux<Cart> getCartsByUser(String userId) {
        return mongoCartRepository.findByUserId(userId)
                .doOnComplete(() -> log.debug("Recuperação de carrinhos concluída para o usuário: {}", userId));
    }

    public Mono<Cart> addItemToCart(String cartId, CartItem item) {
        return mongoCartRepository.findById(cartId)
                .flatMap(cart -> {
                    cart.addItem(item);
                    cart.setUpdatedAt(LocalDateTime.now());
                    return mongoCartRepository.save(cart)
                            .flatMap(updatedCart -> {
                                CartDocument cartDocument = CartDocument.fromDomain(updatedCart);
                                return elasticsearchCartRepository.save(cartDocument)
                                        .thenReturn(updatedCart)
                                        .doOnError(error -> log.error("Erro ao atualizar o carrinho em Elasticsearch: {}", error.getMessage()));
                            });
                })
                .doOnSuccess(updatedCart -> log.debug("Item added to cart successfully: {}", cartId));
    }

    public Mono<Cart> removeItemFromCart(String cartId, String itemId) {
        return mongoCartRepository.findById(cartId)
                .flatMap(cart -> {
                    cart.removeItem(itemId);
                    cart.setUpdatedAt(LocalDateTime.now());
                    return mongoCartRepository.save(cart)
                            .flatMap(updatedCart -> {
                                CartDocument cartDocument = CartDocument.fromDomain(updatedCart);
                                return elasticsearchCartRepository.save(cartDocument)
                                        .thenReturn(updatedCart)
                                        .doOnError(error -> log.error("Erro ao atualizar o carrinho no Elasticsearch: {}", error.getMessage()));
                            });
                })
                .doOnSuccess(updatedCart -> log.debug("Item removido do carrinho com sucesso: {}", cartId));
    }

    public Mono<Cart> updateCartStatus(String cartId, CartStatus status) {
        return mongoCartRepository.findById(cartId)
                .flatMap(cart -> {
                    cart.setStatus(status);
                    cart.setUpdatedAt(LocalDateTime.now());
                    if (status == CartStatus.COMPLETED) {
                        cart.setCompletedAt(LocalDateTime.now());
                    }
                    return mongoCartRepository.save(cart)
                            .flatMap(updatedCart -> {
                                CartDocument cartDocument = CartDocument.fromDomain(updatedCart);
                                return elasticsearchCartRepository.save(cartDocument)
                                        .thenReturn(updatedCart)
                                        .doOnError(error -> log.error("Erro ao atualizar o status do carrinho em Elasticsearch: {}", error.getMessage()));
                            });
                })
                .doOnSuccess(updatedCart -> log.debug("Status do carrinho atualizado com sucesso: {}", cartId));
    }

    public Mono<Cart> applyPromoCode(String cartId, String promoCode) {
        return mongoCartRepository.findById(cartId)
                .flatMap(cart -> {
                    cart.setPromoCode(promoCode);
                    cart.setUpdatedAt(LocalDateTime.now());
                    // calcula o desconto baseado no promoCode
                    return mongoCartRepository.save(cart)
                            .flatMap(updatedCart -> {
                                CartDocument cartDocument = CartDocument.fromDomain(updatedCart);
                                return elasticsearchCartRepository.save(cartDocument)
                                        .thenReturn(updatedCart)
                                        .doOnError(error -> log.error("Erro ao atualizar o carrinho com o código promocional em Elasticsearch: {}", error.getMessage()));
                            });
                })
                .doOnSuccess(updatedCart -> log.debug("Código promocional aplicado ao carrinho com sucesso: {}", cartId));
    }
}
