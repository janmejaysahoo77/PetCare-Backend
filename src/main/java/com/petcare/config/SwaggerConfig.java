package com.petcare.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI petCareOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PetCare+ API")
                        .description("""
                                ## PetCare+ Smart Pet Ecosystem – Backend API

                                ### Authentication
                                All protected endpoints require a Firebase ID token passed as a Bearer token:
                                ```
                                Authorization: Bearer <firebase_id_token>
                                ```

                                ### Base URL
                                `/api/v1`
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("PetCare+ Team")
                                .email("petcareplus@hackathon.dev"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH,
                                new SecurityScheme()
                                        .name(BEARER_AUTH)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("Firebase JWT")
                                        .description("Enter your Firebase ID token")));
    }
}
