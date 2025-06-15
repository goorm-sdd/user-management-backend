package org.example.goormssd.usermanagementbackend.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReauthResponseDto {
    private boolean valid;
    private String reauthToken;  // 재인증용 단기 JWT
    private String message;
}