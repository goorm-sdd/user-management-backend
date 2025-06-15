package org.example.goormssd.usermanagementbackend.dto.admin.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "회원 상태는 필수 입력입니다.")
    @Pattern(regexp = "^(active|deleted)$", message = "회원 상태는 'active' 또는 'deleted'만 가능합니다.")
    @Schema(
            description = "회원 상태 값 (active 또는 deleted)",
            example = "deleted",
            allowableValues = {"active", "deleted"}
    )
    private String status;
}
