package org.example.goormssd.usermanagementbackend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String password;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "role_id", nullable = false)
//    private Role role;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_status_id", nullable = false)
//    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private Boolean emailVerified = false;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private LocalDateTime deletedAt;

    public enum Role {
        USER,
        ADMIN;

        public boolean isAdmin() {
            return this == ADMIN;
        }

        public boolean isUser() {
            return this == USER;
        }
    }
    public enum Status {
        ACTIVE,
        DELETED;

        public boolean isActive() {
            return this == ACTIVE;
        }

        public boolean isDeleted() {
            return this == DELETED;
        }
    }

}