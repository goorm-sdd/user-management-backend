package org.example.goormssd.usermanagementbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.dto.request.PasswordVerifyRequestDto;
import org.example.goormssd.usermanagementbackend.dto.response.PasswordVerifyResponseDto;
import org.example.goormssd.usermanagementbackend.service.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/password")
@RequiredArgsConstructor
public class PasswordController {
    private final PasswordService passwordService;

    @PostMapping("/verify")
    public ResponseEntity<PasswordVerifyResponseDto> verifyPassword(
            @Valid @RequestBody PasswordVerifyRequestDto requestDto,
            Authentication authentication
    ) {
        boolean isValid = passwordService.verify(authentication, requestDto.getPassword());
        if (isValid) {
            return ResponseEntity.ok(new PasswordVerifyResponseDto(true, "비밀번호가 일치합니다."));
        } else {
            return ResponseEntity
                    .status(401)
                    .body(new PasswordVerifyResponseDto(false, "비밀번호가 일치하지 않습니다."));
        }
    }
}
