package org.example.goormssd.usermanagementbackend.service.member;

import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.domain.Member;
import org.example.goormssd.usermanagementbackend.domain.PhoneVerification;
import org.example.goormssd.usermanagementbackend.dto.member.request.UpdatePasswordRequestDto;
import org.example.goormssd.usermanagementbackend.dto.member.response.MyProfileResponseDto;
import org.example.goormssd.usermanagementbackend.exception.ErrorCode;
import org.example.goormssd.usermanagementbackend.exception.GlobalException;
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
    private final PhoneVerificationRepository phoneVerificationRepository;



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
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

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
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        if (!dto.getNewPassword().equals(dto.getNewPasswordCheck())) {
            throw new GlobalException(ErrorCode.PASSWORDS_DO_NOT_MATCH);
        }

        if (passwordEncoder.matches(dto.getNewPassword(), member.getPassword())) {
            throw new GlobalException(ErrorCode.PASSWORD_SAME_AS_OLD);
        }

        String encoded = passwordEncoder.encode(dto.getNewPassword());
        member.updatePassword(encoded);  // 영속 상태에서 수행 → dirty checking OK
    }


    @Transactional
    public void updatePhoneNumber(Member member, String newPhoneNumber) {
        if (member.getPhoneNumber().equals(newPhoneNumber)) {
            throw new GlobalException(ErrorCode.PHONE_NUMBER_ALREADY_USED);
        }

        PhoneVerification verification = phoneVerificationRepository.findById(newPhoneNumber)
                .orElseThrow(() -> new GlobalException(ErrorCode.VERIFICATION_NOT_FOUND));

        if (!verification.isVerified()) {
            throw new GlobalException(ErrorCode.VERIFICATION_CODE_INVALID);
        }

        // 영속 객체로 다시 조회
        Member persistedMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        persistedMember.updatePhoneNumber(newPhoneNumber);
        persistedMember.setModifiedAt(LocalDateTime.now()); // 트리거 역할

        phoneVerificationRepository.delete(verification);
    }

    @Transactional
    public void updateStatus(Member member, String status) {
        Member.Status targetStatus;
        try {
            targetStatus = Member.Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new GlobalException(ErrorCode.INVALID_STATUS_VALUE);
        }

        if (member.getStatus() == targetStatus) {
            throw new GlobalException(ErrorCode.ALREADY_IN_TARGET_STATUS);
        }

        if (member.getStatus() == Member.Status.DELETED && targetStatus == Member.Status.ACTIVE) {
            throw new GlobalException(ErrorCode.DELETED_CANNOT_BE_RECOVERED);
        }

        Member persisted = memberRepository.findById(member.getId())
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        persisted.setStatus(targetStatus);
        persisted.setModifiedAt(LocalDateTime.now());

    }
}
