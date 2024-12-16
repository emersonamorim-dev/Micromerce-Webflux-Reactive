package com.webflux.micromerce.payment.infrastructure.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
@EnableR2dbcRepositories(
    basePackages = "com.webflux.micromerce.payment.infrastructure.repository"
)
@EnableTransactionManagement
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final PaymentReadingConverter paymentReadingConverter;

    public R2dbcConfig(
            @Value("${spring.data.r2dbc.host:mysql}") String host,
            @Value("${spring.data.r2dbc.port:3306}") int port,
            @Value("${spring.data.r2dbc.database:payment_db}") String database,
            @Value("${spring.data.r2dbc.username:root}") String username,
            @Value("${spring.data.r2dbc.password:root}") String password,
            PaymentReadingConverter paymentReadingConverter) {
        
        Assert.hasText(host, "O host não deve estar vazio");
        Assert.hasText(database, "O banco de dados não deve estar vazio");
        Assert.hasText(username, "O nome de usuário não deve estar vazio");
        Assert.hasText(password, "A senha não deve estar vazia");
        Assert.isTrue(port > 0, "A porta deve ser maior que 0");
        Assert.notNull(paymentReadingConverter, "PaymentReadingConverter não deve ser nulo");
        
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.paymentReadingConverter = paymentReadingConverter;
    }

    @Override
    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Object> converters = new ArrayList<>();
        converters.add(new UuidToStringConverter());
        converters.add(new StringToUuidConverter());
        converters.add(paymentReadingConverter);
        return new R2dbcCustomConversions(getStoreConversions(), converters);
    }

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        return new ConnectionPool(
            ConnectionPoolConfiguration.builder(
                ConnectionFactories.get(ConnectionFactoryOptions.builder()
                    .option(DRIVER, "mysql")
                    .option(HOST, host)
                    .option(PORT, port)
                    .option(USER, username)
                    .option(PASSWORD, password)
                    .option(DATABASE, database)
                    .build()))
                .maxIdleTime(Duration.ofMinutes(30))
                .maxSize(20)
                .initialSize(5)
                .maxCreateConnectionTime(Duration.ofSeconds(5))
                .build()
        );
    }

    @Bean
    ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }
}
