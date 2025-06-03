Spring Security + JWT 기반 인증 시스템의 파일 구성 순서 및 로직 흐름 정리

## 1. Member.java (도메인)
```java
   @Entity
   public class Member {
   private String email;
   private String password;
   private Role role;    // USER, ADMIN
   private Status status; // ACTIVE, DELETED
   }
```
    사용자 정보를 담는 엔티티 클래스
    - `email`: 사용자 이메일 (로그인 ID)
    - `password`: 암호화된 비밀번호
    - `role`: 사용자 권한 (USER, ADMIN)
    - `status`: 사용자 상태 (ACTIVE, DELETED)

    이 엔티티는 JPA를 통해 데이터베이스에 매핑되어 CRUD 작업을 수행함

## 2. MemberRepository.java
```java
    public interface MemberRepository extends JpaRepository<Member, Long> {
        Optional<Member> findByEmail(String email);
    }
```
    JPA를 사용하여 Member 엔티티에 대한 CRUD 및 조회 기능 제공
    - `findByEmail`: 이메일로 사용자를 조회하는 메서드
    - Optional<Member>를 반환하여 사용자 존재 여부를 명확히 처리

## 3. CustomUserDetailsService.java
```java
    public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String email) { ... }
}
```
    Spring Security의 UserDetailsService 구현체
    - `loadUserByUsername`: 이메일로 사용자를 조회하고 UserDetails 객체 반환
    - MemberRepository를 통해 DB에서 사용자 정보 조회
    - 조회된 사용자의 Role과 Status에 따라 권한 설정

    이 서비스는 JWT 인증 필터에서 사용되어 인증 정보를 SecurityContext에 저장함

## 4. JwtUtil.java
```java
    public class JwtUtil {
        public String generateToken(String email) { ... }
        public boolean validateToken(String token) { ... }
        public String extractEmail(String token) { ... }
}
```
    JWT 토큰 생성 및 검증을 담당하는 유틸리티 클래스
    - `generateToken`: 이메일을 기반으로 JWT 토큰 생성
    - `validateToken`: 토큰의 유효성 검사
    - `extractEmail`: 토큰에서 이메일(subject) 추출

    JWT 토큰은 Base64로 인코딩된 시크릿 키를 사용하여 서명됨
    이 시크릿 키는 application-local.properties에 설정됨

## 5. JwtAuthenticationFilter.java
```java
    @Component
    public class JwtAuthenticationFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(...) { ... }
    }
```
    HTTP 요청에서 JWT 토큰을 추출하고 검증하는 필터
    - `doFilterInternal`: 요청의 Authorization 헤더에서 토큰을 추출하고 유효성 검사 후 인증 객체를 설정
    - 검증 성공 시 SecurityContext에 인증 정보를 저장하여 이후 컨트롤러에서 @AuthenticationPrincipal로 접근 가능

## 6. SecurityConfig.java
```java
    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) { ... }
    }
```
    Spring Security 설정 클래스
    - CSRF 보호, 세션 관리, URL별 접근 권한 설정 등 전반적인 보안 설정 담당
    - JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 등록하여 요청마다 JWT 인증 수행

## 7. application-local.properties
```properties
    spring.h2.console.enabled=true
    jwt.secret=Base64로 인코딩된 시크릿 키
```
    H2 콘솔 활성화 및 JWT 시크릿 키 설정
    - `spring.h2.console.enabled`: H2 데이터베이스 콘솔 활성화
    - `jwt.secret`: JWT 토큰 서명에 사용되는 Base64로 인코딩된 시크릿 키
 
## 전체 인증 로직 흐름

[요청 → JwtAuthenticationFilter → JwtUtil로 토큰 검증] →
[토큰에서 이메일 추출 → CustomUserDetailsService로 사용자 조회] →
[Spring Security에 인증 정보 저장] →
[컨트롤러에서는 @AuthenticationPrincipal 등으로 사용자 정보 접근 가능]

## 전체 인증 흐름 설명
1. **요청**: 클라이언트가 JWT 토큰을 포함한 요청을 서버에 보냄.
2. **JwtAuthenticationFilter**: 요청을 가로채 JWT 토큰을 추출하고 검증함.
3. **JwtUtil**: 토큰의 유효성을 검사하고, 유효한 경우 토큰에서 이메일을 추출함.
4. **CustomUserDetailsService**: 추출된 이메일을 사용하여 데이터베이스에서 사용자를 조회함.
5. **Spring Security**: 조회된 사용자 정보를 SecurityContext에 저장하여 이후 요청에서 인증 정보에 접근할 수 있도록 함.
6. **컨트롤러**: @AuthenticationPrincipal 등을 통해 인증된 사용자 정보를 쉽게 접근할 수 있음.


## 아직 구현되지 않은 부분
| 기능               | 설명                                            |
| ---------------- | --------------------------------------------- |
| 회원가입 API         | 사용자를 DB에 저장하고 비밀번호 암호화                        |
| 로그인 API          | 사용자 인증 후 AccessToken (나중엔 RefreshToken 포함) 발급 |
| 토큰 응답 DTO        | 로그인 성공 시 토큰을 반환할 구조                           |
| Refresh Token 처리 | Access Token 만료 시 재발급 로직 추가                   |
