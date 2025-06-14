package org.example.goormssd.usermanagementbackend.dto.response;

import lombok.*;

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
}
