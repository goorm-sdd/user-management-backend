package org.example.goormssd.usermanagementbackend.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.dto.request.EmailCheckRequestDto;
import org.example.goormssd.usermanagementbackend.dto.request.SignupRequestDto;
import org.example.goormssd.usermanagementbackend.dto.response.ApiResponseDto;
import org.example.goormssd.usermanagementbackend.dto.response.MyProfileResponseDto;
import org.example.goormssd.usermanagementbackend.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "User API", description = "일반 사용자 API")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/auth/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        // 회원 저장 및 이메일 인증 처리까지 서비스에서 수행
        memberService.signup(requestDto);
        return ResponseEntity.ok("회원가입이 완료되었습니다. 이메일을 확인해주세요.");
    }

    @PostMapping("/auth/email")
    public ResponseEntity<Map<String, Object>> checkEmailDuplicate(@RequestBody @Valid EmailCheckRequestDto requestDto) {
        boolean exists = memberService.isEmailDuplicate(requestDto.getEmail());

        Map<String, Object> response = new HashMap<>();
        if (exists) {
            response.put("status", 409);
            response.put("message", "중복된 이메일입니다.");
            return ResponseEntity.status(409).body(response);
        } else {
            response.put("status", 200);
            response.put("message", "사용가능한 이메일입니다.");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/users/me")
    public ResponseEntity<ApiResponseDto<MyProfileResponseDto>> getMyProfile() {

        MyProfileResponseDto responseDto = memberService.getMyProfile();

        ApiResponseDto<MyProfileResponseDto> response = new ApiResponseDto<>(
                200,
                "User information retrieved successfully.",
                responseDto
        );

        return ResponseEntity.ok(response);
    }
}
