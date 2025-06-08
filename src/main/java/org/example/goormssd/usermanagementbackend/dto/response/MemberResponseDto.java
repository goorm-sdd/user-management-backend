package org.example.goormssd.usermanagementbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
