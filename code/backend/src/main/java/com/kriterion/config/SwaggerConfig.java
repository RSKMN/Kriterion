package com.kriterion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI kriterionOpenAPI() {
		return new OpenAPI().info(new Info().title("Kriterion API").version("v1").description("Kriterion backend API contract"));
	}
}
