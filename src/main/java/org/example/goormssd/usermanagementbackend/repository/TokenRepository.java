package org.example.goormssd.usermanagementbackend.repository;

import org.example.goormssd.usermanagementbackend.domain.Token;
import org.example.goormssd.usermanagementbackend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByAccessToken(String accessToken);
    Optional<Token> findByRefreshToken(String refreshToken);
//    void deleteAllByCustomer(Member member);
}