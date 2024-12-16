package com.webflux.micromerce.catalog.infrastructure.config;

import com.webflux.micromerce.catalog.infrastructure.monitoring.KibanaMetricsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class PostgresMetricsService {

    private final KibanaMetricsSender kibanaMetricsSender;
    private final DatabaseClient databaseClient;

    @Autowired
    public PostgresMetricsService(KibanaMetricsSender kibanaMetricsSender,
                                @Lazy DatabaseClient databaseClient) {
        this.kibanaMetricsSender = kibanaMetricsSender;
        this.databaseClient = databaseClient;
    }

    public Mono<Void> monitorPostgresMetrics() {
        String query = "SELECT pg_stat_database.datname, pg_stat_database.numbackends, " +
                      "pg_stat_database.xact_commit, pg_stat_database.xact_rollback " +
                      "FROM pg_stat_database;";

        return databaseClient.sql(query)
                .map((row, metadata) -> {
                    String dbName = row.get("datname", String.class);
                    int numBackends = row.get("numbackends", Integer.class);
                    int xactCommit = row.get("xact_commit", Integer.class);
                    int xactRollback = row.get("xact_rollback", Integer.class);

                    return String.format("DB: %s, Backends: %d, Commits: %d, Rollbacks: %d",
                            dbName, numBackends, xactCommit, xactRollback);
                })
                .all()
                .collectList()
                .flatMap(results -> {
                    String metrics = "Postgres Metrics: " + results.stream()
                            .collect(Collectors.joining("; "));
                    return kibanaMetricsSender.sendMetricsToKibana("Postgres", metrics);
                })
                .then();
    }

    public Mono<Void> sendConnectionMetrics(String connectionInfo) {
        return kibanaMetricsSender.sendMetricsToKibana("Postgres", connectionInfo);
    }
}
