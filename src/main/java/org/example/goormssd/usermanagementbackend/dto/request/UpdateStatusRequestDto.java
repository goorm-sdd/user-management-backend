package org.example.goormssd.usermanagementbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 상태 변경 요청 DTO")
public class UpdateStatusRequestDto {

    @Schema(
            description = "회원 상태 값 (active 또는 deleted)",
            example = "deleted",
            allowableValues = {"active", "deleted"}
    )
    private String status; // "active" or "deleted"
}
