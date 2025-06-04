package org.example.goormssd.usermanagementbackend.repository;

import org.example.goormssd.usermanagementbackend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

}