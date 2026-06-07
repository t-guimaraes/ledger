package com.tguimaraes.ledger.core.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("Ledger API")
                    .description("Financial Ledger API built with Kotlin and Hexagonal Architecture")
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("Thiago Guimarães")
                            .email("tguimaraes.dev@gmail.com")
                    )
            )
}