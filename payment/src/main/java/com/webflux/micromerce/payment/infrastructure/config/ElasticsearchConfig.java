package com.webflux.micromerce.payment.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import java.time.Duration;
import java.util.Collections;

@Configuration
@EnableReactiveElasticsearchRepositories(
        basePackages = {
            "com.webflux.micromerce.payment.infrastructure.repository"
        }
)
public class ElasticsearchConfig extends ReactiveElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris:http://elasticsearch:9200}")
    private String elasticsearchUri;

    @Value("${spring.elasticsearch.connection-timeout:5s}")
    private Duration connectionTimeout;

    @Value("${spring.elasticsearch.socket-timeout:30s}")
    private Duration socketTimeout;

    @Override
    public ClientConfiguration clientConfiguration() {
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))
                .build();

        return ClientConfiguration.builder()
                .connectedTo(elasticsearchUri.replace("http://", ""))
                .withConnectTimeout(connectionTimeout)
                .withSocketTimeout(socketTimeout)
                .withClientConfigurer(builder -> {
                    ((WebClient.Builder) builder)
                            .exchangeStrategies(exchangeStrategies)
                            .defaultHeader("Accept", "application/vnd.elasticsearch+json;compatible-with=8")
                            .defaultHeader("Content-Type", "application/vnd.elasticsearch+json;compatible-with=8");
                    return builder;
                })
                .build();
    }

    @Bean
    @Override
    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
        return new ElasticsearchCustomConversions(Collections.emptyList());
    }
}
