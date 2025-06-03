package org.example.goormssd.usermanagementbackend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerification {

    @Id
    @Column(nullable = false)
    private String email; // 이메일 주소 (PK)

    @Column(nullable = false)
    private String code; // 인증 코드 (UUID 등)

    @Column(nullable = false)
    private LocalDateTime expiresAt; // 인증 유효 시간

    @Column(nullable = false)
    private boolean verified = false; // 인증 완료 여부
}
