package com.backend.backend.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "메시지가 없습니다.")
    private String message;
}
