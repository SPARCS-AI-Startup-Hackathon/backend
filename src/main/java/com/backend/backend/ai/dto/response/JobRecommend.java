package com.backend.backend.ai.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JobRecommend {
    private String recommendJob;

    @Builder
    public JobRecommend(String recommendJob) {
        this.recommendJob = recommendJob;
    }
}
