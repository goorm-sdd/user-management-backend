package org.example.goormssd.usermanagementbackend.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.goormssd.usermanagementbackend.dto.request.FindEmailRequestDto;
import org.example.goormssd.usermanagementbackend.dto.request.FindPasswordRequestDto;
import org.example.goormssd.usermanagementbackend.dto.request.LoginRequestDto;
import org.example.goormssd.usermanagementbackend.dto.response.ApiResponseDto;
import org.example.goormssd.usermanagementbackend.dto.response.FindEmailResponseDto;
import org.example.goormssd.usermanagementbackend.dto.response.LoginResponseDto;
import org.example.goormssd.usermanagementbackend.dto.response.RefreshTokenDto;
import org.example.goormssd.usermanagementbackend.service.AuthService;
import org.example.goormssd.usermanagementbackend.service.dto.LoginResult;
import org.example.goormssd.usermanagementbackend.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api") // API 버전 관리
@RequiredArgsConstructor // Lombok, final 이나 @NonNull 필드에 대해 생성자 자동 생성
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // @RequiredArgsConstructor를 사용하면 @Autowired가 필요없음
    // 가독성과 코드 간결성을 위해 @RequiredArgsConstructor를 사용하는 뱡향이 나아보임
    //    @Autowired
    //    public AuthController(AuthService authService) {
    //        this.authService = authService;
    //    }

    @PostMapping("/auth/signin")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(
            @RequestBody LoginRequestDto loginRequest,
            HttpServletResponse response) {

        // var 사용을 통해 타입 추론을 활용할 수 있지만, 명시적인 타입 선언이 가독성에 더 좋을 수 있음
        // 팀 프로젝트에서의 중요한 로직에서는 var 사용을 지양하고, 타입을 명시하는게 좋을 수 있음
        // var LogingResult result = authService.loginWithUserInfo(loginRequest);
        LoginResult result = authService.loginWithUserInfo(loginRequest);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 7)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

//
//        LoginResponseDto body = new LoginResponseDto(result.getAccessToken(), result.getUser());
//        ApiResponseDto<LoginResponseDto> apiResponse = new ApiResponseDto<>(200, "Login successful", body);
//
//        return ResponseEntity.ok(apiResponse);

        LoginResponseDto responseBody = new LoginResponseDto(result.getAccessToken(), result.getUser());

        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "Login successful", responseBody)
        );
    }

    @PostMapping("/signout")
    public ResponseEntity<ApiResponseDto<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

//        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return ResponseEntity.status(401).body(new ApiResponseDto<>(401, "Unauthorized", null));
//        }

// 변수명 accessToken을 사용하여 가독성을 높임
//        String token = authHeader.substring(7);
//        authService.logout(token);
//        String accessToken = authHeader.substring(7);
//        authService.logout(accessToken);

        // AccessToken 검증
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

        // 디버깅용: 모든 쿠키 로깅
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                log.debug("[쿠키] {} = {}", c.getName(), c.getValue());
            }
        } else {
            log.debug("[쿠키] 없음");
        }

        // 쿠키에서 refreshToken 추출
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

        // refreshToken 있으면 서비스에 전달하여 DB 삭제
        if (refreshToken != null && !refreshToken.isBlank()) {
            authService.logout(refreshToken);
            log.info("[로그아웃] 서비스에 refreshToken 전달 완료");
        } else {
            log.warn("[로그아웃] refreshToken이 전달되지 않음");
        }

        // 클라이언트에 저장된 RefreshToken 제거
        // api 명세에 써있지 않은 부분 로그아웃 시에는 RefreshToken을 제거 해야할 필요가 있음
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

    @PostMapping("/auth/token/refresh")
    public ResponseEntity<ApiResponseDto<RefreshTokenDto>> refreshToken(
            HttpServletRequest request) {

        // 쿠키에서 refreshToken 추출
        String refreshToken = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(c -> "refreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "refreshToken이 전달되지 않음"
                ));

        // DB에서 유효 여부 확인
        if (!authService.isValidRefreshToken(refreshToken)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 refreshToken"
            );
        }

        // 새 AccessToken 발급
        String email = jwtUtil.extractEmail(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(email);


        RefreshTokenDto body = new RefreshTokenDto(newAccessToken);
        ApiResponseDto<RefreshTokenDto> apiResponse =
                new ApiResponseDto<>(200, "Token refreshed", body);

        return ResponseEntity.ok(apiResponse);
    }
    @PostMapping("/auth/find/email")
    public ResponseEntity<ApiResponseDto<FindEmailResponseDto>> findEmail(@RequestBody FindEmailRequestDto request) {
        String email = authService.findEmailByUsernameAndPhone(request);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "Email (ID) retrieved successfully.", new FindEmailResponseDto(email)));
    }

    @PostMapping("/auth/find/password")
    public ResponseEntity<ApiResponseDto<Void>> resetPassword(@RequestBody FindPasswordRequestDto request) {
        authService.resetPasswordAndSendEmail(request);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "Temporary password has been sent via email.", null));
    }


}