package com.backend.backend.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRequest {
    @Schema(description = "사용자 이메일", nullable = false, example = "mr6208@naver.com")
    @Email(message = "이메일 형식이여야 합니다.")
    private String email;
    @Schema(description = "사용자 비밀번호", nullable = false, example = "asdf1020")
    @NotBlank
    private String password;
    @Schema(description = "사용자 이름", nullable = false, example = "김덕배")
    @NotBlank
    private String name;
    @Schema(description = "사용자 나이", nullable = false, example = "68")
    @NotNull
    private Integer age;
    @Schema(description = "사용자 전화번호", nullable = false, example = "01046666208")
    @NotBlank
    private String tel;
}
