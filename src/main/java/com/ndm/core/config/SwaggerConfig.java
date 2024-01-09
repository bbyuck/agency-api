package com.ndm.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Florecer API Spec",
                description = "개인 프로젝트 Florecer API 명세서",
                version = "v1"
        )
)
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {


}
