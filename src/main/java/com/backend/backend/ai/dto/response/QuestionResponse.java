package com.backend.backend.ai.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuestionResponse {
    private String question;

    @Builder
    public QuestionResponse(String question) {
        this.question = question;
    }
}
