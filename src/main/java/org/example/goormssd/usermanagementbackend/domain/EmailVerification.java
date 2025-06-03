package org.example.goormssd.usermanagementbackend.domain;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 인증 대상 이메일 (Member와 1:1 매핑)
    @Column(nullable = false, unique = true)
    private String email;

    // 이메일 인증용 고유 코드 (UUID 등)
    @Column(nullable = false, unique = true)
    private String code;

    // 만료 시간
    @Column(nullable = false)
    private LocalDateTime expiresAt;

}
