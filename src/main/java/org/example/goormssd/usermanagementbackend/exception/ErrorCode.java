package org.example.goormssd.usermanagementbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 400 BAD REQUEST
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력 값입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    PASSWORDS_DO_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    PASSWORD_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "기존 비밀번호와 동일한 비밀번호는 사용할 수 없습니다."),
    PHONE_NUMBER_ALREADY_USED(HttpStatus.BAD_REQUEST, "기존 전화번호와 동일합니다."),
    INVALID_STATUS_VALUE(HttpStatus.BAD_REQUEST, "상태 값은 'active' 또는 'deleted'만 가능합니다."),
    ALREADY_IN_TARGET_STATUS(HttpStatus.BAD_REQUEST, "이미 해당 상태입니다."),
    DELETED_CANNOT_BE_RECOVERED(HttpStatus.BAD_REQUEST, "탈퇴한 사용자는 복구할 수 없습니다."),
    USERNAME_NOT_MATCH(HttpStatus.BAD_REQUEST, "사용자 이름이 일치하지 않습니다."),
    VERIFICATION_CODE_INVALID(HttpStatus.BAD_REQUEST, "인증번호가 올바르지 않습니다."),
    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "인증번호가 만료되었습니다."),

    // 401 UNAUTHORIZED
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않았습니다."),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    ACCESS_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, "AccessToken이 필요합니다."),
    NOT_ADMIN(HttpStatus.UNAUTHORIZED, "관리자 권한이 없습니다."),

    // 403 FORBIDDEN
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "이메일 인증을 완료해야 로그인할 수 있습니다."),
    MEMBER_DELETED(HttpStatus.FORBIDDEN, "탈퇴된 회원입니다. 로그인이 불가능합니다."),

    // 404 NOT FOUND
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일로 등록된 사용자가 없습니다."),
    VERIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "인증 요청 기록이 없습니다."),

    // 409 CONFLICT
    EMAIL_ALREADY_VERIFIED(HttpStatus.CONFLICT, "이미 인증이 완료된 계정입니다."),

    // 500 INTERNAL SERVER ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    SMS_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SMS 발송 실패");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
