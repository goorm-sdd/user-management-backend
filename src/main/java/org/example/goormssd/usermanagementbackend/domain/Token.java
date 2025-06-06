package org.example.goormssd.usermanagementbackend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="token_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member user;

//    private String accessToken;
    // AccessToken은 별도로 저장하지 않고, JWT로 생성하여 클라이언트에 전달
    // refreshToken민 DB에 저장하여 관리
    @Column(nullable = false, unique = true)
    private String refreshToken;

    private LocalDateTime deletedAt;
}