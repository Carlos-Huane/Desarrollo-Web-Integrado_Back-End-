package com.grupo6.intranet.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI intranetOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Intranet TelecoPerú — API")
                        .description("Gestión de incidencias, base de conocimiento, dashboard y exportación.")
                        .version("1.0.0")
                        .contact(new Contact().name("Grupo 6 — Desarrollo Web Integrado")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT del endpoint /api/auth/login")));
    }
}
