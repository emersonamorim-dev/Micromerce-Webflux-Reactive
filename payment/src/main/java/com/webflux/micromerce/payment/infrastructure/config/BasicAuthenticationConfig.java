package com.webflux.micromerce.payment.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class BasicAuthenticationConfig {

    @Value("${payment.security.admin.username:admin}")
    private String adminUsername;

    @Value("${payment.security.admin.password:admin}")
    private String adminPassword;

    @Value("${payment.security.api.username:api}")
    private String apiUsername;

    @Value("${payment.security.api.password:api}")
    private String apiPassword;

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        PasswordEncoder encoder = passwordEncoder();
        List<UserDetails> users = new ArrayList<>();

        // Usuário administrador com acesso total
        UserDetails adminUser = User.withUsername(adminUsername)
                .password(encoder.encode(adminPassword))
                .roles("ADMIN", "API")
                .authorities("ROLE_ADMIN", "ROLE_API", 
                           "PAYMENT_READ", "PAYMENT_WRITE", 
                           "PAYMENT_DELETE", "PAYMENT_ADMIN")
                .build();
        users.add(adminUser);

        // Usuário da API com acesso limitado
        UserDetails apiUser = User.withUsername(apiUsername)
                .password(encoder.encode(apiPassword))
                .roles("API")
                .authorities("ROLE_API", "PAYMENT_READ", "PAYMENT_WRITE")
                .build();
        users.add(apiUser);

        return new MapReactiveUserDetailsService(users);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}