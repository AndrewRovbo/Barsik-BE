package com.barsik.backend.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI barsikOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Barsik API")
                .description("API для Barsik, платформы поиска пет-ситтеров")
                .version("v1.0")
                .contact(new Contact().name("Barsik Team").email("support@barsik.example"))
                .license(new License().name("MIT").url("https://opensource.org/licenses/MIT"))
            );
    }
}
