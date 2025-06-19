package org.example.goormssd.usermanagementbackend.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.goormssd.usermanagementbackend.dto.auth.requset.*;
import org.example.goormssd.usermanagementbackend.dto.auth.response.LoginResponseDto;
import org.example.goormssd.usermanagementbackend.dto.auth.response.LoginResult;
import org.example.goormssd.usermanagementbackend.dto.auth.response.RefreshTokenDto;
import org.example.goormssd.usermanagementbackend.dto.common.ApiResponseDto;
import org.example.goormssd.usermanagementbackend.dto.auth.response.FindEmailResponseDto;
import org.example.goormssd.usermanagementbackend.service.auth.AuthService;
import org.example.goormssd.usermanagementbackend.security.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "https://user-management-frontend-ruby.vercel.app/"
        },
        allowCredentials = "true"
)
@Slf4j
@RestController
@RequestMapping("/api") // API 버전 관리
@RequiredArgsConstructor // Lombok, final 이나 @NonNull 필드에 대해 생성자 자동 생성
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // @RequiredArgsConstructor를 사용하면 @Autowired가 필요없음
    // 가독성과 코드 간결성을 위해 @RequiredArgsConstructor를 사용하는 뱡향이 나아보임
    //    @Autowired
    //    public AuthController(AuthService authService) {
    //        this.authService = authService;
    //    }


    @Operation(
            summary = "회원가입",
            description = "이메일, 비밀번호, 사용자 이름 등의 정보를 입력받아 회원가입을 처리합니다." +
                    "회원가입 전 반드시 휴대폰 인증을 완료해야 하며, 이메일 인증 링크도 함께 발송됩니다."
    )
    @Tag(name = "인증 API", description = "회원가입, 로그인, 인증 관련 API입니다.")
    @PostMapping("/auth/signup")
    public ResponseEntity<String> signup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 요청 데이터", required = true
            )
            @Valid @RequestBody SignupRequestDto requestDto
    ) {
        // 회원 저장 및 이메일 인증 처리까지 서비스에서 수행
        authService.signup(requestDto);
        return ResponseEntity.ok("회원가입이 완료되었습니다. 이메일을 확인해주세요.");
    }

    @Operation(
            summary = "이메일 중복 확인",
            description = "입력한 이메일이 이미 가입된 이메일인지 확인합니다."
    )
    @Tag(name = "인증 API", description = "회원가입, 로그인, 인증 관련 API입니다.")
    @PostMapping("/auth/email")
    public ResponseEntity<Map<String, Object>> checkEmailDuplicate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "이메일 중복 확인 요청", required = true
            )
            @RequestBody @Valid EmailCheckRequestDto requestDto
    ) {
        boolean exists = authService.isEmailDuplicate(requestDto.getEmail());

        Map<String, Object> response = new HashMap<>();
        if (exists) {
            response.put("status", 409);
            response.put("message", "중복된 이메일입니다.");
            return ResponseEntity.status(409).body(response);
        } else {
            response.put("status", 200);
            response.put("message", "사용가능한 이메일입니다.");
            return ResponseEntity.ok(response);
        }
    }

    @Operation(
            summary = "사용자 로그인",
            description = "이메일과 비밀번호를 통해 로그인합니다. AccessToken은 응답 바디에, RefreshToken은 쿠키에 저장됩니다."
    )
    @Tag(name = "인증 API", description = "회원가입, 로그인, 인증 관련 API입니다.")
    @PostMapping("/auth/signin")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로그인 요청 정보 (이메일, 비밀번호)", required = true)
            @RequestBody LoginRequestDto loginRequest,
            @Parameter(hidden = true) HttpServletResponse response) {

        // var 사용을 통해 타입 추론을 활용할 수 있지만, 명시적인 타입 선언이 가독성에 더 좋을 수 있음
        // 팀 프로젝트에서의 중요한 로직에서는 var 사용을 지양하고, 타입을 명시하는게 좋을 수 있음
        // var LogingResult result = authService.loginWithUserInfo(loginRequest);
        LoginResult result = authService.loginWithUserInfo(loginRequest);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 7)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

