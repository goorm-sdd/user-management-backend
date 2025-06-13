package org.example.goormssd.usermanagementbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.domain.Member;
import org.example.goormssd.usermanagementbackend.dto.request.SignupRequestDto;
import org.example.goormssd.usermanagementbackend.dto.response.MyProfileResponseDto;
import org.example.goormssd.usermanagementbackend.repository.MemberRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final EmailService emailService;

    public void signup(SignupRequestDto requestDto) {
        if (!requestDto.getPassword().equals(requestDto.getPasswordCheck())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 회원 생성
        Member member = Member.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .phoneNumber(requestDto.getPhoneNumber())
                .emailVerified(false)
                .role(Member.Role.USER)
                .status(Member.Status.ACTIVE)
                .build();

        // 저장
        memberRepository.save(member);

        // 인증 코드 생성 및 메일 전송
        String code = emailVerificationService.createVerificationEntry(member);
        emailService.sendVerificationEmail(member.getEmail(), code);

    }

    public boolean isEmailDuplicate(String email) {
        return memberRepository.existsByEmail(email);
    }

    public MyProfileResponseDto getMyProfile() {
        // SecurityContext 에 세팅된 UserDetails 의 username 으로 Member 조회
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록된 회원이 아닙니다."));

        return new MyProfileResponseDto(
                member.getId(),
                member.getUsername(),
                member.getEmail(),
                member.getPhoneNumber(),
                member.getPassword() // 실제로는 비밀번호를 반환하지 않도록 주의
        );
    }
}
