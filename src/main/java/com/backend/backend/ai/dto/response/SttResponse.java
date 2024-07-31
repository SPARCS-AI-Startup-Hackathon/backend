package com.backend.backend.ai.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SttResponse {
    private String text;
    @Builder
    public SttResponse(String text) {
        this.text = text;
    }
}
