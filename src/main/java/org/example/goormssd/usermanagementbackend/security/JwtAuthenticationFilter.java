package org.example.goormssd.usermanagementbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    private static final List<String> EXCLUDE_URLS = Arrays.asList(
            "/api/auth",
            "/h2-console"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDE_URLS.stream().anyMatch(path::startsWith);
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.debug("[JwtFilter] Request URI: {}", request.getRequestURI());

        Optional<String> tokenOpt = resolveToken(request);
        if (tokenOpt.isPresent()) {
            String token = tokenOpt.get();

            // 민감 경로인지 확인
            String path = request.getRequestURI();
            String method = request.getMethod();
            boolean isSensitivePath = "PATCH".equals(method) && (
                    path.equals("/api/users/me/password") ||
                            path.equals("/api/users/me/phone") ||
                            path.equals("/api/users/me") ||
                            path.startsWith("/api/admin/users/status")
            );

            boolean isValid = isSensitivePath
                    ? jwtUtil.validateReauthToken(token)
                    : jwtUtil.validateAccessToken(token);

            if (isValid) {
                authenticateToken(token, request);
            } else {
                log.warn("[JwtFilter] 유효하지 않은 토큰: {}", token);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return Optional.of(bearer.substring(7));
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }


    private void authenticateToken(String token, HttpServletRequest request) {
        try {
            String email = jwtUtil.extractEmail(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.info("[JwtFilter] Authentication successful for user: {}", email);
        } catch (Exception ex) {
            log.warn("[JwtFilter] Token authentication failed: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        }
    }
}
