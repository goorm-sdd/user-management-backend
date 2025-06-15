package org.example.goormssd.usermanagementbackend.service.member;

import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.domain.Member;
import org.example.goormssd.usermanagementbackend.repository.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean verify(Authentication authentication, String rawPassword) {
        String email = authentication.getName(); // 또는 사용자 구별값
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));
        return passwordEncoder.matches(rawPassword, member.getPassword());
    }
}
