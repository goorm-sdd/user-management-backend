package org.example.goormssd.usermanagementbackend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Entity
// 테이블 이름은 DB에 따라 예약어일 경우가 있어 접두사 tbl_를 붙임
@Table(name = "tbl_token")
// Soft Delete 관리
// @SQLDelete 애노테이션을 활용하면 자동으로 deletedAt IS NULL 조건을 걸어줄 수 있음
@SQLDelete(sql = "UPDATE tbl_token SET deleted_at = CURRENT_TIMESTAMP WHERE token_id = ?")
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

    // 필드명 통일 user -> member
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // private String accessToken;
    // AccessToken은 별도로 저장하지 않고, JWT로 생성하여 클라이언트에 전달
    // refreshToken민 DB에 저장하여 관리
    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refreshToken;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}