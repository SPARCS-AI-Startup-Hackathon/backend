package com.backend.backend.ai.controller;

import com.backend.backend.ai.dto.request.ChatMessage;
import com.backend.backend.ai.service.ClovaApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Tag(name = "클로바 대화API", description = "클로바를 사용해 사용자와 대화를 지원하는 API")
public class ClovaApiController {
    private final ClovaApiService clovaApiService;
    @GetMapping(value = "/first-question/{token}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "첫 질문 생성", description = "사용자의 정보를 기반으로 첫 질문을 생성해주는 API")
    public Flux<String> firstQuestion(@PathVariable String token) throws Exception {
        return clovaApiService.firstSetting(token);
    }
    @PostMapping("/first-question-answer")
    @Operation(summary = "사용자 첫 답변 회수", description = "사용자의 첫 답변을 추출하는 API")
    public void firstAnswer(@RequestBody @Valid ChatMessage message){
        clovaApiService.getFirstAnswer(message);
    }
}


