package org.example.goormssd.usermanagementbackend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.dto.request.SignupRequestDto;
import org.example.goormssd.usermanagementbackend.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        // 회원 저장 및 이메일 인증 처리까지 서비스에서 수행
        memberService.signup(requestDto);
        return ResponseEntity.ok("회원가입이 완료되었습니다. 이메일을 확인해주세요.");
    }
}
