package org.example.goormssd.usermanagementbackend.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReauthResponseDto {
    private boolean valid;
    private String reauthToken;
}