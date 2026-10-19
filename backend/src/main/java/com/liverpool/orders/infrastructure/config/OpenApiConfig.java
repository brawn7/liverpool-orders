package com.liverpool.orders.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ordersOpenApi() {
        return new OpenAPI().info(new Info()
            .title("Liverpool Orders API")
            .version("v1")
            .description("API REST para la gestión de pedidos y usuarios realizados desde app y sitio web.")
            .contact(new Contact().name("Braulio Toledo").url("https://github.com/brawn7")));
    }
}