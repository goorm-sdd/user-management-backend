package org.example.goormssd.usermanagementbackend.dto.member.response;

import lombok.*;
import org.example.goormssd.usermanagementbackend.domain.Member;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String role;          // USER or ADMIN
    private String status;        // ACTIVE or DELETED
    private boolean emailVerified;
    private LocalDateTime createdAt;

    public static MemberResponseDto from(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .role(member.getRole().name())
                .status(member.getStatus().name().toLowerCase())
                .emailVerified(member.isEmailVerified())
                .createdAt(member.getCreatedAt())
                .build();
    }

}
