package com.oilcommerce.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Oil Commerce API")
                .description("Enterprise ERP + E-Commerce backend for Nisha Pure Oils & Varshini Gold")
                .version("1.0.0")
                .contact(new Contact().name("Oil Commerce Team").email("support@oilcommerce.in"))
                .license(new License().name("Proprietary"))
            )
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication",
                    new SecurityScheme()
                        .name("Bearer Authentication")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Paste your JWT access token below (without 'Bearer' prefix)")
                )
            );
    }
}
