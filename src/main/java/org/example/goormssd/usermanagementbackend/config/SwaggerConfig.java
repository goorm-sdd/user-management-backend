package org.example.goormssd.usermanagementbackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("JWT", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))
                        .addSecuritySchemes("ReauthToken", new SecurityScheme()
                                .name("reauthToken")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)))
                .addSecurityItem(new SecurityRequirement()
                        .addList("JWT")
                        .addList("ReauthToken"))
                .info(new Info()
                        .title("회원 관리 서비스 API")
                        .description("Spring Boot + JWT 기반 회원관리 API 문서입니다.")
                        .version("v1.0"))
                .tags(List.of(
                        new Tag().name("관리자 API").description("관리자 전용 API입니다."),
                        new Tag().name("프론트 미구현 API").description("사용되지 않는 API 모음입니다."),
                        new Tag().name("인증 API").description("회원가입, 로그인, 인증 관련 API입니다."),
                        new Tag().name("회원 API").description("일반 회원 기능 관련 API입니다.")
                ));
    }
}
