package org.example.goormssd.usermanagementbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.domain.Member;
import org.example.goormssd.usermanagementbackend.domain.PhoneVerification;
import org.example.goormssd.usermanagementbackend.dto.request.SignupRequestDto;
import org.example.goormssd.usermanagementbackend.dto.request.UpdatePasswordRequestDto;
import org.example.goormssd.usermanagementbackend.dto.response.MyProfileResponseDto;
import org.example.goormssd.usermanagementbackend.repository.MemberRepository;
import org.example.goormssd.usermanagementbackend.repository.PhoneVerificationRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final EmailService emailService;
    private final PhoneVerificationRepository phoneVerificationRepository;

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


    @Transactional
    public void updatePassword(Member detachedMember, UpdatePasswordRequestDto dto) {
        Member member = memberRepository.findById(detachedMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if (!dto.getNewPassword().equals(dto.getNewPasswordCheck())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        if (passwordEncoder.matches(dto.getNewPassword(), member.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호와 동일한 비밀번호를 사용할 수 없습니다.");
        }

        String encoded = passwordEncoder.encode(dto.getNewPassword());
        member.updatePassword(encoded);  // 영속 상태에서 수행 → dirty checking OK
    }


    @Transactional
    public void updatePhoneNumber(Member member, String newPhoneNumber) {
        if (member.getPhoneNumber().equals(newPhoneNumber)) {
            throw new IllegalArgumentException("기존 전화번호와 동일합니다.");
        }

        PhoneVerification verification = phoneVerificationRepository.findById(newPhoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("전화번호 인증 기록이 없습니다."));

        if (!verification.isVerified()) {
            throw new IllegalArgumentException("전화번호 인증이 완료되지 않았습니다.");
        }

        // 영속 객체로 다시 조회
        Member persistedMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        persistedMember.updatePhoneNumber(newPhoneNumber);
        persistedMember.setModifiedAt(LocalDateTime.now()); // 트리거 역할
    }
}
