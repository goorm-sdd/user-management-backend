package org.example.goormssd.usermanagementbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.goormssd.usermanagementbackend.dto.request.LoginRequestDto;
import org.example.goormssd.usermanagementbackend.dto.response.ApiResponseDto;
import org.example.goormssd.usermanagementbackend.dto.response.LoginResponseDto;
import org.example.goormssd.usermanagementbackend.service.AuthAdminService;
import org.example.goormssd.usermanagementbackend.service.dto.LoginResult;
import org.example.goormssd.usermanagementbackend.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api") // API 버전 관리
@RequiredArgsConstructor
@Tag(name = "Admin API", description = "관리자 권한이 필요한 API")
public class AuthAdminController {

    private final AuthAdminService adminService;
    private final JwtUtil jwtUtil;

    @PostMapping("/auth/admin/signin")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(
            @RequestBody LoginRequestDto loginRequest,
            HttpServletResponse response) {

        LoginResult result = adminService.loginWithUserInfo(loginRequest);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 7)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());


        LoginResponseDto responseBody = new LoginResponseDto(result.getAccessToken(), result.getUser());

        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "Login successful", responseBody)
        );
    }

    @PostMapping("/admin/signout")
    public ResponseEntity<ApiResponseDto<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(401, "AccessToken이 필요합니다.", null));
        }
        String accessToken = authHeader.substring(7);
        if (!jwtUtil.validateToken(accessToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(401, "AccessToken이 유효하지 않습니다.", null));
        }

        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                log.debug("[쿠키] {} = {}", c.getName(), c.getValue());
            }
        } else {
            log.debug("[쿠키] 없음");
        }

        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equalsIgnoreCase(cookie.getName().trim())) {
                    refreshToken = cookie.getValue();
                    log.debug("[로그아웃] refreshToken 추출: {}", refreshToken);
                    break;
                }
            }
        }

        if (refreshToken != null && !refreshToken.isBlank()) {
            adminService.logout(refreshToken);
            log.info("[로그아웃] 서비스에 refreshToken 전달 완료");
        } else {
            log.warn("[로그아웃] refreshToken이 전달되지 않음");
        }

        ResponseCookie expiredCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());

        return ResponseEntity.ok(new ApiResponseDto<>(200, "Logout successful.", null));
    }
}
