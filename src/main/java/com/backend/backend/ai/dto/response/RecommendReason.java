package com.backend.backend.ai.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecommendReason {
    private String reason;

    @Builder
    public RecommendReason(String reason) {
        this.reason = reason;
    }
}
