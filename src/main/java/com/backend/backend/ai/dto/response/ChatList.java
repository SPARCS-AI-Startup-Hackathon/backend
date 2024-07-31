package com.backend.backend.ai.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatList {
    private List<ChatResponse> chatResponses = new ArrayList<>();

    @Builder
    public ChatList(List<ChatResponse> chatResponses) {
        this.chatResponses = chatResponses;
    }
}
