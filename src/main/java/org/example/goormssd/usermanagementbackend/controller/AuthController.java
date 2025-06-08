package org.example.goormssd.usermanagementbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.goormssd.usermanagementbackend.dto.request.*;
import org.example.goormssd.usermanagementbackend.dto.response.ApiResponseDto;
import org.example.goormssd.usermanagementbackend.dto.response.FindEmailResponseDto;
import org.example.goormssd.usermanagementbackend.dto.response.LoginResponseDto;
import org.example.goormssd.usermanagementbackend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(@RequestBody LoginRequestDto loginRequest, HttpServletResponse response) {
        var result = authService.loginWithUserInfo(loginRequest);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 7)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        LoginResponseDto body = new LoginResponseDto(result.getAccessToken(), result.getUser());
        ApiResponseDto<LoginResponseDto> apiResponse = new ApiResponseDto<>(200, "Login successful", body);

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/signout")
    public ResponseEntity<ApiResponseDto<Void>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(new ApiResponseDto<>(401, "Unauthorized", null));
        }
        String token = authHeader.substring(7);
        authService.logout(token);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "Logout successful.", null));
    }

    @PostMapping("/find/email")
    public ResponseEntity<ApiResponseDto<FindEmailResponseDto>> findEmail(@RequestBody FindEmailRequestDto request) {
        String email = authService.findEmailByUsernameAndPhone(request);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "Email (ID) retrieved successfully.", new FindEmailResponseDto(email)));
    }

    @PostMapping("/find/password")
    public ResponseEntity<ApiResponseDto<Void>> resetPassword(@RequestBody FindPasswordRequestDto request) {
        authService.resetPasswordAndSendEmail(request);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "Temporary password has been sent via email.", null));
    }

}