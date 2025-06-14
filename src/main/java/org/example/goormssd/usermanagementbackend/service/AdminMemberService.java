package org.example.goormssd.usermanagementbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.domain.Member;
import org.example.goormssd.usermanagementbackend.dto.response.DashboardResponseDto;
import org.example.goormssd.usermanagementbackend.dto.response.MemberDetailResponseDto;
import org.example.goormssd.usermanagementbackend.dto.response.MemberListResponseDto;
import org.example.goormssd.usermanagementbackend.dto.response.MemberResponseDto;
import org.example.goormssd.usermanagementbackend.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberRepository memberRepository;

    public DashboardResponseDto getDashboard(
            int pageNum, int pageLimit, Boolean emailVerified, String status
    ) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageLimit, Sort.by("createdAt").descending());

        Member.Status statusEnum = null;
        if (status != null) {
            try {
                statusEnum = Member.Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("잘못된 status 값입니다. (active 또는 deleted)");
            }
        }

        Page<Member> page;

        if (emailVerified != null && statusEnum != null) {
            page = memberRepository.findByEmailVerifiedAndStatus(emailVerified, statusEnum, pageable);
        } else if (emailVerified != null) {
            page = memberRepository.findByEmailVerified(emailVerified, pageable);
        } else if (statusEnum != null) {
            page = memberRepository.findByStatus(statusEnum, pageable);
        } else {
            page = memberRepository.findAll(pageable);
        }

        List<MemberResponseDto> users = page.getContent().stream()
                .map(member -> new MemberResponseDto(
                        member.getId(),
                        member.getUsername(),
                        member.getEmail(),
                        member.getPhoneNumber(),
                        member.getRole().name(),
                        member.getStatus().name().toLowerCase(),
                        member.isEmailVerified(),
                        member.getCreatedAt()
                ))
                .collect(Collectors.toList());

        long sumUser     = memberRepository.count();
        long deletedUser = memberRepository.countByStatus(Member.Status.DELETED);

        return new DashboardResponseDto(
                sumUser,
                deletedUser,
                users,
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    public MemberListResponseDto getAllMembers(int pageNum, int pageLimit, String sortBy, String sortDir) {
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(pageNum - 1, pageLimit, Sort.by(direction, sortBy));
        Page<Member> page = memberRepository.findAll(pageable);

        List<MemberResponseDto> users = page.getContent().stream()
                .map(member -> new MemberResponseDto(
                        member.getId(),
                        member.getUsername(),
                        member.getEmail(),
                        member.getPhoneNumber(),
                        member.getRole().name(),
                        member.getStatus().name().toLowerCase(),
                        member.isEmailVerified(),
                        member.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new MemberListResponseDto(
                users,
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    public MemberListResponseDto getDeletedMembers(int pageNum, int pageLimit, String sortBy, String sortDir) {
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(pageNum - 1, pageLimit, Sort.by(direction, sortBy));

        Page<Member> page = memberRepository.findAllByStatus(Member.Status.DELETED, pageable);

        List<MemberResponseDto> users = page.getContent().stream()
                .map(member -> new MemberResponseDto(
                        member.getId(),
                        member.getUsername(),
                        member.getEmail(),
                        member.getPhoneNumber(),
                        member.getRole().name(),
                        member.getStatus().name().toLowerCase(),
                        member.isEmailVerified(),
                        member.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new MemberListResponseDto(
                users,
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    public MemberListResponseDto getUnverifiedEmailMembers(int pageNum, int pageLimit, String sortBy, String sortDir) {
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(pageNum - 1, pageLimit, Sort.by(direction, sortBy));

        Page<Member> page = memberRepository.findAllByEmailVerifiedFalse(pageable);

        List<MemberResponseDto> users = page.getContent().stream()
                .map(member -> new MemberResponseDto(
                        member.getId(),
                        member.getUsername(),
                        member.getEmail(),
                        member.getPhoneNumber(),
                        member.getRole().name(),
                        member.getStatus().name().toLowerCase(),
                        member.isEmailVerified(),
                        member.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new MemberListResponseDto(
                users,
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    public MemberDetailResponseDto getMemberDetailById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원이 존재하지 않습니다. id=" + id));
        return new MemberDetailResponseDto(
                member.getId(),
                member.getUsername(),
                member.getEmail(),
                member.getPhoneNumber(),
                member.getPassword()
        );
    }

    public MemberListResponseDto searchMembers(
            String email, String username,
            int pageNum, int pageLimit,
            String sortBy, String sortDir,
            Boolean emailVerified, String status
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(pageNum - 1, pageLimit, Sort.by(direction, sortBy));

        Page<Member> page;

        Member.Status statusEnum = null;
        if (status != null && !status.isBlank()) {
            try {
                statusEnum = Member.Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("잘못된 status 값입니다. (active 또는 deleted)");
            }
        }

        if (email != null && !email.isBlank()) {
            if (emailVerified != null && statusEnum != null) {
                page = memberRepository.findByEmailContainingIgnoreCaseAndEmailVerifiedAndStatus(email, emailVerified, statusEnum, pageable);
            } else if (emailVerified != null) {
                page = memberRepository.findByEmailContainingIgnoreCaseAndEmailVerified(email, emailVerified, pageable);
            } else if (statusEnum != null) {
                page = memberRepository.findByEmailContainingIgnoreCaseAndStatus(email, statusEnum, pageable);
            } else {
                page = memberRepository.findByEmailContainingIgnoreCase(email, pageable);
            }
        } else if (username != null && !username.isBlank()) {
            if (emailVerified != null && statusEnum != null) {
                page = memberRepository.findByUsernameContainingIgnoreCaseAndEmailVerifiedAndStatus(username, emailVerified, statusEnum, pageable);
            } else if (emailVerified != null) {
                page = memberRepository.findByUsernameContainingIgnoreCaseAndEmailVerified(username, emailVerified, pageable);
            } else if (statusEnum != null) {
                page = memberRepository.findByUsernameContainingIgnoreCaseAndStatus(username, statusEnum, pageable);
            } else {
                page = memberRepository.findByUsernameContainingIgnoreCase(username, pageable);
            }
        } else {
            if (emailVerified != null && statusEnum != null) {
                page = memberRepository.findByEmailVerifiedAndStatus(emailVerified, statusEnum, pageable);
            } else if (emailVerified != null) {
                page = memberRepository.findByEmailVerified(emailVerified, pageable);
            } else if (statusEnum != null) {
                page = memberRepository.findByStatus(statusEnum, pageable);
            } else {
                page = memberRepository.findAll(pageable);
            }
        }

        List<MemberResponseDto> users = page.getContent().stream()
                .map(member -> new MemberResponseDto(
                        member.getId(),
                        member.getUsername(),
                        member.getEmail(),
                        member.getPhoneNumber(),
                        member.getRole().name(),
                        member.getStatus().name().toLowerCase(),
                        member.isEmailVerified(),
                        member.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new MemberListResponseDto(
                users,
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }


    public void updateStatus(Long id, String status) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원이 존재하지 않습니다."));

        if ("active".equalsIgnoreCase(status)) {
            member.setStatus(Member.Status.ACTIVE);
        } else if ("deleted".equalsIgnoreCase(status)) {
            member.setStatus(Member.Status.DELETED);
        } else {
            throw new IllegalArgumentException("Invalid status value. Must be 'active' or 'deleted'.");
        }

        memberRepository.save(member);
    }
}