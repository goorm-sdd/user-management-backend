package org.example.goormssd.usermanagementbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.dto.request.PasswordVerifyRequestDto;
import org.example.goormssd.usermanagementbackend.dto.response.ReauthResponseDto;
import org.example.goormssd.usermanagementbackend.service.PasswordService;
import org.example.goormssd.usermanagementbackend.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/password")
@RequiredArgsConstructor
public class PasswordController {
    private final PasswordService passwordService;
    private final JwtUtil jwtUtil;

    @PostMapping("/verify")
    public ResponseEntity<ReauthResponseDto> verifyPassword(
            @Valid @RequestBody PasswordVerifyRequestDto requestDto,
            Authentication auth) {
        boolean ok = passwordService.verify(auth, requestDto.getPassword());
        if (!ok) {
            return ResponseEntity.status(401)
                    .body(new ReauthResponseDto(false, null, "비밀번호 불일치"));
        }
        String token = jwtUtil.generateReauthToken(
                (UserDetails) auth.getPrincipal());
        return ResponseEntity.ok(
                new ReauthResponseDto(true, token, "재인증 토큰 발급"));
    }
}
