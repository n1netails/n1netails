package com.n1netails.n1netails.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${info.application.version}")
    private String version;

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("N1ne Tails - API")
                        .description("N1ne Tails is an open-source project that provides practical alerts and monitoring for applications.")
                        .version(version)
                        .contact(new Contact()
                                .name("Contrivance Creations")
                        )
                        .license(new License()
                                .name("AGPL-3.0")
                                .url("https://github.com/n1netails/n1netails/blob/main/LICENSE")
                        )
                );
    }

    @Bean
    public OpenApiCustomizer securityOpenApiCustomizer() {
        return openApi -> {
            // Add the security scheme
            openApi.getComponents()
                    .addSecuritySchemes(SECURITY_SCHEME_NAME, createBearerSecurityScheme());

            // Apply the scheme globally to all operations
            openApi.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
        };
    }

    private SecurityScheme createBearerSecurityScheme() {
        return new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter JWT Bearer token");
    }
}
