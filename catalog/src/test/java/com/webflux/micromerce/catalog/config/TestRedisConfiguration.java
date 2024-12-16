package com.webflux.micromerce.catalog.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

@TestConfiguration
public class TestRedisConfiguration {
    
    @Bean
    @Primary
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate() {
        return Mockito.mock(ReactiveRedisTemplate.class);
    }
}
