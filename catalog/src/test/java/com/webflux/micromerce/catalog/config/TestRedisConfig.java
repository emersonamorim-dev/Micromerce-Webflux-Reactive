package com.webflux.micromerce.catalog.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.webflux.micromerce.catalog.domain.model.Product;
import io.lettuce.core.ClientOptions;

@TestConfiguration
public class TestRedisConfig {

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName("redis");  // Nome do serviço Redis no Docker Compose
        configuration.setPort(6379);

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
            .clientOptions(ClientOptions.builder().build())
            .build();

        return new LettuceConnectionFactory(configuration, clientConfig);
    }

    @Bean
    @Primary
    public ReactiveRedisTemplate<String, Product> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Product> valueSerializer = new Jackson2JsonRedisSerializer<>(Product.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, Product> builder =
            RedisSerializationContext.newSerializationContext(keySerializer);

        RedisSerializationContext<String, Product> context = builder
            .value(valueSerializer)
            .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
