package com.backend.backend.ai.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatResponse {
    private String sender;
    private String content;
    @Builder
    public ChatResponse(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }
}
