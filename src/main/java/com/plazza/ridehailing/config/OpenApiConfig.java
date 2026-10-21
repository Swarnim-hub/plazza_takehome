package com.plazza.ridehailing.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ride Hailing Platform API")
                        .version("1.0.0")
                        .description("Production-grade backend service for an on-demand ride-hailing platform (like Uber). " +
                                "Supports tiered distance pricing, car types (Hatchback/Sedan), free automatic vehicle upgrades, " +
                                "coupons, pluggable surge pricing, configurable matching strategies (nearest vs highest-rated), " +
                                "ride cancellation policies, and race-condition safety.")
                        .contact(new Contact()
                                .name("Engineering Team")
                                .email("dev@ridehailing.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
