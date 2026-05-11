package com.kriterion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI kriterionOpenAPI() {
		final String securitySchemeName = "bearerAuth";
		return new OpenAPI()
				.info(new Info()
						.title("Kriterion API")
						.version("v1.0")
						.description("Production-grade Financial Management API for Web and Mobile.")
						.contact(new io.swagger.v3.oas.models.info.Contact()
								.name("Kriterion Team")
								.email("support@kriterion.app")))
				.addServersItem(new io.swagger.v3.oas.models.servers.Server()
						.url("/api/v1")
						.description("Default Server URL"))
				.addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement()
						.addList(securitySchemeName))
				.components(new io.swagger.v3.oas.models.Components()
						.addSecuritySchemes(securitySchemeName,
								new io.swagger.v3.oas.models.security.SecurityScheme()
										.name(securitySchemeName)
										.type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")
										.description("Enter JWT token to access protected endpoints")));
	}
}
