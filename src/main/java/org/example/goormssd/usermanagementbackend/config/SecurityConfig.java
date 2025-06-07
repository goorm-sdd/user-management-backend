package org.example.goormssd.usermanagementbackend.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.goormssd.usermanagementbackend.util.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
//
//    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
//        this.jwtFilter = jwtFilter;
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화: JWT 사용 시 세션 상태 없음
                .csrf(AbstractHttpConfigurer::disable)
                // H2 Console 프레임 옵션 허용
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                // Stateless 세션 관리
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 권한 정책 정의
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 허용
                        .requestMatchers("/api/auth/**", "/h2-console/**").permitAll()
                        // 사용자 권한 필요
                        .requestMatchers("/api/users/**").hasAnyRole("USER", "ADMIN")
                        // 관리자 권한 필요
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 로그아웃은 인증된 사용자만
                        .requestMatchers("/api/signout").authenticated()
                        // 그 외 요청 인증 필요
                        .anyRequest().authenticated()
                )

                // JWT 필터 등록
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, expt) -> {
                            log.warn("인증 실패: {}", expt.getMessage());
                            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다.");
                        })
                        .accessDeniedHandler((req, res, expt) -> {
                            log.warn("접근 거부: {}", expt.getMessage());
                            res.sendError(HttpServletResponse.SC_FORBIDDEN, "접근이 거부되었습니다.");
                        })
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
