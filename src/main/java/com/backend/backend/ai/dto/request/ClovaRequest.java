package com.backend.backend.ai.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 요청 바디 내부
 * 1. 그 전의 대화 가져오기
 * 2. 사용자의 응답 대화 가져오기
 */
@Getter
@Setter
@NoArgsConstructor
public class ClovaRequest {
    private String content;
    private String role;

    @Builder
    public ClovaRequest(String content, String role) {
        this.content = content;
        this.role = role;
    }
}
