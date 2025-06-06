package org.example.goormssd.usermanagementbackend.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.dto.request.LoginRequestDto;
import org.example.goormssd.usermanagementbackend.dto.response.ApiResponseDto;
import org.example.goormssd.usermanagementbackend.dto.response.LoginResponseDto;
import org.example.goormssd.usermanagementbackend.service.AuthService;
import org.example.goormssd.usermanagementbackend.service.dto.LoginResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api") // API 버전 관리
@RequiredArgsConstructor // Lombok, final 이나 @NonNull 필드에 대해 생성자 자동 생성
public class AuthController {

    private final AuthService authService;

    // @RequiredArgsConstructor를 사용하면 @Autowired가 필요없음
    // 가독성과 코드 간결성을 위해 @RequiredArgsConstructor를 사용하는 뱡향이 나아보임
    //    @Autowired
    //    public AuthController(AuthService authService) {
    //        this.authService = authService;
    //    }

    @PostMapping("/auth/signin") // 로그인 엔드포인트
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(@RequestBody LoginRequestDto loginRequest, HttpServletResponse response) {

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

        // 모든 쿠키 출력 (디버깅)
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                System.out.println("[쿠키] " + c.getName() + " = " + c.getValue());
            }
        } else {
            System.out.println("[쿠키] 없음");
        }

        // 쿠키에서 refreshToken 추출 (파싱 개선)
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equalsIgnoreCase(cookie.getName().trim())) {
                    refreshToken = cookie.getValue();
                    System.out.println("[로그아웃] refreshToken 추출: " + refreshToken);
                    break;
                }
            }
        }

        // refreshToken이 존재하면 DB에서 삭제 처리
        if (refreshToken != null && !refreshToken.isBlank()) {
            authService.logout(refreshToken);
            System.out.println("[로그아웃] 서비스에 refreshToken 전달 완료");
        } else {
            System.out.println("[로그아웃] refreshToken이 전달되지 않음");
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
}