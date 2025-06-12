package org.example.goormssd.usermanagementbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("회원 관리 서비스 API")
                        .description("Spring Boot + JWT 기반 회원관리 API 문서입니다.")
                        .version("v1.0"));
    }
}
