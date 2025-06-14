package org.example.goormssd.usermanagementbackend.config;

import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                .addTagsItem(new Tag().name("Auth").description("인증 관련 API"))
                .addTagsItem(new Tag().name("User").description("회원 관련 API"))
                .addTagsItem(new Tag().name("Admin").description("관리자 전용 API"))
                .addTagsItem(new Tag().name("Not Used").description("현재 프론트에서 사용하지 않지만 참고용으로 유지되는 API"));
    }
}
