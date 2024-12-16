package com.webflux.micromerce.payment;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import java.net.URI;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@EnableWebFlux
@SpringBootApplication(scanBasePackages = {
    "com.webflux.micromerce.payment.infrastructure",
    "com.webflux.micromerce.payment.application",
    "com.webflux.micromerce.payment.presentation",
    "com.webflux.micromerce.payment.domain"
})
@OpenAPIDefinition(
    info = @Info(
        title = "Payment Service API",
        version = "1.0",
        description = "Reactive Serviço de processamento de pagamentos para Micromerce Platform",
        contact = @Contact(
            name = "EmerDev Team",
            email = "emerson_tecno@hotmail.com",
            url = "https://github.com/emersonamorim-dev"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0"
        )
    ),
    servers = {
        @Server(url = "/", description = "Default Server URL")
    }
)
public class PaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> swaggerRouterFunction() {
        return route(GET("/"), request ->
            ServerResponse.temporaryRedirect(URI.create("/swagger-ui/index.html")).build());
    }
}
