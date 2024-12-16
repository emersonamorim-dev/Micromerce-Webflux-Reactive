package com.webflux.micromerce.cart.infrastructure.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.webflux.micromerce.cart.infrastructure.repository")
public class MongoDBConfig extends AbstractReactiveMongoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MongoDBConfig.class);

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    @Bean
    public MongoClient reactiveMongoClient() {
        log.info("Initializing MongoDB client with URI: {}", mongoUri.replaceAll("mongodb://.*@", "mongodb://*****@"));
        
        ConnectionString connectionString = new ConnectionString(mongoUri);
        
        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .applyToConnectionPoolSettings(builder ->
                builder.maxConnectionIdleTime(1, TimeUnit.MINUTES)
                    .maxSize(20)
                    .minSize(5)
                    .maxWaitTime(120, TimeUnit.SECONDS)
            )
            .applyToSocketSettings(builder ->
                builder.connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
            )
            .applyToServerSettings(builder ->
                builder.heartbeatFrequency(10, TimeUnit.SECONDS)
                    .minHeartbeatFrequency(500, TimeUnit.MILLISECONDS)
            )
            .build();

        return MongoClients.create(settings);
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(MongoClient mongoClient) {
        log.info("Criando ReactiveMongoTemplate para banco de dados: {}", database);
        return new ReactiveMongoTemplate(mongoClient, database);
    }
}
