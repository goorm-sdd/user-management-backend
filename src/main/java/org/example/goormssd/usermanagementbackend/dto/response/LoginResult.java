package org.example.goormssd.usermanagementbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class LoginResult {
    private final String accessToken;
    private final String refreshToken;
    private final LoginUserDto user;
}

// 단순 읽기 전용 DTO 일시 레코드 클래스 사용도 좋은 선택
// 추후 필드 변경 예정이라면 레코드 클래스 사용은 지양하는 것이 좋음
// 무엇이 맞다보다는 팀의 코드 스타일과 협업 방식에 따라 결정하는 것이 중요함
// public record LoginResult(String accessToken, String refreshToken, LoginUserDto user) {
// }