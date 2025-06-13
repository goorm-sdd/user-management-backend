package org.example.goormssd.usermanagementbackend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "phone_verification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneVerification {

    @Id
    private String phoneNumber; // 전화번호가 PK

    @Column(nullable = false)
    private String code; // 인증 코드

    @Column(nullable = false)
    private LocalDateTime expiresAt; // 만료 시간

    @Column(nullable = false)
    private Boolean verified = false;
}
