package com.webflux.micromerce.catalog.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER = 
        new PostgreSQLContainer<>("postgres:16.1")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    private static final GenericContainer<?> REDIS_CONTAINER = 
        new GenericContainer<>(DockerImageName.parse("redis:7.2"))
            .withExposedPorts(6379);

    @Container
    private static final KafkaContainer KAFKA_CONTAINER = 
        new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.1"));

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL Properties
        registry.add("spring.r2dbc.url", () -> 
            String.format("r2dbc:postgresql://%s:%d/%s",
                POSTGRES_CONTAINER.getHost(),
                POSTGRES_CONTAINER.getFirstMappedPort(),
                POSTGRES_CONTAINER.getDatabaseName()));
        registry.add("spring.r2dbc.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.r2dbc.password", POSTGRES_CONTAINER::getPassword);
        
        // Redis Properties
        registry.add("spring.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.redis.port", REDIS_CONTAINER::getFirstMappedPort);
        
        // Kafka Properties
        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        
        // R2DBC Pool Configuration
        registry.add("spring.r2dbc.pool.initial-size", () -> "5");
        registry.add("spring.r2dbc.pool.max-size", () -> "10");
        registry.add("spring.r2dbc.pool.max-idle-time", () -> "30m");
        
        // SQL Initialization
        registry.add("spring.sql.init.mode", () -> "always");
        registry.add("spring.sql.init.schema-locations", () -> "classpath:schema.sql");
    }
}
