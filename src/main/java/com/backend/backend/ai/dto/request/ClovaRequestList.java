package com.backend.backend.ai.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * List로 변환
 */
@Getter
@Setter
@NoArgsConstructor
public class ClovaRequestList {
    private List<ClovaRequest> messages;

    @Builder
    public ClovaRequestList(List<ClovaRequest> messages) {
        this.messages = messages;
    }
}
