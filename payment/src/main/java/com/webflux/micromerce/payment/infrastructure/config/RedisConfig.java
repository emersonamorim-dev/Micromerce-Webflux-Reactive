package com.webflux.micromerce.payment.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webflux.micromerce.payment.domain.model.PaymentMethod;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${spring.redis.timeout:2000}")
    private int timeout;

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisHost);
        configuration.setPort(redisPort);

        SocketOptions socketOptions = SocketOptions.builder()
            .connectTimeout(Duration.ofMillis(timeout))
            .keepAlive(true)
            .tcpNoDelay(true)
            .build();

        ClientOptions clientOptions = ClientOptions.builder()
            .socketOptions(socketOptions)
            .autoReconnect(true)
            .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
            .publishOnScheduler(true)
            .requestQueueSize(1_000_000)
            .build();

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
            .clientOptions(clientOptions)
            .commandTimeout(Duration.ofMillis(timeout))
            .shutdownTimeout(Duration.ofSeconds(5))
            .build();

        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration, clientConfig);
        factory.setValidateConnection(true);
        return factory;
    }

    @Bean
    public ReactiveRedisTemplate<String, PaymentMethod> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory, 
            ObjectMapper objectMapper) {
        
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        
        // Cria um serializador com ObjectMapper pré-configurado
        ObjectMapper redisObjectMapper = objectMapper.copy();
        redisObjectMapper.findAndRegisterModules();
        Jackson2JsonRedisSerializer<PaymentMethod> valueSerializer = 
            new Jackson2JsonRedisSerializer<>(redisObjectMapper, PaymentMethod.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, PaymentMethod> builder =
            RedisSerializationContext.newSerializationContext(keySerializer);

        RedisSerializationContext<String, PaymentMethod> context = builder
            .value(valueSerializer)
            .hashKey(keySerializer)
            .hashValue(valueSerializer)
            .build();

        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, context);
    }
}