//
//        LoginResponseDto body = new LoginResponseDto(result.getAccessToken(), result.getUser());
//        ApiResponseDto<LoginResponseDto> apiResponse = new ApiResponseDto<>(200, "Login successful", body);
//
//        return ResponseEntity.ok(apiResponse);

        LoginResponseDto responseBody = new LoginResponseDto(result.getAccessToken(), result.getUser());

        return ResponseEntity.ok(
                new ApiResponseDto<>(200, "Login successful", responseBody)
        );
    }

    @Operation(
            summary = "사용자 로그아웃",
            description = "AccessToken 검증 후 RefreshToken을 삭제하고, 쿠키도 만료시킵니다.",
            security = @SecurityRequirement(name = "AccessToken")
    )
    @Tag(name = "인증 API", description = "회원가입, 로그인, 인증 관련 API입니다.")
    @PostMapping("/signout")
    public ResponseEntity<ApiResponseDto<Void>> logout(
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(hidden = true) HttpServletResponse response
    ) {

//        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return ResponseEntity.status(401).body(new ApiResponseDto<>(401, "Unauthorized", null));
//        }

// 변수명 accessToken을 사용하여 가독성을 높임
//        String token = authHeader.substring(7);
//        authService.logout(token);
//        String accessToken = authHeader.substring(7);
//        authService.logout(accessToken);

        // AccessToken 검증
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(401, "AccessToken이 필요합니다.", null));
        }
        String accessToken = authHeader.substring(7);
        if (!jwtUtil.validateAccessToken(accessToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(401, "AccessToken이 유효하지 않습니다.", null));
        }

        // 디버깅용: 모든 쿠키 로깅
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                log.debug("[쿠키] {} = {}", c.getName(), c.getValue());
            }
        } else {
            log.debug("[쿠키] 없음");
        }

        // 쿠키에서 refreshToken 추출
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equalsIgnoreCase(cookie.getName().trim())) {
                    refreshToken = cookie.getValue();
                    log.debug("[로그아웃] refreshToken 추출: {}", refreshToken);
                    break;
                }
            }
        }

        // refreshToken 있으면 서비스에 전달하여 DB 삭제
        if (refreshToken != null && !refreshToken.isBlank()) {
            authService.logout(refreshToken);
            log.info("[로그아웃] 서비스에 refreshToken 전달 완료");
        } else {
            log.warn("[로그아웃] refreshToken이 전달되지 않음");
        }

        // 클라이언트에 저장된 RefreshToken 제거
        // api 명세에 써있지 않은 부분 로그아웃 시에는 RefreshToken을 제거 해야할 필요가 있음
        ResponseCookie expiredCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());

        return ResponseEntity.ok(new ApiResponseDto<>(200, "Logout successful.", null));
    }

    @Operation(
            summary = "토큰 재발급",
            description = "쿠키에 담긴 RefreshToken을 사용하여 AccessToken을 재발급합니다."
    )
    @Tag(name = "인증 API", description = "회원가입, 로그인, 인증 관련 API입니다.")
    @PostMapping("/auth/token/refresh")
    public ResponseEntity<ApiResponseDto<RefreshTokenDto>> refreshToken(
            @Parameter(hidden = true) HttpServletRequest request) {

        // 쿠키에서 refreshToken 추출
        String refreshToken = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(c -> "refreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "refreshToken이 전달되지 않음"
                ));

        // DB에서 유효 여부 확인
        if (!authService.isValidRefreshToken(refreshToken)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 refreshToken"
            );
        }

        // 새 AccessToken 발급
        String email = jwtUtil.extractEmail(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(email);


        RefreshTokenDto body = new RefreshTokenDto(newAccessToken);
        ApiResponseDto<RefreshTokenDto> apiResponse =
                new ApiResponseDto<>(200, "Token refreshed", body);

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
            summary = "이메일(아이디) 찾기",
            description = "이름과 전화번호를 기반으로 등록된 이메일을 조회합니다."
    )
    @Tag(name = "인증 API", description = "회원가입, 로그인, 인증 관련 API입니다.")
    @PostMapping("/auth/find/email")
    public ResponseEntity<ApiResponseDto<FindEmailResponseDto>> findEmail(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "이름과 전화번호", required = true)
            @RequestBody FindEmailRequestDto request) {
        String email = authService.findEmailByUsernameAndPhone(request);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "Email (ID) retrieved successfully.", new FindEmailResponseDto(email)));
    }

    @Operation(
            summary = "비밀번호 재설정",
            description = "이메일과 이름, 전화번호를 통해 본인 확인 후 임시 비밀번호를 이메일로 발송합니다."
    )
    @Tag(name = "인증 API", description = "회원가입, 로그인, 인증 관련 API입니다.")
    @PostMapping("/auth/find/password")
    public ResponseEntity<ApiResponseDto<Void>> resetPassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "이름, 이메일, 전화번호", required = true)
            @RequestBody FindPasswordRequestDto request) {
        authService.resetPasswordAndSendEmail(request);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "Temporary password has been sent via email.", null));
    }


}