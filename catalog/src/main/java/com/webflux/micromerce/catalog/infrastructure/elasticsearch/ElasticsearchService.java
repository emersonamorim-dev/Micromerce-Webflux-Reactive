package com.webflux.micromerce.catalog.infrastructure.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ElasticsearchService {

    private final ElasticsearchClient elasticsearchClient;

    public ElasticsearchService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public String checkClusterHealth() throws IOException {
        return elasticsearchClient.cluster().health().status().jsonValue();
    }
}
