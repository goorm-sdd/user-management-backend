package org.example.goormssd.usermanagementbackend.repository;

import org.example.goormssd.usermanagementbackend.domain.Token;
import org.example.goormssd.usermanagementbackend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
//    Optional<Token> findByAccessToken(String accessToken);

    // RefreshToken을 통해 유효한 토큰만 찾기 (deletedAt이 null인 경우)
    Optional<Token> findByRefreshTokenAndDeletedAtIsNull(String refreshToken);

//    void deleteAllByCustomer(Member member);
}