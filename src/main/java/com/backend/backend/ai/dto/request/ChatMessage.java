package com.backend.backend.ai.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자의 응답
 */
@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {
    private String message;
}
