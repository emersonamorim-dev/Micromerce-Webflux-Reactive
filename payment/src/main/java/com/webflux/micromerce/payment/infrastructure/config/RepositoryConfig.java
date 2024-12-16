package com.webflux.micromerce.payment.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableR2dbcRepositories(
    basePackages = "com.webflux.micromerce.payment.infrastructure.repository.r2dbc",
    considerNestedRepositories = true
)
@EnableRedisRepositories(
    basePackages = "com.webflux.micromerce.payment.infrastructure.repository.redis",
    considerNestedRepositories = true
)
public class RepositoryConfig {
}
