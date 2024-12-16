package com.webflux.micromerce.cart.infrastructure.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.webflux.micromerce.cart.infrastructure.monitoring.KibanaMetricsSender;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MongoMetricsService {

    private final KibanaMetricsSender kibanaMetricsSender;
    private final MongoClient mongoClient;

    @Autowired
    public MongoMetricsService(KibanaMetricsSender kibanaMetricsSender,
                             @Lazy ReactiveMongoTemplate mongoTemplate,
                             MongoClient mongoClient) {
        this.kibanaMetricsSender = kibanaMetricsSender;
        this.mongoClient = mongoClient;
    }

    public Mono<Void> monitorMongoMetrics() {
        return Mono.from(mongoClient.getDatabase("admin")
                .runCommand(new Document("serverStatus", 1)))
                .flatMap(result -> {
                    Document connections = result.get("connections", Document.class);
                    Document opcounters = result.get("counters", Document.class);

                    String metrics = String.format(
                            "MongoDB Metrics - Connections: [current: %d, available: %d], " +
                            "Operations: [insert: %d, query: %d, update: %d, delete: %d]",
                            connections.getInteger("current"),
                            connections.getInteger("available"),
                            opcounters.getInteger("insert"),
                            opcounters.getInteger("query"),
                            opcounters.getInteger("update"),
                            opcounters.getInteger("delete")
                    );

                    return kibanaMetricsSender.sendMetricsToKibana("MongoDB", metrics);
                });
    }

    public Mono<Void> sendConnectionMetrics(String connectionInfo) {
        return kibanaMetricsSender.sendMetricsToKibana("MongoDB", connectionInfo);
    }
}
