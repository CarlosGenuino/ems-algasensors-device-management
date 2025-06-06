package com.algaworks.algasensors.device.management.api.config.web;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Device Management api",
                version = "1.0",
                description = "API para gestão de dispositivos"
        )
)
@Configuration
public class SwaggerConfig {
}
