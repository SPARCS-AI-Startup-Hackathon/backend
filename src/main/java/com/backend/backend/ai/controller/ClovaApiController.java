package com.backend.backend.ai.controller;

import com.backend.backend.ai.dto.request.ChatMessage;
import com.backend.backend.ai.service.ClovaApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
public class ClovaApiController {
    private final ClovaApiService clovaApiService;
    @PostMapping("/test-clova")
    public ResponseEntity<?> testClovaApi(@RequestBody ChatMessage request) throws JsonProcessingException {
        return ResponseEntity.ok(clovaApiService.callChatCompletionApi(request));

    }
}


