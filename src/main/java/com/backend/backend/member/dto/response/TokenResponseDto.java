package com.backend.backend.member.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private long accessTokenValidationTime;
    private long refreshTokenValidationTime;
}