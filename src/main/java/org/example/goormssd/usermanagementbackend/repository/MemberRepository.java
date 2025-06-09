package org.example.goormssd.usermanagementbackend.repository;

import org.example.goormssd.usermanagementbackend.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByStatus(Member.Status status);

    Page<Member> findAllByStatus(Member.Status status, Pageable pageable);

    Page<Member> findAllByEmailVerifiedFalse(Pageable pageable);

    Page<Member> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    Page<Member> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

}